package com.orionerp.modules.financeiro.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orionerp.common.PageResponse;
import com.orionerp.config.TestSecurityConfig;
import com.orionerp.modules.financeiro.dto.TituloResponse;
import com.orionerp.modules.financeiro.service.TituloService;
import com.orionerp.security.CustomUserDetailsService;
import com.orionerp.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TituloController.class)
@Import(TestSecurityConfig.class)
class TituloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TituloService tituloService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void deveRetornar401SemAutenticacao() throws Exception {
        mockMvc.perform(get("/api/v1/financeiro/titulos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void deveListarTitulosComAutoridade() throws Exception {
        var page = PageResponse.<TituloResponse>builder()
                .items(List.of())
                .page(0).size(20).totalElements(0).totalPages(0).first(true).last(true)
                .build();

        when(tituloService.list(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(int.class), any(int.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/financeiro/titulos")
                        .param("page", "0").param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items").isArray());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"financeiro:listar"})
    void deveListarTitulosComAutoridadeEspecifica() throws Exception {
        var page = PageResponse.<TituloResponse>builder()
                .items(List.of())
                .page(0).size(20).totalElements(0).totalPages(0).first(true).last(true)
                .build();

        when(tituloService.list(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(int.class), any(int.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/financeiro/titulos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"outra:permissao"})
    void deveRetornar403SemAutoridadeCorreta() throws Exception {
        mockMvc.perform(get("/api/v1/financeiro/titulos"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void deveDeletarTitulo() throws Exception {
        mockMvc.perform(delete("/api/v1/financeiro/titulos/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Registro excluído com sucesso"));

        verify(tituloService).delete(1L);
    }

    @Test
    @WithMockUser(username = "user", authorities = {"financeiro:criar"})
    void deveRetornar403AoTentarDeletarSemPermissao() throws Exception {
        mockMvc.perform(delete("/api/v1/financeiro/titulos/1").with(csrf()))
                .andExpect(status().isForbidden());
    }
}
