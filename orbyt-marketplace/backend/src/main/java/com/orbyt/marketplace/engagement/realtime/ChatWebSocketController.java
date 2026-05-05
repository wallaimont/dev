package com.orbyt.marketplace.engagement.realtime;

import com.orbyt.marketplace.engagement.application.ChatService;
import com.orbyt.marketplace.engagement.application.NotificationService;
import com.orbyt.marketplace.engagement.domain.Chat;
import com.orbyt.marketplace.engagement.domain.ChatMessage;
import com.orbyt.marketplace.engagement.realtime.dto.ChatReadReceipt;
import com.orbyt.marketplace.engagement.realtime.dto.ChatRealtimeMessage;
import com.orbyt.marketplace.identity.application.AuthUserPrincipal;
import com.orbyt.marketplace.shared.tenant.TenantContext;
import java.security.Principal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;
    private final NotificationService notificationService;
    private final RealtimeMessagingService realtimeMessagingService;
    private final UserTargetResolver userTargetResolver;

    @MessageMapping("/chat.send")
    public void send(ChatSendCommand command, Principal principal) {
        AuthUserPrincipal authUser = extractAuthUser(principal);
        TenantContext.set(authUser.getTenantId());
        try {
            ChatMessage message = chatService.sendMessage(
                    command.chatId(),
                    authUser.getUserId(),
                    command.content(),
                    command.messageType()
            );

            Chat chat = message.getChat();
            ChatRealtimeMessage payload = new ChatRealtimeMessage(
                    chat.getId(),
                    message.getId(),
                    message.getSenderId(),
                    message.getContent(),
                    message.getMessageType(),
                    message.getCreatedAt() != null ? message.getCreatedAt() : OffsetDateTime.now(),
                    chat.getBuyerUnread(),
                    chat.getSellerUnread()
            );

                UUID recipientCandidateId = authUser.getUserId().equals(chat.getBuyerId())
                    ? chat.getSellerId()
                    : chat.getBuyerId();
                UUID recipientUserId = userTargetResolver.resolveUserId(recipientCandidateId).orElse(recipientCandidateId);

            realtimeMessagingService.pushChatMessage(recipientUserId, payload);
            realtimeMessagingService.pushChatMessage(authUser.getUserId(), payload);

            notificationService.createNotification(
                    recipientUserId,
                    "CHAT_NEW_MESSAGE",
                    "Nova mensagem no chat",
                    message.getContent(),
                    "INTERNAL"
            );
        } finally {
            TenantContext.clear();
        }
    }

    @MessageMapping("/chat.read")
    public void markAsRead(ChatReadCommand command, Principal principal) {
        AuthUserPrincipal authUser = extractAuthUser(principal);
        TenantContext.set(authUser.getTenantId());
        try {
            Chat chat = chatService.markAsReadAndReturnChat(command.chatId(), authUser.getUserId());
            ChatReadReceipt receipt = new ChatReadReceipt(
                    chat.getId(),
                    authUser.getUserId(),
                    chat.getBuyerUnread(),
                    chat.getSellerUnread(),
                    OffsetDateTime.now()
            );
            realtimeMessagingService.pushChatReadReceipt(chat.getBuyerId(), receipt);
            realtimeMessagingService.pushChatReadReceipt(chat.getSellerId(), receipt);
        } finally {
            TenantContext.clear();
        }
    }

    private AuthUserPrincipal extractAuthUser(Principal principal) {
        if (principal instanceof UsernamePasswordAuthenticationToken authentication
                && authentication.getPrincipal() instanceof AuthUserPrincipal authUser) {
            return authUser;
        }
        throw new IllegalStateException("Invalid websocket principal");
    }

    public record ChatSendCommand(UUID chatId, String content, String messageType) {
    }

    public record ChatReadCommand(UUID chatId) {
    }
}
