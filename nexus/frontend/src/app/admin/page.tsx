'use client'

import { useEffect, useState } from 'react'
import { adminApi } from '@/lib/api/client'
import { formatCurrency } from '@/lib/utils'
import { Button } from '@/components/ui/Button'
import { Users, ShoppingCart, DollarSign, TrendingUp, Package, Store } from 'lucide-react'

export default function AdminDashboardPage() {
  const [dash, setDash] = useState<any>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    adminApi.dashboard('30d')
      .then(r => setDash(r.data))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  const kpis = [
    { label: 'GMV Total', value: formatCurrency(dash?.gmv ?? 0), icon: DollarSign, color: 'bg-nexus-50 text-nexus-600', delta: dash?.gmvDelta },
    { label: 'Pedidos', value: String(dash?.totalOrders ?? 0), icon: ShoppingCart, color: 'bg-green-50 text-green-600', delta: dash?.ordersDelta },
    { label: 'Vendedores', value: String(dash?.totalSellers ?? 0), icon: Store, color: 'bg-amber-50 text-amber-600' },
    { label: 'Usuarios', value: String(dash?.totalUsers ?? 0), icon: Users, color: 'bg-purple-50 text-purple-600' },
    { label: 'Produtos', value: String(dash?.totalProducts ?? 0), icon: Package, color: 'bg-blue-50 text-blue-600' },
    { label: 'Ticket Medio', value: formatCurrency(dash?.avgTicket ?? 0), icon: TrendingUp, color: 'bg-accent-50 text-accent-600' },
  ]

  return (
    <div>
      {/* Header */}
      <div className="bg-white border-b border-[var(--border-light)] px-6 py-4 flex items-center justify-between">
        <div>
          <h1 className="font-display text-xl font-[700] tracking-tight">Painel Administrativo</h1>
          <p className="text-sm text-[var(--text-secondary)] mt-0.5">Visao geral da plataforma — ultimos 30 dias</p>
        </div>
        <div className="flex gap-2">
          {['7D', '30D', '90D'].map(p => (
            <button key={p} className="px-3 py-1.5 rounded-lg text-xs font-medium bg-[var(--bg-subtle)] text-[var(--text-secondary)] hover:bg-nexus-50 hover:text-nexus-700 transition-colors">
              {p}
            </button>
          ))}
        </div>
      </div>

      <div className="p-6 space-y-6">
        {/* KPI Grid */}
        {loading ? (
          <div className="grid grid-cols-3 gap-4">
            {Array.from({ length: 6 }).map((_, i) => <div key={i} className="skeleton h-28 rounded-[14px]" />)}
          </div>
        ) : (
          <div className="grid grid-cols-3 gap-4">
            {kpis.map(k => (
              <div key={k.label} className="bg-white rounded-[14px] border border-[var(--border-light)] p-5">
                <div className="flex items-center justify-between mb-3">
                  <span className="text-xs font-medium text-[var(--text-secondary)]">{k.label}</span>
                  <div className={`w-9 h-9 rounded-[10px] ${k.color} flex items-center justify-center`}>
                    <k.icon className="w-4 h-4" />
                  </div>
                </div>
                <p className="font-display text-2xl font-[700] tracking-tight">{k.value}</p>
                {k.delta && (
                  <p className={`text-xs mt-1 font-medium ${Number(k.delta) >= 0 ? 'text-green-600' : 'text-red-500'}`}>
                    {Number(k.delta) >= 0 ? '▲' : '▼'} {k.delta}%
                  </p>
                )}
              </div>
            ))}
          </div>
        )}

        {/* Quick actions */}
        <div className="grid grid-cols-2 gap-4">
          <div className="bg-white rounded-[14px] border border-[var(--border-light)] p-5">
            <h3 className="font-semibold text-[15px] mb-1">Vendedores pendentes</h3>
            <p className="text-xs text-[var(--text-tertiary)] mb-4">Solicitacoes aguardando aprovacao</p>
            <div className="text-center py-6 text-[var(--text-tertiary)] text-sm">
              {dash?.pendingSellers > 0
                ? <p className="text-2xl font-display font-[700] text-amber-600">{dash.pendingSellers}</p>
                : <p>Nenhum vendedor pendente</p>
              }
            </div>
            <Button variant="secondary" size="sm" className="w-full">Ver vendedores</Button>
          </div>

          <div className="bg-white rounded-[14px] border border-[var(--border-light)] p-5">
            <h3 className="font-semibold text-[15px] mb-1">Produtos em revisao</h3>
            <p className="text-xs text-[var(--text-tertiary)] mb-4">Produtos aguardando moderacao</p>
            <div className="text-center py-6 text-[var(--text-tertiary)] text-sm">
              {dash?.pendingProducts > 0
                ? <p className="text-2xl font-display font-[700] text-amber-600">{dash.pendingProducts}</p>
                : <p>Nenhum produto pendente</p>
              }
            </div>
            <Button variant="secondary" size="sm" className="w-full">Ver produtos</Button>
          </div>
        </div>
      </div>
    </div>
  )
}
