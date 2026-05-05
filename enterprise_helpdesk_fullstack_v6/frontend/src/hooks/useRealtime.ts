import { useEffect } from 'react'
import { useNotify } from './useNotify'

const WS_URL = import.meta.env.VITE_WS_URL || 'ws://localhost:8000/ws/events?channel=tickets'

export function useRealtime(onEvent?: (event: Record<string, unknown>) => void) {
  const { notify } = useNotify()

  useEffect(() => {
    const socket = new WebSocket(WS_URL)

    socket.onopen = () => {
      notify('Canal em tempo real conectado.', 'info')
    }

    socket.onmessage = (message) => {
      try {
        const event = JSON.parse(message.data) as Record<string, unknown>
        onEvent?.(event)
        const type = String(event.type || '')
        if (type === 'ticket_created') notify(`Novo chamado #${event.ticket_id} criado.`, 'info')
        if (type === 'ticket_updated') notify(`Chamado #${event.ticket_id} atualizado.`, 'info')
        if (type === 'comment_added') notify(`Novo comentário no chamado #${event.ticket_id}.`, 'info')
        if (type === 'attachment_uploaded') notify(`Novo anexo enviado no chamado #${event.ticket_id}.`, 'info')
      } catch {
        // ignore malformed messages
      }
    }

    socket.onclose = () => {
      notify('Canal em tempo real desconectado.', 'error')
    }

    const heartbeat = window.setInterval(() => {
      if (socket.readyState === WebSocket.OPEN) {
        socket.send('ping')
      }
    }, 20000)

    return () => {
      window.clearInterval(heartbeat)
      socket.close()
    }
  }, [notify, onEvent])
}
