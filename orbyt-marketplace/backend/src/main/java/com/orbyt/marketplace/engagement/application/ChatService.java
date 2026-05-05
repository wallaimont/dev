package com.orbyt.marketplace.engagement.application;

import com.orbyt.marketplace.engagement.domain.Chat;
import com.orbyt.marketplace.engagement.domain.ChatMessage;
import com.orbyt.marketplace.engagement.repository.ChatRepository;
import com.orbyt.marketplace.shared.tenant.TenantContext;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    @Transactional(readOnly = true)
    public List<Chat> getUserChats(UUID userId, String role) {
        UUID tenantId = TenantContext.require();
        if ("SELLER".equalsIgnoreCase(role)) {
            return chatRepository.findByTenantIdAndSellerIdOrderByLastMessageAtDesc(tenantId, userId);
        }
        return chatRepository.findByTenantIdAndBuyerIdOrderByLastMessageAtDesc(tenantId, userId);
    }

    @Transactional
    public Chat getOrCreateChat(UUID buyerId, UUID sellerId) {
        UUID tenantId = TenantContext.require();
        return chatRepository.findByTenantIdAndBuyerIdAndSellerId(tenantId, buyerId, sellerId)
                .orElseGet(() -> {
                    Chat chat = new Chat();
                    chat.setTenantId(tenantId);
                    chat.setBuyerId(buyerId);
                    chat.setSellerId(sellerId);
                    chat.setStatus("ACTIVE");
                    return chatRepository.save(chat);
                });
    }

    @Transactional
    public ChatMessage sendMessage(UUID chatId, UUID senderId, String content, String messageType) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat not found"));

        ChatMessage msg = new ChatMessage();
        msg.setChat(chat);
        msg.setTenantId(chat.getTenantId());
        msg.setSenderId(senderId);
        msg.setContent(content);
        msg.setMessageType(messageType != null ? messageType : "TEXT");
        msg.setStatus("ACTIVE");

        chat.getMessages().add(msg);
        chat.setLastMessageAt(OffsetDateTime.now());

        if (senderId.equals(chat.getBuyerId())) {
            chat.setSellerUnread(chat.getSellerUnread() + 1);
        } else {
            chat.setBuyerUnread(chat.getBuyerUnread() + 1);
        }

        chatRepository.save(chat);
        return msg;
    }

    @Transactional
    public void markAsRead(UUID chatId, UUID userId) {
        markAsReadAndReturnChat(chatId, userId);
    }

    @Transactional
    public Chat markAsReadAndReturnChat(UUID chatId, UUID userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat not found"));
        if (userId.equals(chat.getBuyerId())) {
            chat.setBuyerUnread(0);
        } else {
            chat.setSellerUnread(0);
        }
        chat.getMessages().stream()
                .filter(m -> !m.getSenderId().equals(userId) && !m.isRead())
                .forEach(m -> {
                    m.setRead(true);
                    m.setReadAt(OffsetDateTime.now());
                });
        return chatRepository.save(chat);
    }
}
