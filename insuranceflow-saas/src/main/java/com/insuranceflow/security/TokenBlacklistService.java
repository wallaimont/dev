package com.insuranceflow.security;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final TokenBlacklistRepository repository;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public void blacklist(String token, UUID userId, String tipo, String motivo) {
        try {
            String jti = tokenProvider.getJtiFromToken(token);
            if (jti == null) {
                jti = UUID.randomUUID().toString();
            }
            Date expiration = tokenProvider.getExpirationFromToken(token);

            TokenBlacklist entry = TokenBlacklist.builder()
                .tokenJti(jti)
                .userId(userId)
                .tipo(tipo)
                .expiraEm(expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .revogadoEm(LocalDateTime.now())
                .motivo(motivo)
                .build();

            repository.save(entry);
        } catch (Exception e) {
            log.warn("Falha ao blacklistar token: {}", e.getMessage());
        }
    }

    public boolean isBlacklisted(String token) {
        try {
            String jti = tokenProvider.getJtiFromToken(token);
            if (jti == null) return false;
            return repository.existsByTokenJti(jti);
        } catch (Exception e) {
            return false;
        }
    }

    @Scheduled(cron = "0 0 */6 * * *")
    @Transactional
    public void cleanupExpiredTokens() {
        int deleted = repository.deleteExpiredTokens(LocalDateTime.now());
        if (deleted > 0) {
            log.info("Limpeza de tokens expirados: {} removidos", deleted);
        }
    }
}
