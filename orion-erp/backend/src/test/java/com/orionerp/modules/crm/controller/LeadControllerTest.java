package com.orionerp.modules.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.crm.dto.LeadRequest;
import com.orionerp.modules.crm.dto.LeadResponse;
import com.orionerp.modules.crm.service.LeadService;
import com.orionerp.security.CustomUserDetailsService;
import com.orionerp.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LeadController.class)
class LeadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LeadService leadService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private LeadResponse criarLeadResponse() {
        return new LeadResponse(
                1L, UUID.randomUUID(), 1L, "Teste Lead", "lead@test.com",
                "11999999999", "Empresa X", "Gerente", "SITE",
                10L, "NOVO", null, "Obs",
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveListarLeads() throws Exception {
        var response = criarLeadResponse();
        var page = PageResponse.<LeadResponse>builder()
                .items(List.of(response))
                .page(0).size(20).totalElements(1).totalPages(1).first(true).last(true)
                .build();

        when(leadService.list(any(), any(), any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/crm/leads")
                        .param("page", "0").param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items[0].nome").value("Teste Lead"))
                .andExpect(jsonPath("$.data.items[0].status").value("NOVO"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveBuscarLeadPorId() throws Exception {
        var response = criarLeadResponse();
        when(leadService.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/crm/leads/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nome").value("Teste Lead"))
                .andExpect(jsonPath("$.data.email").value("lead@test.com"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveCriarLead() throws Exception {
        var request = new LeadRequest(1L, "Novo Lead", "novo@test.com",
                "11888888888", "Nova Empresa", "Diretor", "INDICACAO", null, "Observação");
        var response = new LeadResponse(
                2L, UUID.randomUUID(), 1L, "Novo Lead", "novo@test.com",
                "11888888888", "Nova Empresa", "Diretor", "INDICACAO",
                null, "NOVO", null, "Observação",
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(leadService.create(any(LeadRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/crm/leads").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nome").value("Novo Lead"))
                .andExpect(jsonPath("$.data.status").value("NOVO"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveAvancarStatusLead() throws Exception {
        var response = new LeadResponse(
                1L, UUID.randomUUID(), 1L, "Teste Lead", "lead@test.com",
                "11999999999", "Empresa X", "Gerente", "SITE",
                10L, "CONTATADO", null, null,
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(leadService.avancarStatus(1L)).thenReturn(response);

        mockMvc.perform(post("/api/v1/crm/leads/1/avancar").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CONTATADO"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveConverterLead() throws Exception {
        var response = new LeadResponse(
                1L, UUID.randomUUID(), 1L, "Lead Convertido", null,
                null, null, null, null,
                null, "CONVERTIDO", 5L, null,
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(leadService.converter(eq(1L), eq(5L))).thenReturn(response);

        mockMvc.perform(post("/api/v1/crm/leads/1/converter").with(csrf())
                        .param("clienteId", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CONVERTIDO"))
                .andExpect(jsonPath("$.data.convertidoClienteId").value(5));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void devePerderLead() throws Exception {
        var response = new LeadResponse(
                1L, UUID.randomUUID(), 1L, "Lead Perdido", null,
                null, null, null, null,
                null, "PERDIDO", null, "Sem budget",
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(leadService.perder(eq(1L), eq("Sem budget"))).thenReturn(response);

        mockMvc.perform(post("/api/v1/crm/leads/1/perder").with(csrf())
                        .param("motivo", "Sem budget"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PERDIDO"));
    }

    @Test
    void deveRetornar401SemAutenticacao() throws Exception {
        mockMvc.perform(get("/api/v1/crm/leads"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornar400ComRequestInvalida() throws Exception {
        var request = new LeadRequest(null, "", null, null, null, null, null, null, null);

        mockMvc.perform(post("/api/v1/crm/leads").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
