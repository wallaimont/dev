package com.orbyt.marketplace.order.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orbyt.marketplace.IntegrationTestBase;
import com.orbyt.marketplace.order.domain.Order;
import com.orbyt.marketplace.order.domain.OrderGroup;
import com.orbyt.marketplace.order.repository.OrderGroupRepository;
import com.orbyt.marketplace.order.repository.OrderRepository;
import com.orbyt.marketplace.platform.repository.TenantRepository;
import java.math.BigDecimal;
import java.util.Base64;
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
class OrderControllerIntegrationTest extends IntegrationTestBase {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
        @Autowired TenantRepository tenantRepository;
        @Autowired OrderGroupRepository orderGroupRepository;
        @Autowired OrderRepository orderRepository;

        private UUID tenantId;

        @BeforeEach
        void setUp() {
                tenantId = tenantRepository.findBySlugIgnoreCase("orbyt-demo").orElseThrow().getId();
        }

    private String registerAndGetToken(String email) throws Exception {
        String registerPayload = objectMapper.writeValueAsString(Map.of(
                "tenantSlug", "orbyt-demo",
                "fullName", "Order Test User",
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
        return objectMapper.readTree(response).get("accessToken").asText();
    }

        private String extractUserId(String token) throws Exception {
                String[] parts = token.split("\\.");
                String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
                return objectMapper.readTree(payload).get("sub").asText();
        }

        private Order createOrder(UUID buyerId, String status) {
                OrderGroup group = new OrderGroup();
                group.setTenantId(tenantId);
                group.setBuyerId(buyerId);
                group.setCurrencyCode("BRL");
                group.setSubtotal(new BigDecimal("120.00"));
                group.setShippingTotal(BigDecimal.ZERO);
                group.setDiscountTotal(BigDecimal.ZERO);
                group.setGrandTotal(new BigDecimal("120.00"));
                group.setStatus("AWAITING_PAYMENT");
                OrderGroup savedGroup = orderGroupRepository.save(group);

                Order order = new Order();
                order.setTenantId(tenantId);
                order.setOrderGroup(savedGroup);
                order.setBuyerId(buyerId);
                order.setSellerId(UUID.randomUUID());
                order.setStoreId(UUID.randomUUID());
                order.setCurrencyCode("BRL");
                order.setSubtotal(new BigDecimal("120.00"));
                order.setTotal(new BigDecimal("120.00"));
                order.setStatus(status);
                return orderRepository.save(order);
        }

    @Test
    void shouldReturnEmptyListForNewBuyer() throws Exception {
        String token = registerAndGetToken("order.buyer1@orbyt.local");
        // Extract userId from token (JWT subject)
        String[] parts = token.split("\\.");
        String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
        String userId = objectMapper.readTree(payload).get("sub").asText();

        mockMvc.perform(get("/v1/orders/my")
                        .param("buyerId", userId)
                        .header("X-Tenant-Id", "orbyt-demo")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldReturnNotFoundForNonExistentOrder() throws Exception {
        String token = registerAndGetToken("order.buyer2@orbyt.local");

        mockMvc.perform(get("/v1/orders/00000000-0000-0000-0000-000000000001")
                        .header("X-Tenant-Id", "orbyt-demo")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnEmptySellerOrders() throws Exception {
        String token = registerAndGetToken("order.seller1@orbyt.local");
        String[] parts = token.split("\\.");
        String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
        String userId = objectMapper.readTree(payload).get("sub").asText();

        mockMvc.perform(get("/v1/orders/seller")
                        .param("sellerId", userId)
                        .header("X-Tenant-Id", "orbyt-demo")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldReturnNotFoundForNonExistentOrderGroup() throws Exception {
        String token = registerAndGetToken("order.buyer3@orbyt.local");

        mockMvc.perform(get("/v1/orders/groups/00000000-0000-0000-0000-000000000002")
                        .header("X-Tenant-Id", "orbyt-demo")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnEmptyHistoryForNonExistentOrder() throws Exception {
        String token = registerAndGetToken("order.buyer4@orbyt.local");

        mockMvc.perform(get("/v1/orders/00000000-0000-0000-0000-000000000003/history")
                        .header("X-Tenant-Id", "orbyt-demo")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldRequireAuthenticationForOrders() throws Exception {
        mockMvc.perform(get("/v1/orders/my")
                        .param("buyerId", "00000000-0000-0000-0000-000000000001"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldUpdateOrderStatusAndCreateHistory() throws Exception {
        String token = registerAndGetToken("order.buyer5@orbyt.local");
        UUID buyerId = UUID.fromString(extractUserId(token));
        Order order = createOrder(buyerId, "AWAITING_PAYMENT");

        String payload = objectMapper.writeValueAsString(Map.of(
                "status", "PROCESSING",
                "changedBy", buyerId.toString()
        ));

        mockMvc.perform(patch("/v1/orders/" + order.getId() + "/status")
                        .header("X-Tenant-Id", "orbyt-demo")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PROCESSING"));

        mockMvc.perform(get("/v1/orders/" + order.getId() + "/history")
                        .header("X-Tenant-Id", "orbyt-demo")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].fromStatus").value("AWAITING_PAYMENT"))
                .andExpect(jsonPath("$[0].toStatus").value("PROCESSING"));
    }

    @Test
    void shouldRejectCancelWhenDelivered() throws Exception {
        String token = registerAndGetToken("order.buyer6@orbyt.local");
        UUID buyerId = UUID.fromString(extractUserId(token));
        Order order = createOrder(buyerId, "DELIVERED");

        mockMvc.perform(post("/v1/orders/" + order.getId() + "/cancel")
                        .header("X-Tenant-Id", "orbyt-demo")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isUnprocessableEntity());
    }
}
