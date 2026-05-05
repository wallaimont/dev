"use client";

import { Client, IMessage, StompSubscription } from "@stomp/stompjs";
import { useCallback, useEffect, useRef } from "react";
import { createStompClient } from "@/lib/websocket-client";
import { useAuthStore } from "@/store/auth-store";

export type RealtimeChatMessageEvent = {
  chatId: string;
  messageId: string;
  senderId: string;
  content: string;
  messageType: string;
  sentAt: string;
  buyerUnread: number;
  sellerUnread: number;
};

export type RealtimeChatReadEvent = {
  chatId: string;
  readerId: string;
  buyerUnread: number;
  sellerUnread: number;
  readAt: string;
};

type UseChatSocketParams = {
  onMessage?: (event: RealtimeChatMessageEvent) => void;
  onRead?: (event: RealtimeChatReadEvent) => void;
};

export function useChatSocket({ onMessage, onRead }: UseChatSocketParams = {}) {
  const accessToken = useAuthStore((state) => state.accessToken);
  const clientRef = useRef<Client | null>(null);
  const onMessageRef = useRef(onMessage);
  const onReadRef = useRef(onRead);

  useEffect(() => {
    onMessageRef.current = onMessage;
  }, [onMessage]);

  useEffect(() => {
    onReadRef.current = onRead;
  }, [onRead]);

  useEffect(() => {
    if (!accessToken) {
      return;
    }

    const client = createStompClient(accessToken);
    clientRef.current = client;

    let messageSub: StompSubscription | null = null;
    let readSub: StompSubscription | null = null;

    client.onConnect = () => {
      messageSub = client.subscribe("/user/queue/chat.messages", (frame: IMessage) => {
        if (!onMessageRef.current) {
          return;
        }
        try {
          const payload = JSON.parse(frame.body) as RealtimeChatMessageEvent;
          onMessageRef.current(payload);
        } catch {
          // Ignore malformed payloads from transport.
        }
      });

      readSub = client.subscribe("/user/queue/chat.read", (frame: IMessage) => {
        if (!onReadRef.current) {
          return;
        }
        try {
          const payload = JSON.parse(frame.body) as RealtimeChatReadEvent;
          onReadRef.current(payload);
        } catch {
          // Ignore malformed payloads from transport.
        }
      });
    };

    client.activate();

    return () => {
      messageSub?.unsubscribe();
      readSub?.unsubscribe();
      clientRef.current = null;
      void client.deactivate();
    };
  }, [accessToken]);

  const sendMessage = useCallback((chatId: string, content: string, messageType = "TEXT") => {
    const client = clientRef.current;
    if (!client || !client.connected) {
      return false;
    }
    client.publish({
      destination: "/app/chat.send",
      body: JSON.stringify({ chatId, content, messageType })
    });
    return true;
  }, []);

  const markAsRead = useCallback((chatId: string) => {
    const client = clientRef.current;
    if (!client || !client.connected) {
      return false;
    }
    client.publish({
      destination: "/app/chat.read",
      body: JSON.stringify({ chatId })
    });
    return true;
  }, []);

  return {
    sendMessage,
    markAsRead
  };
}
