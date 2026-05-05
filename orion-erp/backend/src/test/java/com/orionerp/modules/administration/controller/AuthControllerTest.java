package com.orionerp.modules.administration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orionerp.modules.administration.dto.AuthResponse;
import com.orionerp.modules.administration.dto.AuthUserDto;
import com.orionerp.modules.administration.dto.LoginRequest;
import com.orionerp.modules.administration.dto.RefreshTokenRequest;
import com.orionerp.modules.administration.service.AuthService;
import com.orionerp.security.CustomUserDetailsService;
import com.orionerp.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void deveRealizarLoginComSucesso() throws Exception {
        var authResponse = new AuthResponse(
                "Bearer", "access-token-123", "refresh-token-456",
                1800, null
        );

        when(authService.login(any(LoginRequest.class), any())).thenReturn(authResponse);

        var request = new LoginRequest("admin@orion.com", "senha123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("access-token-123"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.message").value("Login efetuado com sucesso"));
    }

    @Test
    void deveRetornar400ComLoginSemEmail() throws Exception {
        var request = new LoginRequest("", "senha123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar400ComLoginSemSenha() throws Exception {
        var request = new LoginRequest("admin@orion.com", "");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRealizarRefreshToken() throws Exception {
        var authResponse = new AuthResponse(
                "Bearer", "new-access-token", "new-refresh-token",
                1800, null
        );

        when(authService.refresh(any(RefreshTokenRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"refresh-token-456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.message").value("Token renovado com sucesso"));
    }

    @Test
    void deveRetornarUsuarioAtual() throws Exception {
        var user = new AuthUserDto(1L, 1L, 1L, "Admin", "admin@orion.com", "Administrador",
                java.util.List.of("ROLE_ADMIN"));

        when(authService.currentUser()).thenReturn(user);

        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nome").value("Admin"))
                .andExpect(jsonPath("$.data.email").value("admin@orion.com"));
    }
}
