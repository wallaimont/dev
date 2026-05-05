
import { api } from './client'
import type { CreateCommentPayload, CreateTicketPayload, Ticket, TicketActivity, TicketAttachment, TicketStatus, UpdateTicketPayload } from '../types'

export async function getTickets() {
  const { data } = await api.get<Ticket[]>('/tickets')
  return data
}

export async function getTicket(ticketId: number) {
  const { data } = await api.get<Ticket>(`/tickets/${ticketId}`)
  return data
}

export async function getTicketActivity(ticketId: number) {
  const { data } = await api.get<TicketActivity[]>(`/tickets/${ticketId}/activity`)
  return data
}

export async function createTicket(payload: CreateTicketPayload) {
  const { data } = await api.post<Ticket>('/tickets', payload)
  return data
}

export async function updateTicket(ticketId: number, payload: UpdateTicketPayload) {
  const { data } = await api.put<Ticket>(`/tickets/${ticketId}`, payload)
  return data
}

export async function requestCloseTicket(ticketId: number) {
  const { data } = await api.post<Ticket>(`/tickets/${ticketId}/request-close`)
  return data
}

export async function approveCloseTicket(ticketId: number) {
  const { data } = await api.post<Ticket>(`/tickets/${ticketId}/approve-close`)
  return data
}

export async function addComment(ticketId: number, payload: CreateCommentPayload) {
  const { data } = await api.post(`/tickets/${ticketId}/comments`, payload)
  return data
}

export async function uploadAttachment(ticketId: number, file: File) {
  const formData = new FormData()
  formData.append('file', file)
  const { data } = await api.post<TicketAttachment>(`/tickets/${ticketId}/attachments`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
  return data
}

export async function deleteAttachment(ticketId: number, attachmentId: number) {
  await api.delete(`/tickets/${ticketId}/attachments/${attachmentId}`)
}

export const statusOptions: { value: TicketStatus; label: string }[] = [
  { value: 'open', label: 'Aberto' },
  { value: 'in_progress', label: 'Em andamento' },
  { value: 'waiting_client', label: 'Aguardando cliente' },
  { value: 'resolved', label: 'Resolvido' },
  { value: 'closed', label: 'Fechado' },
]
