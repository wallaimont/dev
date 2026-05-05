
export type UserRole = 'admin' | 'manager' | 'analyst' | 'client'
export type TicketStatus = 'open' | 'in_progress' | 'waiting_client' | 'resolved' | 'closed'
export type TicketPriority = 'low' | 'medium' | 'high' | 'critical'

export interface User {
  id: number
  full_name: string
  email: string
  role: UserRole
  company_id?: number | null
  is_active: boolean
  created_at: string
}

export interface LoginForm {
  email: string
  password: string
}

export interface AuthTokens {
  access_token: string
  refresh_token: string
  token_type: string
}

export interface DashboardMetrics {
  total_tickets: number
  open_tickets: number
  in_progress_tickets: number
  resolved_tickets: number
  overdue_tickets: number
  critical_tickets: number
}

export interface AnalystPerformance {
  analyst_id: number
  analyst_name: string
  assigned_total: number
  resolved_total: number
  overdue_total: number
  avg_resolution_hours: number
  active_load: number
}

export interface DashboardExecutive {
  totals: DashboardMetrics
  avg_sla_hours: number
  avg_resolution_hours: number
  status_breakdown: { status: TicketStatus; total: number }[]
  trends: { day: string; opened: number; resolved: number }[]
  analyst_performance: AnalystPerformance[]
}

export interface Company {
  id: number
  name: string
  segment?: string | null
  active: boolean
  created_at: string
}

export interface TicketComment {
  id: number
  content: string
  created_at: string
  author: User
}

export interface TicketAttachment {
  id: number
  ticket_id: number
  original_name: string
  stored_name: string
  file_path: string
  content_type?: string | null
  size_bytes: number
  uploaded_by_id: number
  created_at: string
}

export interface TicketActivity {
  id: number
  action: string
  description?: string | null
  performed_by: string
  created_at: string
}

export interface Ticket {
  id: number
  title: string
  description: string
  status: TicketStatus
  priority: TicketPriority
  company_id: number
  created_by_id: number
  assigned_to_id?: number | null
  closure_requested_by_id?: number | null
  closure_approved_by_id?: number | null
  sla_hours: number
  due_at?: string | null
  resolved_at?: string | null
  closure_requested_at?: string | null
  closure_approved_at?: string | null
  created_at: string
  updated_at: string
  comments: TicketComment[]
  attachments: TicketAttachment[]
}

export interface CreateTicketPayload {
  title: string
  description: string
  priority: TicketPriority
  company_id: number
  assigned_to_id?: number | null
  sla_hours?: number
}

export interface UpdateTicketPayload {
  title?: string
  description?: string
  status?: TicketStatus
  priority?: TicketPriority
  assigned_to_id?: number | null
  sla_hours?: number
}

export interface CreateCommentPayload {
  content: string
}

export interface TicketFilters {
  search: string
  status: 'all' | TicketStatus
  priority: 'all' | TicketPriority
  companyId: number | 'all'
}

export type TicketsViewMode = 'table' | 'kanban'
export type ThemeMode = 'dark' | 'light'
