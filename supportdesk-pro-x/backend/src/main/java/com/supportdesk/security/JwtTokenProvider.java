package com.supportdesk.security;

import com.supportdesk.config.AppProperties;
import com.supportdesk.service.TokenBlacklistService;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.security.*;
import java.security.spec.*;
import java.time.Duration;
import java.util.*;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class JwtTokenProvider {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final long accessTokenExpMs;
    private final String issuer;
    private final TokenBlacklistService tokenBlacklistService;

    public JwtTokenProvider(AppProperties appProperties,
                            ResourceLoader resourceLoader,
                            TokenBlacklistService tokenBlacklistService) throws Exception {
        this.accessTokenExpMs = appProperties.getJwt().getAccessTokenExpirationMs();
        this.issuer = appProperties.getJwt().getIssuer();
        this.tokenBlacklistService = tokenBlacklistService;

        PrivateKey resolvedPrivateKey;
        PublicKey resolvedPublicKey;

        try {
            resolvedPrivateKey = loadPrivateKey(readPem(resourceLoader, appProperties.getJwt().getPrivateKeyPath()));
            resolvedPublicKey = loadPublicKey(readPem(resourceLoader, appProperties.getJwt().getPublicKeyPath()));
        } catch (Exception ex) {
            log.warn("JWT key files unavailable or invalid. Falling back to ephemeral keys for this runtime only.");
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair pair = generator.generateKeyPair();
            resolvedPrivateKey = pair.getPrivate();
            resolvedPublicKey = pair.getPublic();
        }

        this.privateKey = resolvedPrivateKey;
        this.publicKey = resolvedPublicKey;
    }

    public String generateAccessToken(UserPrincipal principal) {
        String jti = UUID.randomUUID().toString();
        return Jwts.builder()
                .id(jti)
                .subject(principal.getId().toString())
                .claim("email", principal.getEmail())
                .claim("roles", principal.getAuthorities().stream()
                        .map(a -> a.getAuthority()).filter(a -> a.startsWith("ROLE_")).toList())
                .issuer(issuer)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpMs))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = parseClaims(token);
            String jti = claims.getId();
            return !tokenBlacklistService.isBlacklisted(jti);
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public void blacklist(String token) {
        try {
            Claims claims = parseClaims(token);
            long remaining = claims.getExpiration().getTime() - System.currentTimeMillis();
            if (remaining > 0) {
                tokenBlacklistService.blacklist(claims.getId(), Duration.ofMillis(remaining));
            }
        } catch (JwtException e) {
            log.warn("Could not blacklist token: {}", e.getMessage());
        }
    }

    public UUID getUserIdFromToken(String token) {
        return UUID.fromString(parseClaims(token).getSubject());
    }

    private Claims parseClaims(String token) {
        return Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token).getPayload();
    }

    private String readPem(ResourceLoader loader, String location) throws Exception {
        Resource resource = loader.getResource(location);
        try (InputStream in = resource.getInputStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private PrivateKey loadPrivateKey(String pem) throws Exception {
        String stripped = pem.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "").replaceAll("\\s", "");
        byte[] bytes = Base64.getDecoder().decode(stripped);
        return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(bytes));
    }

    private PublicKey loadPublicKey(String pem) throws Exception {
        String stripped = pem.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "").replaceAll("\\s", "");
        byte[] bytes = Base64.getDecoder().decode(stripped);
        return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(bytes));
    }
}
