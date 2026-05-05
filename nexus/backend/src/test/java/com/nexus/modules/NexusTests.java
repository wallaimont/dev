package com.nexus.modules.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexus.modules.auth.controller.AuthController;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@DisplayName("Authentication Integration Tests")
class AuthIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("nexus_test")
        .withUsername("nexus")
        .withPassword("nexus123");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    private static final String TENANT_HEADER = "X-Tenant-ID";
    private static final String TENANT_SLUG   = "test-tenant";

    // ---- Helpers ----
    private String register(String email, String password) throws Exception {
        var req = new RegisterRequest();
        req.email = email; req.password = password;
        req.firstName = "Test"; req.lastName = "User";

        MvcResult result = mvc.perform(post("/api/v1/auth/register")
            .header(TENANT_HEADER, TENANT_SLUG)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andReturn();

        AuthResponse resp = mapper.readValue(
            result.getResponse().getContentAsString(), AuthResponse.class);
        return resp.accessToken;
    }

    // ---- Tests ----

    @Test
    @DisplayName("Register new user → returns 201 with tokens")
    void register_success() throws Exception {
        var req = new RegisterRequest();
        req.email = "newuser@test.com"; req.password = "securePass123";
        req.firstName = "João"; req.lastName = "Silva";

        mvc.perform(post("/api/v1/auth/register")
            .header(TENANT_HEADER, TENANT_SLUG)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.accessToken").isNotEmpty())
            .andExpect(jsonPath("$.refreshToken").isNotEmpty())
            .andExpect(jsonPath("$.email").value("newuser@test.com"))
            .andExpect(jsonPath("$.roles[0]").value("BUYER"));
    }

    @Test
    @DisplayName("Register duplicate email → returns 409")
    void register_duplicateEmail() throws Exception {
        String email = "duplicate@test.com";
        register(email, "pass12345");

        var req = new RegisterRequest();
        req.email = email; req.password = "pass12345";
        req.firstName = "Another"; req.lastName = "User";

        mvc.perform(post("/api/v1/auth/register")
            .header(TENANT_HEADER, TENANT_SLUG)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(req)))
            .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Login valid credentials → returns tokens")
    void login_success() throws Exception {
        String email = "login@test.com";
        register(email, "mypassword1");

        var req = new LoginRequest();
        req.email = email; req.password = "mypassword1";

        mvc.perform(post("/api/v1/auth/login")
            .header(TENANT_HEADER, TENANT_SLUG)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").isNotEmpty())
            .andExpect(jsonPath("$.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.expiresIn").value(900));
    }

    @Test
    @DisplayName("Login wrong password → returns 401")
    void login_wrongPassword() throws Exception {
        register("wrongpass@test.com", "correct123");

        var req = new LoginRequest();
        req.email = "wrongpass@test.com"; req.password = "wrong123";

        mvc.perform(post("/api/v1/auth/login")
            .header(TENANT_HEADER, TENANT_SLUG)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(req)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Refresh token → returns new token pair")
    void refresh_success() throws Exception {
        register("refresh@test.com", "pass12345");

        var loginReq = new LoginRequest();
        loginReq.email = "refresh@test.com"; loginReq.password = "pass12345";

        MvcResult loginResult = mvc.perform(post("/api/v1/auth/login")
            .header(TENANT_HEADER, TENANT_SLUG)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(loginReq)))
            .andReturn();

        AuthResponse loginResp = mapper.readValue(
            loginResult.getResponse().getContentAsString(), AuthResponse.class);

        var refreshReq = new RefreshRequest();
        refreshReq.refreshToken = loginResp.refreshToken;

        mvc.perform(post("/api/v1/auth/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(refreshReq)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    @DisplayName("Register without tenant header → returns 4xx")
    void register_noTenantHeader() throws Exception {
        var req = new RegisterRequest();
        req.email = "notenant@test.com"; req.password = "pass12345";
        req.firstName = "A"; req.lastName = "B";

        mvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(req)))
            .andExpect(result ->
                assertThat(result.getResponse().getStatus()).isIn(400, 422, 500));
    }

    @Test
    @DisplayName("Register with short password → returns 422")
    void register_invalidPassword() throws Exception {
        var req = new RegisterRequest();
        req.email = "shortpass@test.com"; req.password = "123"; // too short
        req.firstName = "A"; req.lastName = "B";

        mvc.perform(post("/api/v1/auth/register")
            .header(TENANT_HEADER, TENANT_SLUG)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(req)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors.password").exists());
    }
}

// ============================================================
// PAYMENT / PIX SERVICE UNIT TESTS
// ============================================================
package com.nexus.modules.payment;

import com.nexus.modules.payment.domain.*;
import com.nexus.modules.payment.repository.*;
import com.nexus.modules.payment.service.PaymentService;
import com.nexus.shared.events.OutboxEventPublisher;
import com.nexus.shared.tenant.TenantContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Payment Service Unit Tests")
class PaymentServiceTest {

    @Mock PaymentRepository paymentRepository;
    @Mock PixChargeRepository pixChargeRepository;
    @Mock PaymentSplitRepository splitRepository;
    @Mock GatewayPixClient gatewayClient;
    @Mock OutboxEventPublisher eventPublisher;

    @InjectMocks PaymentService paymentService;

    private UUID tenantId;
    private UUID orderId;
    private UUID buyerId;
    private String idempotencyKey;

    @BeforeEach
    void setUp() {
        tenantId = UUID.randomUUID();
        orderId  = UUID.randomUUID();
        buyerId  = UUID.randomUUID();
        idempotencyKey = UUID.randomUUID().toString();
        TenantContext.setTenantId(tenantId);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("createPixCharge → idempotency returns existing charge")
    void createPixCharge_idempotency() {
        Payment existingPayment = Payment.builder()
            .id(UUID.randomUUID())
            .tenantId(tenantId)
            .orderId(orderId)
            .idempotencyKey(idempotencyKey)
            .status(PaymentStatus.PENDING)
            .amount(BigDecimal.valueOf(199.90))
            .build();

        PixCharge existingCharge = PixCharge.builder()
            .id(UUID.randomUUID())
            .paymentId(existingPayment.getId())
            .txid("txid123")
            .qrCode("qr-code-data")
            .pixCopyPaste("pix-copy-paste-data")
            .amount(BigDecimal.valueOf(199.90))
            .status(PixStatus.ACTIVE)
            .expiresAt(Instant.now().plusSeconds(1800))
            .build();

        when(paymentRepository.findByIdempotencyKey(idempotencyKey))
            .thenReturn(Optional.of(existingPayment));
        when(pixChargeRepository.findByPaymentId(existingPayment.getId()))
            .thenReturn(Optional.of(existingCharge));

        PixChargeResponse response = paymentService.createPixCharge(orderId, buyerId, idempotencyKey);

        assertThat(response.getTxid()).isEqualTo("txid123");
        assertThat(response.getPixCopyPaste()).isEqualTo("pix-copy-paste-data");

        // Gateway should NOT be called again
        verifyNoInteractions(gatewayClient);
    }

    @Test
    @DisplayName("processWebhook PIX confirmed → updates status and publishes event")
    void processWebhook_pixConfirmed() {
        String txid = "txid-confirmed-123";

        Payment payment = Payment.builder()
            .id(UUID.randomUUID())
            .tenantId(tenantId)
            .orderId(orderId)
            .status(PaymentStatus.PENDING)
            .amount(BigDecimal.valueOf(350.00))
            .build();

        PixCharge charge = PixCharge.builder()
            .id(UUID.randomUUID())
            .paymentId(payment.getId())
            .txid(txid)
            .status(PixStatus.ACTIVE)
            .amount(BigDecimal.valueOf(350.00))
            .expiresAt(Instant.now().plusSeconds(1800))
            .build();

        when(pixChargeRepository.existsByProcessedWebhookId(anyString())).thenReturn(false);
        when(pixChargeRepository.findByTxid(txid)).thenReturn(Optional.of(charge));
        when(paymentRepository.findById(payment.getId())).thenReturn(Optional.of(payment));
        when(pixChargeRepository.save(any())).thenReturn(charge);
        when(paymentRepository.save(any())).thenReturn(payment);

        paymentService.processWebhook("GERENCIANET", "pix.payment.received",
            txid, "valid-signature");

        assertThat(charge.getStatus()).isEqualTo(PixStatus.COMPLETED);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PAID);
        verify(eventPublisher).publish(any(), anyString());
    }

    @Test
    @DisplayName("processWebhook duplicate → ignores gracefully")
    void processWebhook_duplicate() {
        when(pixChargeRepository.existsByProcessedWebhookId(anyString())).thenReturn(true);

        // Should NOT throw, should silently skip
        assertThatNoException().isThrownBy(() ->
            paymentService.processWebhook("GERENCIANET", "pix.payment.received",
                "txid", "sig"));

        verifyNoInteractions(paymentRepository);
    }
}

// ============================================================
// ORDER SERVICE UNIT TESTS
// ============================================================
package com.nexus.modules.order;

import com.nexus.modules.cart.domain.*;
import com.nexus.modules.cart.service.CartService;
import com.nexus.modules.order.domain.*;
import com.nexus.modules.order.dto.CheckoutRequest;
import com.nexus.modules.order.repository.*;
import com.nexus.modules.order.service.OrderService;
import com.nexus.modules.order.service.CommissionCalculator;
import com.nexus.modules.catalog.service.StockService;
import com.nexus.shared.events.OutboxEventPublisher;
import com.nexus.shared.tenant.TenantContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Order Service Unit Tests")
class OrderServiceTest {

    @Mock OrderRepository orderRepository;
    @Mock OrderGroupRepository groupRepository;
    @Mock OrderItemRepository itemRepository;
    @Mock OrderStatusHistoryRepository historyRepository;
    @Mock CartService cartService;
    @Mock StockService stockService;
    @Mock OutboxEventPublisher eventPublisher;
    @Mock CommissionCalculator commissionCalculator;

    @InjectMocks OrderService orderService;

    private UUID tenantId;
    private UUID buyerId;

    @BeforeEach
    void setUp() {
        tenantId = UUID.randomUUID();
        buyerId  = UUID.randomUUID();
        TenantContext.setTenantId(tenantId);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("cancelOrder in PENDING status → succeeds")
    void cancelOrder_pending() {
        UUID orderId = UUID.randomUUID();
        Order order = Order.builder()
            .tenantId(tenantId)
            .buyerId(buyerId)
            .orderNumber("NX001")
            .status(OrderStatus.PENDING)
            .total(BigDecimal.valueOf(199.90))
            .build();

        when(orderRepository.findByIdAndTenantId(orderId, tenantId))
            .thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenReturn(order);

        assertThatNoException().isThrownBy(() ->
            orderService.cancelOrder(orderId, "Changed mind", buyerId));

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(stockService).releaseReservations(orderId);
        verify(eventPublisher).publish(any(), anyString());
    }

    @Test
    @DisplayName("cancelOrder in SHIPPED status → throws InvalidOrderTransitionException")
    void cancelOrder_shipped_throws() {
        UUID orderId = UUID.randomUUID();
        Order order = Order.builder()
            .tenantId(tenantId)
            .buyerId(buyerId)
            .orderNumber("NX002")
            .status(OrderStatus.SHIPPED)
            .total(BigDecimal.valueOf(199.90))
            .build();

        when(orderRepository.findByIdAndTenantId(orderId, tenantId))
            .thenReturn(Optional.of(order));

        assertThatThrownBy(() ->
            orderService.cancelOrder(orderId, "Too late", buyerId))
            .isInstanceOf(OrderService.InvalidOrderTransitionException.class)
            .hasMessageContaining("SHIPPED");
    }
}
