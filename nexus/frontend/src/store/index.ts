// ============================================================
// NEXUS — Zustand Stores
// ============================================================

import { create } from 'zustand'
import { persist, devtools } from 'zustand/middleware'
import type { AuthResponse, CartResponse } from '@/lib/api/client'

const AUTH_COOKIE_MAX_AGE = 60 * 60 * 24 * 30

function syncAuthCookies(response: AuthResponse) {
  fetch('/api/auth/session', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      accessToken: response.accessToken,
      refreshToken: response.refreshToken,
      roles: response.roles,
    }),
  }).catch(() => {})
}

function clearAuthCookies() {
  fetch('/api/auth/session', { method: 'DELETE' }).catch(() => {})
}

// ============================================================
// AUTH STORE
// ============================================================
interface AuthState {
  user: AuthUser | null
  accessToken: string | null
  refreshToken: string | null
  isAuthenticated: boolean
  isLoading: boolean

  // Actions
  setAuth: (response: AuthResponse) => void
  clearAuth: () => void
  setLoading: (v: boolean) => void
  hasRole: (role: string) => boolean
  isSeller: () => boolean
  isAdmin: () => boolean
}

interface AuthUser {
  userId: string
  tenantId: string
  email: string
  roles: string[]
}

export const useAuthStore = create<AuthState>()(
  devtools(
    persist(
      (set, get) => ({
        user: null,
        accessToken: null,
        refreshToken: null,
        isAuthenticated: false,
        isLoading: false,

        setAuth: (response) => {
          if (typeof window !== 'undefined') {
            localStorage.setItem('nexus_access_token',  response.accessToken)
            localStorage.setItem('nexus_refresh_token', response.refreshToken)
            syncAuthCookies(response)
          }
          set({
            user: {
              userId:   response.userId,
              tenantId: response.tenantId,
              email:    response.email,
              roles:    response.roles,
            },
            accessToken:     response.accessToken,
            refreshToken:    response.refreshToken,
            isAuthenticated: true,
          })
        },

        clearAuth: () => {
          if (typeof window !== 'undefined') {
            localStorage.removeItem('nexus_access_token')
            localStorage.removeItem('nexus_refresh_token')
            clearAuthCookies()
          }
          set({ user: null, accessToken: null, refreshToken: null, isAuthenticated: false })
        },

        setLoading: (v) => set({ isLoading: v }),

        hasRole: (role) => get().user?.roles.includes(role) ?? false,
        isSeller: () => get().user?.roles.includes('SELLER') ?? false,
        isAdmin:  () =>
          get().user?.roles.some(r => r === 'TENANT_ADMIN' || r === 'SUPER_ADMIN') ?? false,
      }),
      {
        name: 'nexus-auth',
        partialize: (state) => ({
          user:         state.user,
          accessToken:  state.accessToken,
          refreshToken: state.refreshToken,
          isAuthenticated: state.isAuthenticated,
        }),
      }
    )
  )
)

// ============================================================
// CART STORE
// ============================================================
interface CartState {
  cart: CartResponse | null
  isOpen: boolean
  sessionId: string

  // Actions
  setCart:       (cart: CartResponse) => void
  clearCart:     () => void
  openCart:      () => void
  closeCart:     () => void
  toggleCart:    () => void
  getItemCount:  () => number
  getSubtotal:   () => number
}

// Generate persistent session ID for guests
const generateSessionId = () =>
  typeof window !== 'undefined'
    ? localStorage.getItem('nexus_session_id') ?? (() => {
        const id = `sess_${Date.now()}_${Math.random().toString(36).slice(2)}`
        localStorage.setItem('nexus_session_id', id)
        return id
      })()
    : 'ssr-session'

export const useCartStore = create<CartState>()(
  devtools((set, get) => ({
    cart: null,
    isOpen: false,
    sessionId: generateSessionId(),

    setCart:    (cart)    => set({ cart }),
    clearCart:  ()        => set({ cart: null }),
    openCart:   ()        => set({ isOpen: true }),
    closeCart:  ()        => set({ isOpen: false }),
    toggleCart: ()        => set((s) => ({ isOpen: !s.isOpen })),
    getItemCount: ()      => get().cart?.itemCount ?? 0,
    getSubtotal:  ()      => get().cart?.subtotal ?? 0,
  }))
)

// ============================================================
// UI STORE (toasts, modals, etc.)
// ============================================================
interface UIState {
  theme: 'light' | 'dark'
  sidebarOpen: boolean
  modalOpen: string | null

  setTheme:    (t: 'light' | 'dark') => void
  toggleTheme: () => void
  setSidebar:  (v: boolean) => void
  openModal:   (id: string) => void
  closeModal:  () => void
}

export const useUIStore = create<UIState>()(
  persist(
    (set, get) => ({
      theme:       'light',
      sidebarOpen: false,
      modalOpen:   null,

      setTheme:    (t) => set({ theme: t }),
      toggleTheme: ()  => set({ theme: get().theme === 'light' ? 'dark' : 'light' }),
      setSidebar:  (v) => set({ sidebarOpen: v }),
      openModal:   (id)=> set({ modalOpen: id }),
      closeModal:  ()  => set({ modalOpen: null }),
    }),
    { name: 'nexus-ui', partialize: (s) => ({ theme: s.theme }) }
  )
)
