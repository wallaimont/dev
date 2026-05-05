import { useContext } from 'react'
import { NotificationsContext } from '../contexts/NotificationsContext'

export function useNotify() {
  const context = useContext(NotificationsContext)
  if (!context) {
    throw new Error('useNotify deve ser usado dentro de NotificationsProvider')
  }
  return context
}
