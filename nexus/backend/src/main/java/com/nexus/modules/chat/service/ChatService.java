package com.nexus.modules.chat.service;

import com.nexus.modules.chat.dto.ChatMessageRequest;
import com.nexus.modules.chat.dto.ChatMessageResponse;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class ChatService {

    public ChatMessageResponse sendMessage(ChatMessageRequest req, UUID senderId) {
        // TODO: persist and return
        return null;
    }

    public List<?> getChats(UUID userId) {
        return Collections.emptyList();
    }

    public List<ChatMessageResponse> getMessages(UUID chatId, UUID userId) {
        return Collections.emptyList();
    }
}
