'use client'

import Link from 'next/link'
import { usePathname } from 'next/navigation'
import { cn } from '@/lib/utils'
import {
  LayoutDashboard, Package, ShoppingBag, Star, DollarSign, Settings, Store, BarChart3
} from 'lucide-react'

const sellerLinks = [
  { href: '/seller/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
  { href: '/seller/products',  icon: Package,         label: 'Produtos' },
  { href: '/seller/orders',    icon: ShoppingBag,     label: 'Pedidos' },
  { href: '/seller/reviews',   icon: Star,            label: 'Avaliacoes' },
  { href: '/seller/payouts',   icon: DollarSign,      label: 'Pagamentos' },
  { href: '/seller/store',     icon: Store,           label: 'Minha loja' },
  { href: '/seller/settings',  icon: Settings,        label: 'Configuracoes' },
]

const adminLinks = [
  { href: '/admin',           icon: LayoutDashboard, label: 'Dashboard' },
  { href: '/admin/sellers',   icon: Store,           label: 'Vendedores' },
  { href: '/admin/products',  icon: Package,         label: 'Produtos' },
  { href: '/admin/orders',    icon: ShoppingBag,     label: 'Pedidos' },
  { href: '/admin/analytics', icon: BarChart3,       label: 'Analytics' },
  { href: '/admin/settings',  icon: Settings,        label: 'Configuracoes' },
]

interface SidebarProps {
  variant: 'seller' | 'admin'
}

export function Sidebar({ variant }: SidebarProps) {
  const pathname = usePathname()
  const links = variant === 'admin' ? adminLinks : sellerLinks

  return (
    <aside className="fixed left-0 top-16 bottom-0 w-[220px] bg-white border-r border-[var(--border-light)] overflow-y-auto z-40">
      <div className="p-3 space-y-0.5">
        <p className="px-3 py-2 text-[10px] font-semibold uppercase tracking-wider text-[var(--text-tertiary)]">
          {variant === 'admin' ? 'Administracao' : 'Vendedor'}
        </p>
        {links.map(link => {
          const active = pathname === link.href || (link.href !== '/admin' && link.href !== '/seller/dashboard' && pathname.startsWith(link.href))
          const isExact = link.href === '/admin' || link.href === '/seller/dashboard'
          const isActive = isExact ? pathname === link.href : active

          return (
            <Link
              key={link.href}
              href={link.href}
              className={cn(
                'flex items-center gap-2.5 px-3 py-2 rounded-lg text-sm transition-colors',
                isActive
                  ? 'bg-nexus-50 text-nexus-700 font-medium'
                  : 'text-[var(--text-secondary)] hover:bg-[var(--bg-subtle)] hover:text-[var(--text-primary)]'
              )}
            >
              <link.icon className="w-4 h-4" />
              {link.label}
            </Link>
          )
        })}
      </div>
    </aside>
  )
}
