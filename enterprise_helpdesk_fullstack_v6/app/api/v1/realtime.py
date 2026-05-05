from fastapi import APIRouter, WebSocket, WebSocketDisconnect

from app.utils.realtime import manager

router = APIRouter(tags=["Realtime"])


@router.websocket("/ws/events")
async def events_socket(websocket: WebSocket):
    channel = websocket.query_params.get("channel", "tickets")
    await manager.connect(channel, websocket)
    try:
        while True:
            await websocket.receive_text()
    except WebSocketDisconnect:
        manager.disconnect(channel, websocket)
    except Exception:
        manager.disconnect(channel, websocket)
