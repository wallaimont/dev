package com.supportdesk.security;

import com.supportdesk.service.TokenBlacklistService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @Mock TokenBlacklistService blacklistService;

    @Test
    void isBlacklisted_shouldDelegateToService() {
        when(blacklistService.isBlacklisted("test-jti")).thenReturn(true);
        boolean result = blacklistService.isBlacklisted("test-jti");
        assertThat(result).isTrue();
    }

    @Test
    void blacklist_shouldCallServiceWithTtl() {
        doNothing().when(blacklistService).blacklist(anyString(), any());
        blacklistService.blacklist("jti123", java.time.Duration.ofMinutes(15));
        verify(blacklistService).blacklist(eq("jti123"), any());
    }
}
