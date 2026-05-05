import { createContext, useEffect, useMemo, useState } from 'react'
import { getCurrentUser, loginRequest } from '../api/auth'
import type { LoginForm, User } from '../types'

interface AuthContextValue {
  user: User | null
  isAuthenticated: boolean
  isLoading: boolean
  login: (payload: LoginForm) => Promise<void>
  logout: () => void
}

export const AuthContext = createContext<AuthContextValue | undefined>(undefined)

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const token = localStorage.getItem('helpdesk_token')

    if (!token) {
      setIsLoading(false)
      return
    }

    getCurrentUser()
      .then((currentUser) => setUser(currentUser))
      .catch(() => {
        localStorage.removeItem('helpdesk_token')
        localStorage.removeItem('helpdesk_refresh_token')
        setUser(null)
      })
      .finally(() => setIsLoading(false))
  }, [])

  async function login(payload: LoginForm) {
    const data = await loginRequest(payload)
    localStorage.setItem('helpdesk_token', data.access_token)
    localStorage.setItem('helpdesk_refresh_token', data.refresh_token)
    const currentUser = await getCurrentUser()
    setUser(currentUser)
  }

  function logout() {
    localStorage.removeItem('helpdesk_token')
    localStorage.removeItem('helpdesk_refresh_token')
    setUser(null)
  }

  const value = useMemo(
    () => ({
      user,
      isAuthenticated: !!user,
      isLoading,
      login,
      logout,
    }),
    [user, isLoading],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
