package com.orbyt.marketplace.engagement.api;

import com.orbyt.marketplace.engagement.api.dto.SendMessageRequest;
import com.orbyt.marketplace.engagement.application.ChatService;
import com.orbyt.marketplace.engagement.application.NotificationService;
import com.orbyt.marketplace.engagement.domain.Chat;
import com.orbyt.marketplace.engagement.domain.ChatMessage;
import com.orbyt.marketplace.engagement.realtime.RealtimeMessagingService;
import com.orbyt.marketplace.engagement.realtime.UserTargetResolver;
import com.orbyt.marketplace.engagement.realtime.dto.ChatReadReceipt;
import com.orbyt.marketplace.engagement.realtime.dto.ChatRealtimeMessage;
import com.orbyt.marketplace.identity.application.AuthUserPrincipal;
import com.orbyt.marketplace.shared.tenant.TenantContext;
import jakarta.validation.Valid;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final NotificationService notificationService;
    private final RealtimeMessagingService realtimeMessagingService;
    private final UserTargetResolver userTargetResolver;

    @GetMapping
    public List<Chat> getMyChats(@AuthenticationPrincipal AuthUserPrincipal principal,
                                 @RequestParam(defaultValue = "BUYER") String role) {
        return chatService.getUserChats(principal.getUserId(), role);
    }

    @PostMapping("/with/{sellerId}")
    public Chat startChat(@AuthenticationPrincipal AuthUserPrincipal principal,
                          @PathVariable UUID sellerId) {
        return chatService.getOrCreateChat(principal.getUserId(), sellerId);
    }

    @PostMapping("/{chatId}/messages")
    public ChatMessage sendMessage(@AuthenticationPrincipal AuthUserPrincipal principal,
                                   @PathVariable UUID chatId,
                                   @Valid @RequestBody SendMessageRequest request) {
        ChatMessage message = chatService.sendMessage(chatId, principal.getUserId(), request.content(), request.messageType());
        Chat chat = message.getChat();
        UUID recipientCandidateId = principal.getUserId().equals(chat.getBuyerId()) ? chat.getSellerId() : chat.getBuyerId();
        UUID recipientUserId = userTargetResolver.resolveUserId(recipientCandidateId).orElse(recipientCandidateId);

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

        realtimeMessagingService.pushChatMessage(recipientUserId, payload);
        realtimeMessagingService.pushChatMessage(principal.getUserId(), payload);

        notificationService.createNotification(
                recipientUserId,
                "CHAT_NEW_MESSAGE",
                "Nova mensagem no chat",
                message.getContent(),
                "INTERNAL"
        );
        return message;
    }

    @PutMapping("/{chatId}/read")
    public void markAsRead(@AuthenticationPrincipal AuthUserPrincipal principal,
                           @PathVariable UUID chatId) {
        TenantContext.set(principal.getTenantId());
        try {
            Chat chat = chatService.markAsReadAndReturnChat(chatId, principal.getUserId());
            ChatReadReceipt receipt = new ChatReadReceipt(
                    chat.getId(),
                    principal.getUserId(),
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
}
