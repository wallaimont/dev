import { api } from './client'
import type { AuthTokens, LoginForm, User } from '../types'

export async function loginRequest(payload: LoginForm) {
  const body = new URLSearchParams()
  body.append('username', payload.email)
  body.append('password', payload.password)

  const { data } = await api.post<AuthTokens>('/auth/login', body, {
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
  })

  return data
}

export async function refreshSession(refreshToken: string) {
  const { data } = await api.post<AuthTokens>('/auth/refresh', { refresh_token: refreshToken }, { headers: { skipAuthRefresh: '1' as any } })
  return data
}

export async function getCurrentUser() {
  const { data } = await api.get<User>('/auth/me')
  return data
}
