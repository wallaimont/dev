'use client'

import { useEffect } from 'react'
import Link from 'next/link'
import { useRouter } from 'next/navigation'
import { useAuthStore } from '@/store'

export default function DashboardPage() {
  const router = useRouter()
  const user = useAuthStore((state) => state.user)
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated)
  const isSeller = useAuthStore((state) => state.isSeller)
  const clearAuth = useAuthStore((state) => state.clearAuth)

  const handleSwitchAccount = () => {
    clearAuth()
    router.push('/login')
  }

  useEffect(() => {
    if (!isAuthenticated) {
      router.replace('/login')
      return
    }

    if (isSeller()) {
      router.replace('/seller/dashboard')
    }
  }, [isAuthenticated, isSeller, router])

  if (!isAuthenticated) {
    return (
      <main className="min-h-screen flex items-center justify-center bg-[var(--bg-page)] text-[var(--text-secondary)]">
        Redirecionando para login...
      </main>
    )
  }

  return (
    <main className="min-h-screen bg-[var(--bg-page)] px-6 py-10">
      <div className="mx-auto max-w-3xl rounded-[20px] border border-[var(--border-light)] bg-white p-8 shadow-[var(--shadow-card)]">
        <p className="text-sm font-medium text-nexus-600">Area do cliente</p>
        <h1 className="mt-2 font-display text-3xl font-700 tracking-tight text-[var(--text-primary)]">
          Ola, {user?.email ?? 'cliente'}
        </h1>
        <p className="mt-3 text-[var(--text-secondary)]">
          Sua conta foi autenticada. O front ainda nao possui todas as paginas do comprador, entao esta tela serve como destino seguro de pos-login.
        </p>
        <div className="mt-6 flex gap-3">
          <Link href="/seller/dashboard" className="btn-primary">Painel do vendedor</Link>
          <button onClick={handleSwitchAccount} className="btn-secondary">Trocar de conta</button>
        </div>
      </div>
    </main>
  )
}