package dev.prassistant.controller;

import dev.prassistant.config.SecurityConfig;
import dev.prassistant.dto.DashboardResponse;
import dev.prassistant.security.CustomUserDetailsService;
import dev.prassistant.security.JwtProvider;
import dev.prassistant.service.DashboardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("GET /api/dashboard - authenticated user gets dashboard data")
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void getDashboard_authenticated() throws Exception {
        var response = new DashboardResponse(100, 25, 80, 60, 20, 5, 78.5);

        when(dashboardService.getDashboard()).thenReturn(response);

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrs").value(100))
                .andExpect(jsonPath("$.openPrs").value(25))
                .andExpect(jsonPath("$.analyzedPrs").value(80))
                .andExpect(jsonPath("$.approvedPrs").value(60))
                .andExpect(jsonPath("$.rejectedPrs").value(20))
                .andExpect(jsonPath("$.criticalPrs").value(5))
                .andExpect(jsonPath("$.avgQualityScore").value(78.5));
    }

    @Test
    @DisplayName("GET /api/dashboard - unauthenticated returns 401")
    void getDashboard_unauthenticated() throws Exception {
        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isUnauthorized());
    }
}
