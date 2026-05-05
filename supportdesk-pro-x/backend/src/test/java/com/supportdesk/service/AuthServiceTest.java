package com.supportdesk.service;

import com.supportdesk.config.AppProperties;
import com.supportdesk.domain.entity.*;
import com.supportdesk.dto.request.*;
import com.supportdesk.dto.response.*;
import com.supportdesk.exception.BusinessException;
import com.supportdesk.repository.*;
import com.supportdesk.security.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock RoleRepository roleRepository;
    @Mock RefreshTokenService refreshTokenService;
    @Mock org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    @Mock JwtTokenProvider jwtTokenProvider;
    @Mock AuthenticationManager authenticationManager;
    @Mock AppProperties appProperties;
    @Mock AppProperties.Jwt jwtProps;

    @InjectMocks AuthService authService;

    @BeforeEach
    void setup() {
        lenient().when(appProperties.getJwt()).thenReturn(jwtProps);
        lenient().when(jwtProps.getAccessTokenExpirationMs()).thenReturn(900_000L);
    }

    @Test
    void login_shouldReturnTokenPair() {
        User user = buildUser();
        UserPrincipal principal = UserPrincipal.from(user);
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(principal);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtTokenProvider.generateAccessToken(any())).thenReturn("access-token");
        when(refreshTokenService.issueToken(any())).thenReturn(RefreshToken.builder().token("rt-123").build());

        AuthResponse result = authService.login(new LoginRequest("user@test.com", "password123"));

        assertThat(result.getAccessToken()).isEqualTo("access-token");
        assertThat(result.getRefreshToken()).isEqualTo("rt-123");
        verify(refreshTokenService).issueToken(any(User.class));
    }

    @Test
    void register_duplicateEmail_throwsBusinessException() {
        when(userRepository.existsByEmail("dup@test.com")).thenReturn(true);
        assertThatThrownBy(() -> authService.register(new RegisterRequest("John", "dup@test.com", "pass1234")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already in use");
    }

    @Test
    void register_success_createsUserAndReturnsTokens() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        Role role = Role.builder().id(UUID.randomUUID()).name("CUSTOMER").build();
        when(roleRepository.findByName("CUSTOMER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(jwtTokenProvider.generateAccessToken(any())).thenReturn("at");
        when(refreshTokenService.issueToken(any())).thenReturn(RefreshToken.builder().token("rt-456").build());

        AuthResponse result = authService.register(new RegisterRequest("Alice", "alice@test.com", "password1"));

        assertThat(result.getUser().getEmail()).isEqualTo("alice@test.com");
        assertThat(result.getRefreshToken()).isEqualTo("rt-456");
    }

    @Test
    void refresh_shouldRotateAndIssueNewTokens() {
        User storedUser = buildUser();
        RefreshToken consumed = RefreshToken.builder().token("old-rt").user(storedUser).build();

        when(refreshTokenService.consumeValidToken("old-rt")).thenReturn(consumed);
        when(userRepository.findByEmailWithRolesAndPermissions(storedUser.getEmail())).thenReturn(Optional.of(storedUser));
        when(jwtTokenProvider.generateAccessToken(any())).thenReturn("new-access");
        when(refreshTokenService.issueToken(any())).thenReturn(RefreshToken.builder().token("new-rt").build());

        AuthResponse result = authService.refresh(new RefreshTokenRequest("old-rt"));

        assertThat(result.getAccessToken()).isEqualTo("new-access");
        assertThat(result.getRefreshToken()).isEqualTo("new-rt");
        verify(refreshTokenService).consumeValidToken("old-rt");
        verify(refreshTokenService).issueToken(any(User.class));
    }

    private User buildUser() {
        Role r = Role.builder().id(UUID.randomUUID()).name("CUSTOMER").build();
        return User.builder()
            .id(UUID.randomUUID()).name("Test User").email("user@test.com")
            .passwordHash("hashed").roles(Set.of(r)).enabled(true).build();
    }
}
