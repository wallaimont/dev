package com.orbyt.marketplace.shipping.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orbyt.marketplace.IntegrationTestBase;
import com.orbyt.marketplace.platform.repository.TenantRepository;
import com.orbyt.marketplace.shipping.domain.ShippingCarrier;
import com.orbyt.marketplace.shipping.repository.ShippingCarrierRepository;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
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
class ShippingControllerIntegrationTest extends IntegrationTestBase {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired TenantRepository tenantRepository;
    @Autowired ShippingCarrierRepository carrierRepository;

    private UUID tenantId;

    @BeforeEach
    void setUp() {
        tenantId = tenantRepository.findBySlugIgnoreCase("orbyt-demo")
                .orElseThrow().getId();
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of(
                "tenantSlug", "orbyt-demo",
                "email", email,
                "password", password
        ));
        String response = mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("accessToken").asText();
    }

    @Test
    void shouldListCarriersPublicly() throws Exception {
        ShippingCarrier carrier = new ShippingCarrier();
        carrier.setTenantId(tenantId);
        carrier.setCode("CORREIOS");
        carrier.setName("Correios PAC");
        carrier.setActive(true);
        carrier.setFlatRate(new BigDecimal("24.90"));
        carrier.setStatus("ACTIVE");
        carrierRepository.save(carrier);

        mockMvc.perform(get("/v1/shipping/carriers")
                        .header("X-Tenant-Id", "orbyt-demo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[?(@.code == 'CORREIOS')]").exists());
    }

    @Test
    void shouldCreateShipmentAndTrack() throws Exception {
        String token = loginAndGetToken("admin@orbyt.local", "Admin@123");
        UUID orderId = UUID.randomUUID();
        UUID sellerId = UUID.randomUUID();

        String shipmentPayload = objectMapper.writeValueAsString(Map.of(
                "orderId", orderId.toString(),
                "sellerId", sellerId.toString(),
                "carrierCode", "CORREIOS",
                "originZip", "01001000",
                "destZip", "20040020",
                "weightKg", 2.5
        ));

        String createResponse = mockMvc.perform(post("/v1/shipping/shipments")
                        .header("X-Tenant-Id", "orbyt-demo")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(shipmentPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(orderId.toString()))
                .andExpect(jsonPath("$.shipmentStatus").value("PENDING"))
                .andReturn().getResponse().getContentAsString();

        String shipmentId = objectMapper.readTree(createResponse).get("id").asText();

        mockMvc.perform(patch("/v1/shipping/shipments/" + shipmentId + "/ship")
                        .param("trackingCode", "BR123456789")
                        .header("X-Tenant-Id", "orbyt-demo")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingCode").value("BR123456789"))
                .andExpect(jsonPath("$.shipmentStatus").value("IN_TRANSIT"));

        mockMvc.perform(get("/v1/shipping/track/BR123456789")
                        .header("X-Tenant-Id", "orbyt-demo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingCode").value("BR123456789"));

        mockMvc.perform(get("/v1/shipping/orders/" + orderId)
                        .header("X-Tenant-Id", "orbyt-demo")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(orderId.toString()));
    }

    @Test
    void shouldAddTrackingEvent() throws Exception {
        String token = loginAndGetToken("admin@orbyt.local", "Admin@123");
        UUID orderId = UUID.randomUUID();

        String shipmentPayload = objectMapper.writeValueAsString(Map.of(
                "orderId", orderId.toString(),
                "sellerId", UUID.randomUUID().toString(),
                "carrierCode", "SEDEX",
                "originZip", "01001000",
                "destZip", "30130000",
                "weightKg", 1.0
        ));

        String createResponse = mockMvc.perform(post("/v1/shipping/shipments")
                        .header("X-Tenant-Id", "orbyt-demo")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(shipmentPayload))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String shipmentId = objectMapper.readTree(createResponse).get("id").asText();

        String trackingPayload = objectMapper.writeValueAsString(Map.of(
                "status", "IN_TRANSIT",
                "location", "Centro de Distribuição SP",
                "description", "Objeto em trânsito"
        ));

        mockMvc.perform(post("/v1/shipping/shipments/" + shipmentId + "/tracking")
                        .header("X-Tenant-Id", "orbyt-demo")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(trackingPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingEvents", hasSize(1)));
    }

    @Test
    void shouldReturnNotFoundForMissingTracking() throws Exception {
        mockMvc.perform(get("/v1/shipping/track/NONEXISTENT999")
                        .header("X-Tenant-Id", "orbyt-demo"))
                .andExpect(status().isNotFound());
    }

        @Test
        void shouldRequireAuthenticationToCreateShipment() throws Exception {
                String shipmentPayload = objectMapper.writeValueAsString(Map.of(
                                "orderId", UUID.randomUUID().toString(),
                                "sellerId", UUID.randomUUID().toString(),
                                "carrierCode", "CORREIOS",
                                "originZip", "01001000",
                                "destZip", "20040020",
                                "weightKg", 2.5
                ));

                mockMvc.perform(post("/v1/shipping/shipments")
                                                .header("X-Tenant-Id", "orbyt-demo")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(shipmentPayload))
                                .andExpect(status().isUnauthorized());
        }
}
