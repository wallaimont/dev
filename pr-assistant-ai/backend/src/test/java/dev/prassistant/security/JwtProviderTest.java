package dev.prassistant.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtProviderTest {

    private JwtProvider jwtProvider;

    // 64+ character secret required for HS512
    private static final String SECRET = "test-secret-key-that-is-at-least-64-characters-long-for-hs512-algorithm-zzz";
    private static final long EXPIRATION_MS = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider(SECRET, EXPIRATION_MS);
    }

    @Test
    @DisplayName("generateToken - returns non-null compact JWT")
    void generateToken_returnsJwt() {
        String token = jwtProvider.generateToken("admin@test.com", "ADMIN");

        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3); // header.payload.signature
    }

    @Test
    @DisplayName("getEmailFromToken - extracts email correctly")
    void getEmailFromToken() {
        String token = jwtProvider.generateToken("admin@test.com", "ADMIN");

        String email = jwtProvider.getEmailFromToken(token);

        assertThat(email).isEqualTo("admin@test.com");
    }

    @Test
    @DisplayName("validateToken - valid token returns true")
    void validateToken_valid() {
        String token = jwtProvider.generateToken("user@test.com", "VIEWER");

        assertThat(jwtProvider.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("validateToken - tampered token returns false")
    void validateToken_tampered() {
        String token = jwtProvider.generateToken("user@test.com", "VIEWER");
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";

        assertThat(jwtProvider.validateToken(tampered)).isFalse();
    }

    @Test
    @DisplayName("validateToken - garbage string returns false")
    void validateToken_garbage() {
        assertThat(jwtProvider.validateToken("not.a.jwt")).isFalse();
    }

    @Test
    @DisplayName("validateToken - null returns false")
    void validateToken_null() {
        assertThat(jwtProvider.validateToken(null)).isFalse();
    }

    @Test
    @DisplayName("generateToken - different users produce different tokens")
    void generateToken_uniquePerUser() {
        String token1 = jwtProvider.generateToken("user1@test.com", "ADMIN");
        String token2 = jwtProvider.generateToken("user2@test.com", "VIEWER");

        assertThat(token1).isNotEqualTo(token2);
    }
}
