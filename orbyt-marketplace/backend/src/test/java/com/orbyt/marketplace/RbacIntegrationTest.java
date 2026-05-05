package com.orbyt.marketplace;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RbacIntegrationTest extends IntegrationTestBase {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void adminShouldAccessDashboardAndBuyerShouldBeForbidden() throws Exception {
        String adminToken = loginAndGetAccessToken("admin@orbyt.local", "Admin@123");

        mockMvc.perform(get("/v1/admin/dashboard")
                        .header("X-Tenant-Id", "orbyt-demo")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isOk());

        String registerPayload = objectMapper.writeValueAsString(Map.of(
                "tenantSlug", "orbyt-demo",
                "fullName", "Buyer Rbac",
                "email", "buyer.rbac@orbyt.local",
                "password", "Buyer@123"
        ));
        mockMvc.perform(post("/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerPayload))
                .andExpect(status().isOk());

        String buyerToken = loginAndGetAccessToken("buyer.rbac@orbyt.local", "Buyer@123");
        mockMvc.perform(get("/v1/admin/dashboard")
                        .header("X-Tenant-Id", "orbyt-demo")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + buyerToken))
                .andExpect(status().isForbidden());
    }

    private String loginAndGetAccessToken(String email, String password) throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of(
                "tenantSlug", "orbyt-demo",
                "email", email,
                "password", password
        ));
        String response = mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode node = objectMapper.readTree(response);
        return node.get("accessToken").asText();
    }
}
