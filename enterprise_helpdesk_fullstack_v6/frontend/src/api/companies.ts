import { api } from './client'
import type { Company } from '../types'

export async function getCompanies() {
  const { data } = await api.get<Company[]>('/companies')
  return data
}
