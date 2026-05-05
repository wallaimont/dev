import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

function getWebSocketUrl() {
  const apiBase = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080/api";
  return apiBase.endsWith("/") ? `${apiBase}ws` : `${apiBase}/ws`;
}

export function createStompClient(accessToken: string) {
  const tenantKey = process.env.NEXT_PUBLIC_TENANT_KEY ?? "orbyt-demo";

  return new Client({
    reconnectDelay: 4000,
    connectHeaders: {
      Authorization: `Bearer ${accessToken}`,
      "X-Tenant-Id": tenantKey
    },
    webSocketFactory: () => new SockJS(getWebSocketUrl()),
    debug: () => {
      // Keep websocket logs disabled in browser console by default.
    }
  });
}
