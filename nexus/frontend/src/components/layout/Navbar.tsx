'use client'

import Link from 'next/link'
import { useRouter } from 'next/navigation'
import { useAuthStore, useCartStore } from '@/store'
import { ShoppingCart, User, LogOut, Search, Package, Heart, Bell, Settings, Store } from 'lucide-react'
import { useState, useRef, useEffect } from 'react'

export function Navbar() {
  const router = useRouter()
  const user = useAuthStore(s => s.user)
  const isAuthenticated = useAuthStore(s => s.isAuthenticated)
  const clearAuth = useAuthStore(s => s.clearAuth)
  const hasRole = useAuthStore(s => s.hasRole)
  const itemCount = useCartStore(s => s.getItemCount())
  const toggleCart = useCartStore(s => s.toggleCart)
  const [menuOpen, setMenuOpen] = useState(false)
  const [searchQuery, setSearchQuery] = useState('')
  const menuRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    const handler = (e: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(e.target as Node)) setMenuOpen(false)
    }
    document.addEventListener('mousedown', handler)
    return () => document.removeEventListener('mousedown', handler)
  }, [])

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault()
    if (searchQuery.trim()) router.push(`/products?q=${encodeURIComponent(searchQuery.trim())}`)
  }

  const handleLogout = () => {
    clearAuth()
    setMenuOpen(false)
    router.push('/login')
  }

  return (
    <nav className="sticky top-0 z-50 bg-white border-b border-[var(--border-light)]">
      <div className="max-w-7xl mx-auto px-4 h-16 flex items-center gap-4">
        {/* Logo */}
        <Link href="/" className="flex items-center gap-2 shrink-0">
          <div className="w-8 h-8 bg-nexus-900 rounded-lg flex items-center justify-center">
            <span className="font-display font-[800] text-sm text-white">N</span>
          </div>
          <span className="font-display text-lg font-[800] tracking-tight hidden sm:block">
            NEX<span className="text-nexus-500">US</span>
          </span>
        </Link>

        {/* Search */}
        <form onSubmit={handleSearch} className="flex-1 max-w-xl">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-[var(--text-tertiary)]" />
            <input
              type="text"
              placeholder="Buscar produtos, marcas, categorias..."
              value={searchQuery}
              onChange={e => setSearchQuery(e.target.value)}
              className="w-full pl-10 pr-4 py-2 text-sm bg-[var(--bg-subtle)] border border-transparent rounded-xl
                         placeholder-[var(--text-tertiary)] focus:outline-none focus:bg-white focus:border-nexus-300
                         focus:ring-2 focus:ring-nexus-100 transition-all"
            />
          </div>
        </form>

        {/* Actions */}
        <div className="flex items-center gap-1">
          {isAuthenticated ? (
            <>
              {/* Cart */}
              <button onClick={toggleCart} className="btn-icon relative">
                <ShoppingCart className="w-5 h-5" />
                {itemCount > 0 && (
                  <span className="absolute -top-1 -right-1 w-4.5 h-4.5 bg-accent-500 text-white text-[10px] font-bold rounded-full flex items-center justify-center">
                    {itemCount}
                  </span>
                )}
              </button>

              {/* Notifications */}
              <Link href="/notifications" className="btn-icon">
                <Bell className="w-5 h-5" />
              </Link>

              {/* User Menu */}
              <div className="relative" ref={menuRef}>
                <button
                  onClick={() => setMenuOpen(!menuOpen)}
                  className="flex items-center gap-2 px-3 py-1.5 rounded-xl hover:bg-[var(--bg-subtle)] transition-colors"
                >
                  <div className="w-7 h-7 bg-nexus-100 rounded-full flex items-center justify-center">
                    <User className="w-3.5 h-3.5 text-nexus-600" />
                  </div>
                  <span className="text-sm font-medium text-[var(--text-primary)] hidden md:block max-w-[120px] truncate">
                    {user?.email?.split('@')[0]}
                  </span>
                </button>

                {menuOpen && (
                  <div className="absolute right-0 mt-2 w-56 bg-white rounded-xl border border-[var(--border-light)] shadow-[var(--shadow-popup)] py-1 animate-in fade-in slide-in-from-top-1 duration-150">
                    <div className="px-4 py-2.5 border-b border-[var(--border-light)]">
                      <p className="text-sm font-medium truncate">{user?.email}</p>
                      <p className="text-xs text-[var(--text-tertiary)] mt-0.5">
                        {user?.roles?.join(', ')}
                      </p>
                    </div>
                    <Link href="/dashboard" className="menu-item" onClick={() => setMenuOpen(false)}>
                      <Package className="w-4 h-4" /> Meus pedidos
                    </Link>
                    <Link href="/favorites" className="menu-item" onClick={() => setMenuOpen(false)}>
                      <Heart className="w-4 h-4" /> Favoritos
                    </Link>
                    {hasRole('SELLER') && (
                      <Link href="/seller/dashboard" className="menu-item" onClick={() => setMenuOpen(false)}>
                        <Store className="w-4 h-4" /> Painel do vendedor
                      </Link>
                    )}
                    {(hasRole('SUPER_ADMIN') || hasRole('TENANT_ADMIN')) && (
                      <Link href="/admin" className="menu-item" onClick={() => setMenuOpen(false)}>
                        <Settings className="w-4 h-4" /> Admin
                      </Link>
                    )}
                    <div className="border-t border-[var(--border-light)] mt-1 pt-1">
                      <button onClick={handleLogout} className="menu-item text-red-500 w-full">
                        <LogOut className="w-4 h-4" /> Sair
                      </button>
                    </div>
                  </div>
                )}
              </div>
            </>
          ) : (
            <div className="flex gap-2">
              <Link href="/login" className="btn-secondary btn-sm">Entrar</Link>
              <Link href="/login?tab=register" className="btn-primary btn-sm">Criar conta</Link>
            </div>
          )}
        </div>
      </div>
    </nav>
  )
}
