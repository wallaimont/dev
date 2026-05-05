package com.supportdesk.service;

import com.supportdesk.config.AppProperties;
import com.supportdesk.domain.entity.RefreshToken;
import com.supportdesk.domain.entity.User;
import com.supportdesk.exception.BusinessException;
import com.supportdesk.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock RefreshTokenRepository refreshTokenRepository;
    @Mock AppProperties appProperties;
    @Mock AppProperties.Jwt jwt;

    @InjectMocks RefreshTokenService refreshTokenService;

    @BeforeEach
    void setup() {
        lenient().when(appProperties.getJwt()).thenReturn(jwt);
        lenient().when(jwt.getRefreshTokenExpirationMs()).thenReturn(604_800_000L);
    }

    @Test
    void issueToken_shouldPersistTokenWithExpiry() {
        User user = User.builder().id(UUID.randomUUID()).email("test@sdpx.com").build();
        when(refreshTokenRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken token = refreshTokenService.issueToken(user);

        assertThat(token.getToken()).isNotBlank();
        assertThat(token.getExpiresAt()).isAfter(Instant.now());
        assertThat(token.isRevoked()).isFalse();
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void consumeValidToken_shouldRevokeToken() {
        RefreshToken token = RefreshToken.builder()
                .token("rt")
                .expiresAt(Instant.now().plusSeconds(120))
                .revoked(false)
                .build();
        when(refreshTokenRepository.findByToken("rt")).thenReturn(Optional.of(token));
        when(refreshTokenRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken consumed = refreshTokenService.consumeValidToken("rt");

        assertThat(consumed.isRevoked()).isTrue();
        assertThat(consumed.getRevokedReason()).isEqualTo("ROTATED");
    }

    @Test
    void consumeValidToken_shouldFailWhenExpired() {
        RefreshToken token = RefreshToken.builder()
                .token("rt")
                .expiresAt(Instant.now().minusSeconds(60))
                .revoked(false)
                .build();
        when(refreshTokenRepository.findByToken("rt")).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> refreshTokenService.consumeValidToken("rt"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("expired");
    }
}
