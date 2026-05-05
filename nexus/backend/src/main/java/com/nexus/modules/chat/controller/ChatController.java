package com.nexus.modules.chat.controller;

import com.nexus.modules.chat.dto.ChatMessageRequest;
import com.nexus.modules.chat.dto.ChatMessageResponse;
import com.nexus.modules.chat.service.ChatService;
import com.nexus.shared.security.NexusPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageRequest req,
                             @AuthenticationPrincipal NexusPrincipal principal) {
        ChatMessageResponse response = chatService.sendMessage(req, principal.getUserId());

        messagingTemplate.convertAndSendToUser(
            response.getRecipientId().toString(), "/queue/messages", response);
        messagingTemplate.convertAndSendToUser(
            principal.getUserId().toString(), "/queue/messages", response);
    }

    @GetMapping("/api/v1/chats")
    @ResponseBody
    public List<?> getMyChats(@AuthenticationPrincipal NexusPrincipal principal) {
        return chatService.getChats(principal.getUserId());
    }

    @GetMapping("/api/v1/chats/{chatId}/messages")
    @ResponseBody
    public List<ChatMessageResponse> getMessages(
            @PathVariable UUID chatId,
            @AuthenticationPrincipal NexusPrincipal principal) {
        return chatService.getMessages(chatId, principal.getUserId());
    }
}
