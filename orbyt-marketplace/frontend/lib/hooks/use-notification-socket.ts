"use client";

import { IMessage, StompSubscription } from "@stomp/stompjs";
import { useEffect, useRef } from "react";
import { createStompClient } from "@/lib/websocket-client";
import { useAuthStore } from "@/store/auth-store";

export type RealtimeNotificationEvent = {
  notificationId: string;
  type: string;
  title: string;
  body: string;
  read: boolean;
  createdAt: string;
  unreadCount: number;
};

export function useNotificationSocket(onNotification: (event: RealtimeNotificationEvent) => void) {
  const accessToken = useAuthStore((state) => state.accessToken);
  const callbackRef = useRef(onNotification);

  useEffect(() => {
    callbackRef.current = onNotification;
  }, [onNotification]);

  useEffect(() => {
    if (!accessToken) {
      return;
    }

    const client = createStompClient(accessToken);
    let subscription: StompSubscription | null = null;

    client.onConnect = () => {
      subscription = client.subscribe("/user/queue/notifications", (message: IMessage) => {
        try {
          const payload = JSON.parse(message.body) as RealtimeNotificationEvent;
          callbackRef.current(payload);
        } catch {
          // Ignore malformed payloads to keep UI resilient.
        }
      });
    };

    client.activate();

    return () => {
      if (subscription) {
        subscription.unsubscribe();
      }
      void client.deactivate();
    };
  }, [accessToken]);
}
