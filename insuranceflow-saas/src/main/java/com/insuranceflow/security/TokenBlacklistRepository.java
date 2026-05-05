package com.insuranceflow.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.UUID;

public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, UUID> {

    boolean existsByTokenJti(String tokenJti);

    @Modifying
    @Query("DELETE FROM TokenBlacklist t WHERE t.expiraEm < :now")
    int deleteExpiredTokens(LocalDateTime now);

    @Modifying
    @Query("DELETE FROM TokenBlacklist t WHERE t.userId = :userId")
    int deleteByUserId(UUID userId);
}
