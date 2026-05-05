package com.nexus.shared.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class JwtService {

    private final SecretKey secretKey;
    private final long accessTokenTtlMs;
    private final long refreshTokenTtlMs;

    public JwtService(
        @Value("${nexus.security.jwt.secret}") String secret,
        @Value("${nexus.security.jwt.access-token-ttl-ms:900000}") long accessTtl,
        @Value("${nexus.security.jwt.refresh-token-ttl-ms:2592000000}") long refreshTtl
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenTtlMs = accessTtl;
        this.refreshTokenTtlMs = refreshTtl;
    }

    public String generateAccessToken(UUID userId, UUID tenantId, String email, List<String> roles) {
        Instant now = Instant.now();
        return Jwts.builder()
            .subject(userId.toString())
            .claim("tenant_id", tenantId.toString())
            .claim("email", email)
            .claim("roles", roles)
            .claim("type", "ACCESS")
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusMillis(accessTokenTtlMs)))
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact();
    }

    public String generateRefreshToken(UUID userId) {
        Instant now = Instant.now();
        return Jwts.builder()
            .subject(userId.toString())
            .claim("type", "REFRESH")
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusMillis(refreshTokenTtlMs)))
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public boolean isValid(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Invalid JWT: {}", e.getMessage());
            return false;
        }
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(parseToken(token).getSubject());
    }

    public UUID extractTenantId(String token) {
        return UUID.fromString(parseToken(token).get("tenant_id", String.class));
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return parseToken(token).get("roles", List.class);
    }

    public Instant extractExpiration(String token) {
        return parseToken(token).getExpiration().toInstant();
    }

    public long getRefreshTokenTtlMs() {
        return refreshTokenTtlMs;
    }
}
