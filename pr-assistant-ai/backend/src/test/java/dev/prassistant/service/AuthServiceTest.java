package dev.prassistant.service;

import dev.prassistant.dto.LoginRequest;
import dev.prassistant.dto.LoginResponse;
import dev.prassistant.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthService authService;

    private Authentication mockAuth;

    @BeforeEach
    void setUp() {
        mockAuth = new UsernamePasswordAuthenticationToken(
                "admin@test.com", null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
    }

    @Test
    @DisplayName("login - valid credentials returns token with email and role")
    void login_success() {
        when(authenticationManager.authenticate(any())).thenReturn(mockAuth);
        when(jwtProvider.generateToken(eq("admin@test.com"), eq("ADMIN"))).thenReturn("jwt-token");

        LoginResponse response = authService.login(new LoginRequest("admin@test.com", "password"));

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.email()).isEqualTo("admin@test.com");
        assertThat(response.role()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("login - bad credentials throws BadCredentialsException")
    void login_badCredentials() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(new LoginRequest("wrong@test.com", "wrong")))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    @DisplayName("login - user with VIEWER role gets correct role in response")
    void login_viewerRole() {
        Authentication viewerAuth = new UsernamePasswordAuthenticationToken(
                "viewer@test.com", null,
                List.of(new SimpleGrantedAuthority("ROLE_VIEWER"))
        );
        when(authenticationManager.authenticate(any())).thenReturn(viewerAuth);
        when(jwtProvider.generateToken(eq("viewer@test.com"), eq("VIEWER"))).thenReturn("viewer-token");

        LoginResponse response = authService.login(new LoginRequest("viewer@test.com", "password"));

        assertThat(response.role()).isEqualTo("VIEWER");
    }
}
