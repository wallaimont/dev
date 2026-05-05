import { api } from './client'
import type { User } from '../types'

export async function getAssignableUsers(companyId?: number) {
  const query = new URLSearchParams()
  query.append('role', 'analyst')
  if (companyId) query.append('company_id', String(companyId))
  const { data } = await api.get<User[]>(`/users?${query.toString()}`)
  return data
}
