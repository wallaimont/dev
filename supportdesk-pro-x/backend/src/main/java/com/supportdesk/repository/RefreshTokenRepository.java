package com.supportdesk.repository;

import com.supportdesk.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Query("UPDATE RefreshToken r SET r.revoked = TRUE, r.revokedAt = :now, r.revokedReason = :reason WHERE r.user.id = :userId AND r.revoked = FALSE")
    void revokeAllByUserId(UUID userId, Instant now, String reason);

    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.expiresAt < :cutoff")
    void deleteExpiredBefore(Instant cutoff);
}
