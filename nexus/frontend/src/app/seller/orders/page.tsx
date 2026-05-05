'use client'

import { useState } from 'react'
import { useSellerOrders } from '@/hooks'
import { formatCurrency, formatDate } from '@/lib/utils'
import { Button } from '@/components/ui/Button'
import { OrderStatusBadge } from '@/components/ui/Badge'
import { Search, Package } from 'lucide-react'

export default function SellerOrdersPage() {
  const [statusFilter, setStatusFilter] = useState<string>()
  const { data, isLoading } = useSellerOrders({ status: statusFilter })
  const orders = data?.content ?? []

  return (
    <div>
      {/* Header */}
      <div className="bg-white border-b border-[var(--border-light)] px-6 py-4">
        <h1 className="font-display text-xl font-[700] tracking-tight">Pedidos</h1>
        <p className="text-sm text-[var(--text-secondary)] mt-0.5">
          {data?.totalElements ?? 0} pedidos no total
        </p>
      </div>

      <div className="p-6 space-y-4">
        {/* Filters */}
        <div className="flex items-center gap-3">
          <div className="relative flex-1 max-w-sm">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-[var(--text-tertiary)]" />
            <input type="text" placeholder="Buscar pedido..." className="input pl-9 w-full" />
          </div>
          <div className="flex gap-1.5">
            {[
              { key: undefined, label: 'Todos' },
              { key: 'PENDING', label: 'Pendentes' },
              { key: 'SHIPPED', label: 'Enviados' },
              { key: 'DELIVERED', label: 'Entregues' },
              { key: 'CANCELLED', label: 'Cancelados' },
            ].map(f => (
              <button key={f.label}
                onClick={() => setStatusFilter(f.key)}
                className={`px-3 py-1.5 rounded-lg text-xs font-medium transition-colors ${
                  statusFilter === f.key
                    ? 'bg-nexus-500 text-white'
                    : 'bg-[var(--bg-subtle)] text-[var(--text-secondary)] hover:bg-nexus-50'
                }`}>
                {f.label}
              </button>
            ))}
          </div>
        </div>

        {/* Table */}
        {isLoading ? (
          <div className="space-y-2">
            {Array.from({ length: 5 }).map((_, i) => <div key={i} className="skeleton h-14 rounded-xl" />)}
          </div>
        ) : orders.length === 0 ? (
          <div className="bg-white rounded-[14px] border border-[var(--border-light)] p-12 text-center">
            <Package className="w-12 h-12 text-[var(--text-tertiary)] mx-auto mb-3" />
            <p className="font-display font-[700] text-lg">Nenhum pedido encontrado</p>
          </div>
        ) : (
          <div className="bg-white rounded-[14px] border border-[var(--border-light)] overflow-hidden">
            <table className="w-full text-sm">
              <thead>
                <tr className="bg-[var(--bg-subtle)]">
                  {['Pedido', 'Comprador', 'Itens', 'Total', 'Status', 'Data', 'Acao'].map(h => (
                    <th key={h} className="px-4 py-3 text-left text-[11px] font-semibold uppercase tracking-wider text-[var(--text-tertiary)] border-b border-[var(--border-light)]">{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {orders.map((o: any) => (
                  <tr key={o.id} className="hover:bg-[var(--bg-subtle)] transition-colors">
                    <td className="px-4 py-3.5 border-b border-[rgba(11,15,26,.04)] font-mono text-xs text-nexus-600 font-medium">#{o.orderNumber}</td>
                    <td className="px-4 py-3.5 border-b border-[rgba(11,15,26,.04)]">{o.buyerName ?? '-'}</td>
                    <td className="px-4 py-3.5 border-b border-[rgba(11,15,26,.04)] text-[var(--text-secondary)]">{o.itemCount} {o.itemCount === 1 ? 'item' : 'itens'}</td>
                    <td className="px-4 py-3.5 border-b border-[rgba(11,15,26,.04)] font-display font-[700]">{formatCurrency(o.total)}</td>
                    <td className="px-4 py-3.5 border-b border-[rgba(11,15,26,.04)]"><OrderStatusBadge status={o.status} /></td>
                    <td className="px-4 py-3.5 border-b border-[rgba(11,15,26,.04)] text-[var(--text-tertiary)] text-xs">{formatDate(o.createdAt)}</td>
                    <td className="px-4 py-3.5 border-b border-[rgba(11,15,26,.04)]">
                      {o.status === 'PENDING' ? <Button variant="outline" size="sm">Enviar</Button> : <Button variant="ghost" size="sm">Ver</Button>}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  )
}
