
import { api } from './client'
import type { AnalystPerformance, DashboardExecutive, DashboardMetrics } from '../types'

export async function getDashboard() {
  const { data } = await api.get<DashboardMetrics>('/dashboard')
  return data
}

export async function getExecutiveDashboard() {
  const { data } = await api.get<DashboardExecutive>('/dashboard/executive')
  return data
}

export async function getAnalystPerformance() {
  const { data } = await api.get<AnalystPerformance[]>('/dashboard/analysts')
  return data
}

async function downloadBlob(path: string, filename: string) {
  const { data } = await api.get(path, { responseType: 'blob' })
  const url = window.URL.createObjectURL(new Blob([data]))
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  document.body.appendChild(link)
  link.click()
  link.remove()
  window.URL.revokeObjectURL(url)
}

export async function downloadAnalystsCsv() {
  await downloadBlob('/dashboard/exports/analysts.csv', 'analyst_performance.csv')
}

export async function downloadAnalystsPdf() {
  await downloadBlob('/dashboard/exports/analysts.pdf', 'analyst_performance.pdf')
}
