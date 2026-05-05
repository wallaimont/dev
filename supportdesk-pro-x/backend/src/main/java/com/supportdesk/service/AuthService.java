package com.supportdesk.service;

import com.supportdesk.config.AppProperties;
import com.supportdesk.domain.entity.*;
import com.supportdesk.dto.request.*;
import com.supportdesk.dto.response.*;
import com.supportdesk.exception.*;
import com.supportdesk.repository.*;
import com.supportdesk.security.JwtTokenProvider;
import com.supportdesk.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
        private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final AppProperties appProperties;

    @Transactional
    public AuthResponse login(LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        return buildTokenPair(principal);
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new BusinessException("EMAIL_TAKEN", "Email already in use");
        }
        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new BusinessException("ROLE_NOT_FOUND", "Default role not found"));

        User user = User.builder()
                .name(req.getFullName())
                .email(req.getEmail())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .roles(Set.of(customerRole))
                .enabled(true)
                .build();
        userRepository.save(user);
        log.info("New user registered: {}", user.getEmail());

        UserPrincipal principal = UserPrincipal.from(user);
        return buildTokenPair(principal);
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest req) {
        RefreshToken stored = refreshTokenService.consumeValidToken(req.getRefreshToken());

        User user = userRepository.findByEmailWithRolesAndPermissions(stored.getUser().getEmail())
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found"));
        UserPrincipal principal = UserPrincipal.from(user);
        return buildTokenPair(principal);
    }

    @Transactional
    public void logout(String accessToken, UUID userId) {
        jwtTokenProvider.blacklist(accessToken);
                refreshTokenService.revokeAllByUserId(userId, "LOGOUT");
        log.info("User {} logged out", userId);
    }

    private AuthResponse buildTokenPair(UserPrincipal principal) {
        String accessToken = jwtTokenProvider.generateAccessToken(principal);
                RefreshToken refreshToken = refreshTokenService.issueToken(principal.getUser());

        Set<String> roles = principal.getUser().getRoles().stream()
                .map(Role::getName).collect(Collectors.toSet());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(appProperties.getJwt().getAccessTokenExpirationMs() / 1000)
                .user(UserSummaryResponse.builder()
                        .id(principal.getUser().getId())
                        .fullName(principal.getUser().getName())
                        .email(principal.getUser().getEmail())
                        .avatarUrl(principal.getUser().getAvatarUrl())
                        .roles(roles)
                        .build())
                .build();
    }
}
