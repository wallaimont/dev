package com.supportdesk.service;

import com.supportdesk.config.AppProperties;
import com.supportdesk.domain.entity.RefreshToken;
import com.supportdesk.domain.entity.User;
import com.supportdesk.exception.BusinessException;
import com.supportdesk.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AppProperties appProperties;

    @Transactional
    public RefreshToken issueToken(User user) {
        RefreshToken token = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiresAt(Instant.now().plusMillis(appProperties.getJwt().getRefreshTokenExpirationMs()))
                .revoked(false)
                .build();
        return refreshTokenRepository.save(token);
    }

    @Transactional
    public RefreshToken consumeValidToken(String rawToken) {
        RefreshToken stored = refreshTokenRepository.findByToken(rawToken)
                .orElseThrow(() -> new BusinessException("INVALID_REFRESH_TOKEN", "Refresh token not found"));

        if (!stored.isValid()) {
            throw new BusinessException("REFRESH_TOKEN_EXPIRED", "Refresh token has expired or has been revoked");
        }

        stored.setRevoked(true);
        stored.setRevokedAt(Instant.now());
        stored.setRevokedReason("ROTATED");
        return refreshTokenRepository.save(stored);
    }

    @Transactional
    public void revokeAllByUserId(UUID userId, String reason) {
        refreshTokenRepository.revokeAllByUserId(userId, Instant.now(), reason);
    }

    @Scheduled(cron = "0 15 2 * * *")
    @Transactional
    public void cleanupExpired() {
        Instant cutoff = Instant.now().minus(1, ChronoUnit.DAYS);
        refreshTokenRepository.deleteExpiredBefore(cutoff);
        log.info("Expired refresh tokens cleaned up with cutoff={}", cutoff);
    }
}
