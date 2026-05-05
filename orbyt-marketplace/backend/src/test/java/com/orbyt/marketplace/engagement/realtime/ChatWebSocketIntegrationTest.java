package com.orbyt.marketplace.engagement.realtime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orbyt.marketplace.IntegrationTestBase;
import com.orbyt.marketplace.catalog.domain.Seller;
import com.orbyt.marketplace.catalog.repository.SellerRepository;
import com.orbyt.marketplace.config.JwtService;
import com.orbyt.marketplace.engagement.domain.Chat;
import com.orbyt.marketplace.engagement.realtime.dto.ChatReadReceipt;
import com.orbyt.marketplace.engagement.realtime.dto.ChatRealtimeMessage;
import com.orbyt.marketplace.engagement.realtime.dto.NotificationRealtimeMessage;
import com.orbyt.marketplace.platform.repository.TenantRepository;
import com.orbyt.marketplace.shared.tenant.TenantContext;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ChatWebSocketIntegrationTest extends IntegrationTestBase {

    @LocalServerPort
    int port;

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired JwtService jwtService;
    @Autowired TenantRepository tenantRepository;
    @Autowired SellerRepository sellerRepository;
    @Autowired com.orbyt.marketplace.engagement.application.ChatService chatService;

    private UUID tenantId;

    @BeforeEach
    void setUp() {
        tenantId = tenantRepository.findBySlugIgnoreCase("orbyt-demo").orElseThrow().getId();
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void shouldExchangeChatMessageAndReadReceiptOverWebSocket() throws Exception {
        String buyerToken = registerAndLogin("buyer.ws@orbyt.local");
        UUID buyerUserId = jwtService.extractUserId(buyerToken);

        String sellerToken = registerAndLogin("seller.ws@orbyt.local");
        UUID sellerUserId = jwtService.extractUserId(sellerToken);
        Seller seller = createSeller(sellerUserId);

        TenantContext.set(tenantId);
        Chat chat;
        try {
            chat = chatService.getOrCreateChat(buyerUserId, seller.getId());
        } finally {
            TenantContext.clear();
        }

        WebSocketStompClient buyerClient = stompClient();
        WebSocketStompClient sellerClient = stompClient();
        StompSession buyerSession = connect(buyerClient, buyerToken);
        StompSession sellerSession = connect(sellerClient, sellerToken);

        BlockingQueue<ChatRealtimeMessage> sellerMessages = new LinkedBlockingQueue<>();
        BlockingQueue<NotificationRealtimeMessage> sellerNotifications = new LinkedBlockingQueue<>();
        BlockingQueue<ChatReadReceipt> buyerReadReceipts = new LinkedBlockingQueue<>();

        sellerSession.subscribe("/user/queue/chat.messages", queueHandler(ChatRealtimeMessage.class, sellerMessages));
        sellerSession.subscribe("/user/queue/notifications", queueHandler(NotificationRealtimeMessage.class, sellerNotifications));
        buyerSession.subscribe("/user/queue/chat.read", queueHandler(ChatReadReceipt.class, buyerReadReceipts));

        buyerSession.send("/app/chat.send", Map.of(
                "chatId", chat.getId(),
                "content", "Mensagem websocket",
                "messageType", "TEXT"
        ));

        ChatRealtimeMessage messageEvent = sellerMessages.poll(10, TimeUnit.SECONDS);
        assertThat(messageEvent).isNotNull();
        assertThat(messageEvent.chatId()).isEqualTo(chat.getId());
        assertThat(messageEvent.content()).isEqualTo("Mensagem websocket");
        assertThat(messageEvent.senderId()).isEqualTo(buyerUserId);

        NotificationRealtimeMessage notificationEvent = sellerNotifications.poll(10, TimeUnit.SECONDS);
        assertThat(notificationEvent).isNotNull();
        assertThat(notificationEvent.type()).isEqualTo("CHAT_NEW_MESSAGE");
        assertThat(notificationEvent.body()).isEqualTo("Mensagem websocket");

        sellerSession.send("/app/chat.read", Map.of("chatId", chat.getId()));

        ChatReadReceipt readReceipt = buyerReadReceipts.poll(10, TimeUnit.SECONDS);
        assertThat(readReceipt).isNotNull();
        assertThat(readReceipt.chatId()).isEqualTo(chat.getId());
        assertThat(readReceipt.readerId()).isEqualTo(sellerUserId);

        buyerSession.disconnect();
        sellerSession.disconnect();
        buyerClient.stop();
        sellerClient.stop();
    }

    private String registerAndLogin(String email) throws Exception {
        String registerPayload = objectMapper.writeValueAsString(Map.of(
                "tenantSlug", "orbyt-demo",
                "fullName", "WebSocket Test User",
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
        String loginResponse = mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(loginResponse).get("accessToken").asText();
    }

    private Seller createSeller(UUID userId) {
        Seller seller = new Seller();
        seller.setTenantId(tenantId);
        seller.setUserId(userId);
        seller.setLegalName("Seller WebSocket LTDA");
        seller.setDocumentNumber("12345678000199");
        seller.setApprovalStatus("APPROVED");
        seller.setReputationScore(BigDecimal.valueOf(5));
        seller.setStatus("ACTIVE");
        return sellerRepository.save(seller);
    }

    private WebSocketStompClient stompClient() {
        Transport transport = new WebSocketTransport(new StandardWebSocketClient());
        SockJsClient sockJsClient = new SockJsClient(java.util.List.of(transport));
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        return stompClient;
    }

    private StompSession connect(WebSocketStompClient stompClient, String token) throws Exception {
        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        CompletableFuture<StompSession> future = stompClient.connectAsync(
                "http://localhost:" + port + "/api/ws",
                new WebSocketHttpHeaders(),
                connectHeaders,
                new StompSessionHandlerAdapter() {
                }
        );
        return future.get(10, TimeUnit.SECONDS);
    }

    private <T> StompFrameHandler queueHandler(Class<T> payloadType, BlockingQueue<T> queue) {
        return new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return payloadType;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                queue.offer(payloadType.cast(payload));
            }
        };
    }
}
