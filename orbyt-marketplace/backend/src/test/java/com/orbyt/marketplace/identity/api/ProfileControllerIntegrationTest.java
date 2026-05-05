package com.orbyt.marketplace.identity.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orbyt.marketplace.IntegrationTestBase;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProfileControllerIntegrationTest extends IntegrationTestBase {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    private String[] registerAndGetTokenWithUserId(String email) throws Exception {
        String registerPayload = objectMapper.writeValueAsString(Map.of(
                "tenantSlug", "orbyt-demo",
                "fullName", "Profile Test User",
                "email", email,
                "password", "Test@123"
        ));
        mockMvc.perform(post("/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerPayload))
                .andExpect(status().isOk());

        String loginPayload = objectMapper.writeValueAsString(Map.of(
                "tenantSlug", "orbyt-demo",
                "email", email,
                "password", "Test@123"
        ));
        String response = mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(response).get("accessToken").asText();
        String[] parts = token.split("\\.");
        String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
        String userId = objectMapper.readTree(payload).get("sub").asText();
        return new String[]{token, userId};
    }

    @Test
    void shouldCreateAndRetrieveProfile() throws Exception {
        String[] auth = registerAndGetTokenWithUserId("profile.user1@orbyt.local");
        String token = auth[0];
        String userId = auth[1];

        String profilePayload = objectMapper.writeValueAsString(Map.of(
                "displayName", "Teste Perfil",
                "phone", "11999999999",
                "cpf", "12345678901",
                "preferredLanguage", "pt-BR",
                "preferredCurrency", "BRL"
        ));

        mockMvc.perform(put("/v1/profile/" + userId)
                        .header("X-Tenant-Id", "orbyt-demo")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(profilePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("Teste Perfil"))
                .andExpect(jsonPath("$.phone").value("11999999999"));

        mockMvc.perform(get("/v1/profile/" + userId)
                        .header("X-Tenant-Id", "orbyt-demo")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("Teste Perfil"));
    }

    @Test
    void shouldManageAddresses() throws Exception {
        String[] auth = registerAndGetTokenWithUserId("profile.user2@orbyt.local");
        String token = auth[0];
        String userId = auth[1];

        String addressPayload = objectMapper.writeValueAsString(Map.of(
                "label", "Casa",
                "street", "Rua das Flores",
                "number", "100",
                "neighborhood", "Centro",
                "city", "São Paulo",
                "state", "SP",
                "zipCode", "01001000",
                "defaultAddress", true
        ));

        String createResponse = mockMvc.perform(post("/v1/profile/" + userId + "/addresses")
                        .header("X-Tenant-Id", "orbyt-demo")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addressPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.label").value("Casa"))
                .andExpect(jsonPath("$.street").value("Rua das Flores"))
                .andReturn().getResponse().getContentAsString();

        String addressId = objectMapper.readTree(createResponse).get("id").asText();

        mockMvc.perform(get("/v1/profile/" + userId + "/addresses")
                        .header("X-Tenant-Id", "orbyt-demo")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].label").value("Casa"));

        String updatePayload = objectMapper.writeValueAsString(Map.of(
                "label", "Trabalho",
                "street", "Av. Paulista",
                "number", "1000",
                "neighborhood", "Bela Vista",
                "city", "São Paulo",
                "state", "SP",
                "zipCode", "01310100",
                "defaultAddress", false
        ));

        mockMvc.perform(put("/v1/profile/" + userId + "/addresses/" + addressId)
                        .header("X-Tenant-Id", "orbyt-demo")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label").value("Trabalho"));

        mockMvc.perform(delete("/v1/profile/" + userId + "/addresses/" + addressId)
                        .header("X-Tenant-Id", "orbyt-demo")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/profile/" + userId + "/addresses")
                        .header("X-Tenant-Id", "orbyt-demo")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldReturnNotFoundForMissingProfile() throws Exception {
        String[] auth = registerAndGetTokenWithUserId("profile.user3@orbyt.local");
        String token = auth[0];

        mockMvc.perform(get("/v1/profile/00000000-0000-0000-0000-000000000099")
                        .header("X-Tenant-Id", "orbyt-demo")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingUnknownAddress() throws Exception {
        String[] auth = registerAndGetTokenWithUserId("profile.user4@orbyt.local");
        String token = auth[0];
        String userId = auth[1];

        String updatePayload = objectMapper.writeValueAsString(Map.of(
                "label", "Inexistente",
                "street", "Rua Sem Numero",
                "number", "0",
                "neighborhood", "Centro",
                "city", "Sao Paulo",
                "state", "SP",
                "zipCode", "01000000",
                "defaultAddress", false
        ));

        mockMvc.perform(put("/v1/profile/" + userId + "/addresses/00000000-0000-0000-0000-000000000222")
                        .header("X-Tenant-Id", "orbyt-demo")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().isNotFound());
    }
}
