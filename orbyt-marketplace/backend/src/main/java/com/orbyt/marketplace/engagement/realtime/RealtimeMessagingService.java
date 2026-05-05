package com.orbyt.marketplace.engagement.realtime;

import com.orbyt.marketplace.engagement.realtime.dto.ChatReadReceipt;
import com.orbyt.marketplace.engagement.realtime.dto.ChatRealtimeMessage;
import com.orbyt.marketplace.engagement.realtime.dto.NotificationRealtimeMessage;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RealtimeMessagingService {

    private static final String CHAT_DESTINATION = "/queue/chat.messages";
    private static final String CHAT_READ_DESTINATION = "/queue/chat.read";
    private static final String NOTIFICATION_DESTINATION = "/queue/notifications";

    private final SimpMessagingTemplate messagingTemplate;
    private final UserTargetResolver userTargetResolver;

    public void pushChatMessage(UUID recipientUserId, ChatRealtimeMessage payload) {
        sendToUser(recipientUserId, CHAT_DESTINATION, payload);
    }

    public void pushChatReadReceipt(UUID recipientUserId, ChatReadReceipt payload) {
        sendToUser(recipientUserId, CHAT_READ_DESTINATION, payload);
    }

    public void pushNotification(UUID recipientUserId, NotificationRealtimeMessage payload) {
        sendToUser(recipientUserId, NOTIFICATION_DESTINATION, payload);
    }

    private void sendToUser(UUID recipientUserId, String destination, Object payload) {
        userTargetResolver.resolveUsername(recipientUserId)
                .ifPresentOrElse(
                        username -> messagingTemplate.convertAndSendToUser(username, destination, payload),
                        () -> log.warn("Realtime destination user not found: userId={}", recipientUserId)
                );
    }
}
