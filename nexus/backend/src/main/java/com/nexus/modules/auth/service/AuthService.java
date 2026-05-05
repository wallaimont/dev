package com.nexus.modules.auth.service;

import com.nexus.modules.auth.dto.*;
import com.nexus.modules.user.domain.User;
import com.nexus.modules.user.domain.UserProfile;
import com.nexus.modules.user.domain.UserStatus;
import com.nexus.modules.user.repository.UserRepository;
import com.nexus.modules.auth.domain.RefreshToken;
import com.nexus.modules.auth.repository.RefreshTokenRepository;
import com.nexus.shared.security.JwtService;
import com.nexus.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final TenantResolverService tenantResolver;
    private final EmailVerificationService emailVerificationService;
    private final AuditService auditService;

    public AuthResponse register(RegisterRequest req, String tenantSlug) {
        var tenant = tenantResolver.resolveBySlug(tenantSlug);

        if (userRepository.existsByTenantIdAndEmail(tenant.getId(), req.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered");
        }

        var user = new User();
        user.setTenantId(tenant.getId());
        user.setEmail(req.getEmail().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setStatus(UserStatus.PENDING_VERIFICATION);
        user = userRepository.save(user);

        // Create profile
        var profile = new UserProfile();
        profile.setUserId(user.getId());
        profile.setTenantId(tenant.getId());
        profile.setFirstName(req.getFirstName());
        profile.setLastName(req.getLastName());
        profile.setPhone(req.getPhone());
        // profileRepository.save(profile); — injected similarly

        // Assign default BUYER role
        userRepository.assignRole(user.getId(), "BUYER");

        // Send verification email
        emailVerificationService.sendVerification(user);

        log.info("New user registered: {} tenant={}", user.getEmail(), tenant.getSlug());

        return buildAuthResponse(user, List.of("BUYER"), tenant.getId());
    }

    public AuthResponse login(LoginRequest req, String tenantSlug, String ipAddress) {
        var tenant = tenantResolver.resolveBySlug(tenantSlug);

        var user = userRepository.findByTenantIdAndEmail(tenant.getId(), req.getEmail().toLowerCase())
            .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            auditService.recordFailedLogin(user.getId(), ipAddress);
            throw new BadCredentialsException("Invalid credentials");
        }

        if (user.getStatus() == UserStatus.SUSPENDED || user.getStatus() == UserStatus.BANNED) {
            throw new AccountSuspendedException("Account is " + user.getStatus());
        }

        var roles = userRepository.findRolesByUserId(user.getId());
        var response = buildAuthResponse(user, roles, tenant.getId());

        // Persist refresh token (hashed)
        var rawRefreshToken = response.getRefreshToken();
        var rt = new RefreshToken();
        rt.setUserId(user.getId());
        rt.setTokenHash(hashToken(rawRefreshToken));
        rt.setDeviceInfo(req.getDeviceInfo());
        rt.setIpAddress(ipAddress);
        rt.setExpiresAt(Instant.now().plusMillis(jwtService.getRefreshTokenTtlMs()));
        refreshTokenRepository.save(rt);

        auditService.recordSuccessfulLogin(user.getId(), ipAddress);
        return response;
    }

    public AuthResponse refreshToken(RefreshRequest req) {
        String tokenHash = hashToken(req.getRefreshToken());
        var rt = refreshTokenRepository.findByTokenHash(tokenHash)
            .orElseThrow(() -> new InvalidTokenException("Invalid or expired refresh token"));

        if (rt.isRevoked() || rt.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidTokenException("Refresh token expired or revoked");
        }

        var user = userRepository.findById(rt.getUserId())
            .orElseThrow(() -> new InvalidTokenException("User not found"));

        var roles = userRepository.findRolesByUserId(user.getId());

        // Rotate refresh token
        rt.setRevoked(true);
        rt.setRevokedAt(Instant.now());
        refreshTokenRepository.save(rt);

        var response = buildAuthResponse(user, roles, user.getTenantId());

        var newRt = new RefreshToken();
        newRt.setUserId(user.getId());
        newRt.setTokenHash(hashToken(response.getRefreshToken()));
        newRt.setExpiresAt(Instant.now().plusMillis(jwtService.getRefreshTokenTtlMs()));
        refreshTokenRepository.save(newRt);

        return response;
    }

    public void logout(String rawToken) {
        refreshTokenRepository.findByTokenHash(hashToken(rawToken))
            .ifPresent(rt -> {
                rt.setRevoked(true);
                rt.setRevokedAt(Instant.now());
                refreshTokenRepository.save(rt);
            });
    }

    public void verifyEmail(String token) {
        emailVerificationService.verify(token);
    }

    public void forgotPassword(String email, String tenantSlug) {
        var tenant = tenantResolver.resolveBySlug(tenantSlug);
        userRepository.findByTenantIdAndEmail(tenant.getId(), email.toLowerCase())
            .ifPresent(emailVerificationService::sendPasswordReset);
        // Always return 202 to prevent email enumeration
    }

    public void resetPassword(ResetPasswordRequest req) {
        var reset = emailVerificationService.validatePasswordResetToken(req.getToken());
        var user = userRepository.findById(reset.getUserId())
            .orElseThrow(() -> new InvalidTokenException("User not found"));
        user.setPasswordHash(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
        emailVerificationService.markResetUsed(reset);
        // Revoke all refresh tokens for security
        refreshTokenRepository.revokeAllForUser(user.getId());
    }

    // --------------------------------------------------------
    private AuthResponse buildAuthResponse(User user, List<String> roles, UUID tenantId) {
        String accessToken  = jwtService.generateAccessToken(user.getId(), tenantId, user.getEmail(), roles);
        String refreshToken = jwtService.generateRefreshToken(user.getId());

        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(900) // 15 min
            .userId(user.getId())
            .tenantId(tenantId)
            .email(user.getEmail())
            .roles(roles)
            .build();
    }

    private String hashToken(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash token", e);
        }
    }

    // ---- Custom exceptions -----------------------------------
    public static class EmailAlreadyExistsException extends RuntimeException {
        public EmailAlreadyExistsException(String msg) { super(msg); }
    }
    public static class AccountSuspendedException extends RuntimeException {
        public AccountSuspendedException(String msg) { super(msg); }
    }
    public static class InvalidTokenException extends RuntimeException {
        public InvalidTokenException(String msg) { super(msg); }
    }
}
