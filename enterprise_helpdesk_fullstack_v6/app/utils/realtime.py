import json
from collections import defaultdict
from typing import Any

from fastapi import WebSocket


class ConnectionManager:
    def __init__(self):
        self.connections: dict[str, set[WebSocket]] = defaultdict(set)

    async def connect(self, channel: str, websocket: WebSocket):
        await websocket.accept()
        self.connections[channel].add(websocket)

    def disconnect(self, channel: str, websocket: WebSocket):
        self.connections[channel].discard(websocket)
        if not self.connections[channel]:
            self.connections.pop(channel, None)

    async def broadcast(self, channel: str, payload: dict[str, Any]):
        stale = []
        message = json.dumps(payload)
        for ws in list(self.connections.get(channel, set())):
            try:
                await ws.send_text(message)
            except Exception:
                stale.append(ws)
        for ws in stale:
            self.disconnect(channel, ws)


manager = ConnectionManager()
