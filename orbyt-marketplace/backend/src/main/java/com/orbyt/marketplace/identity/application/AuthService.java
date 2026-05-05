package com.orbyt.marketplace.identity.application;

import com.orbyt.marketplace.config.JwtService;
import com.orbyt.marketplace.identity.domain.RefreshToken;
import com.orbyt.marketplace.identity.domain.User;
import com.orbyt.marketplace.identity.api.dto.AuthResponse;
import com.orbyt.marketplace.identity.api.dto.LoginRequest;
import com.orbyt.marketplace.identity.api.dto.RegisterRequest;
import com.orbyt.marketplace.identity.repository.RefreshTokenRepository;
import com.orbyt.marketplace.identity.repository.UserRepository;
import com.orbyt.marketplace.platform.domain.Tenant;
import com.orbyt.marketplace.platform.repository.TenantRepository;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RbacProvisioningService rbacProvisioningService;

    @Value("${app.security.refresh-expiration-seconds}")
    private long refreshExpirationSeconds;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        Tenant tenant = findTenant(request.tenantSlug());
        userRepository.findByTenantIdAndEmailIgnoreCase(tenant.getId(), request.email())
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
                });

        User user = new User();
        user.setTenantId(tenant.getId());
        user.setEmail(request.email().toLowerCase(Locale.ROOT));
        user.setFullName(request.fullName());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setStatus("ACTIVE");
        User saved = userRepository.saveAndFlush(user);
        rbacProvisioningService.assignBuyerRole(tenant.getId(), saved.getId());
        return issueTokens(saved);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Tenant tenant = findTenant(request.tenantSlug());
        User user = userRepository.findByTenantIdAndEmailIgnoreCase(tenant.getId(), request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        return issueTokens(user);
    }

    @Transactional
    public AuthResponse refresh(String refreshToken) {
        RefreshToken stored = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));
        if (stored.isRevoked() || stored.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired");
        }

        User user = userRepository.findById(stored.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);
        return issueTokens(user);
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
    }

    private AuthResponse issueTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getTenantId(), user.getEmail());
        String refreshToken = generateAndStoreRefreshToken(user);
        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtService.getExpirationSeconds()
        );
    }

    private String generateAndStoreRefreshToken(User user) {
        RefreshToken token = new RefreshToken();
        token.setTenantId(user.getTenantId());
        token.setUserId(user.getId());
        token.setToken(UUID.randomUUID() + "." + UUID.randomUUID());
        token.setExpiresAt(OffsetDateTime.now().plusSeconds(refreshExpirationSeconds));
        refreshTokenRepository.save(token);
        return token.getToken();
    }

    private Tenant findTenant(String slug) {
        return tenantRepository.findBySlugIgnoreCase(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found"));
    }
}
