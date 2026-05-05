'use client'

import { useState } from 'react'
import { useSellerOrders, useMyProducts } from '@/hooks'
import { formatCurrency, formatDate } from '@/lib/utils'
import { OrderStatusBadge, Badge } from '@/components/ui/Badge'
import { Button } from '@/components/ui/Button'

// ============================================================
// SELLER DASHBOARD PAGE
// ============================================================
export default function SellerDashboardPage() {
  const [orderFilter, setOrderFilter] = useState<string>()
  const { data: orders }   = useSellerOrders({ status: orderFilter })
  const { data: products } = useMyProducts()

  const kpis = {
    gmv:       45_280.90,
    orders:    312,
    avgTicket: 145.13,
    rating:    4.8,
  }

  return (
    <div>
      <div>
        {/* Header */}
        <div className="bg-white border-b border-[var(--border-light)] px-6 py-4 flex items-center justify-between">
          <div>
            <h1 className="font-display text-xl font-700 tracking-tight">Painel do Vendedor</h1>
            <p className="text-sm text-[var(--text-secondary)] mt-0.5">
              Bem-vindo de volta, Loja TechStore Pro
            </p>
          </div>
          <div className="flex gap-2">
            <Button variant="secondary" size="sm">Adicionar produto</Button>
            <Button size="sm">Ver minha loja →</Button>
          </div>
        </div>

        <div className="p-6 space-y-6">
          {/* KPI Cards */}
          <div className="grid grid-cols-4 gap-4">
            <KpiCard
              label="GMV do mês"
              value={formatCurrency(kpis.gmv)}
              delta="+18.4%"
              positive
              icon="💰"
              bgColor="bg-nexus-50"
            />
            <KpiCard
              label="Pedidos"
              value={String(kpis.orders)}
              delta="+24 hoje"
              positive
              icon="📦"
              bgColor="bg-green-50"
            />
            <KpiCard
              label="Ticket médio"
              value={formatCurrency(kpis.avgTicket)}
              delta="+3.2%"
              positive
              icon="🎫"
              bgColor="bg-amber-50"
            />
            <KpiCard
              label="Avaliação"
              value={String(kpis.rating) + ' ★'}
              delta="428 avaliações"
              positive
              icon="⭐"
              bgColor="bg-accent-50"
            />
          </div>

          {/* Charts Row */}
          <div className="grid grid-cols-3 gap-4">
            <div className="col-span-2 bg-white rounded-[14px] border border-[var(--border-light)] p-5">
              <div className="flex items-center justify-between mb-4">
                <div>
                  <h3 className="font-semibold text-[15px]">Vendas — últimos 30 dias</h3>
                  <p className="text-xs text-[var(--text-tertiary)] mt-0.5">Receita bruta em R$</p>
                </div>
                <div className="flex gap-1.5">
                  {['7D', '30D', '90D'].map(p => (
                    <button key={p}
                      className="px-3 py-1 rounded-lg text-xs font-medium bg-[var(--bg-subtle)] text-[var(--text-secondary)] hover:bg-nexus-50 hover:text-nexus-700 transition-colors">
                      {p}
                    </button>
                  ))}
                </div>
              </div>
              <MiniChart />
            </div>

            <div className="bg-white rounded-[14px] border border-[var(--border-light)] p-5">
              <h3 className="font-semibold text-[15px] mb-1">Top produtos</h3>
              <p className="text-xs text-[var(--text-tertiary)] mb-4">Por receita este mês</p>
              <div className="space-y-3">
                {[
                  { name: 'Smartphone X15 Pro', sales: 89, revenue: 195_511 },
                  { name: 'Fone NC-900 ANC', sales: 145, revenue: 94_105 },
                  { name: 'Smart TV 55" 4K', sales: 34, revenue: 74_766 },
                  { name: 'Notebook Gamer', sales: 12, revenue: 57_588 },
                ].map((p, i) => (
                  <div key={p.name} className="flex items-center gap-3">
                    <span className="text-xs font-700 text-[var(--text-tertiary)] w-4">
                      {i + 1}
                    </span>
                    <div className="flex-1 min-w-0">
                      <p className="text-[13px] font-medium truncate">{p.name}</p>
                      <div className="flex items-center gap-2 mt-0.5">
                        <div className="h-1.5 bg-nexus-100 rounded-full flex-1">
                          <div
                            className="h-1.5 bg-nexus-500 rounded-full"
                            style={{ width: `${(p.revenue / 200_000) * 100}%` }}
                          />
                        </div>
                        <span className="text-xs text-[var(--text-tertiary)] shrink-0">
                          {p.sales} und.
                        </span>
                      </div>
                    </div>
                    <span className="text-[13px] font-700 font-display text-nexus-700 shrink-0">
                      {formatCurrency(p.revenue)}
                    </span>
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* Orders Table */}
          <div className="bg-white rounded-[14px] border border-[var(--border-light)] overflow-hidden">
            <div className="px-5 py-4 border-b border-[var(--border-light)] flex items-center justify-between">
              <h3 className="font-semibold text-[15px]">Pedidos recentes</h3>
              <div className="flex gap-2">
                {['Todos', 'Aguardando', 'Enviados', 'Entregues'].map(f => (
                  <button key={f}
                    onClick={() => setOrderFilter(f === 'Todos' ? undefined : f.toUpperCase())}
                    className={`px-3 py-1.5 rounded-lg text-xs font-medium transition-colors ${
                      (f === 'Todos' && !orderFilter) || f.toUpperCase() === orderFilter
                        ? 'bg-nexus-500 text-white'
                        : 'bg-[var(--bg-subtle)] text-[var(--text-secondary)] hover:bg-nexus-50'
                    }`}>
                    {f}
                  </button>
                ))}
              </div>
            </div>
            <SellerOrdersTable />
          </div>

          {/* Products Status */}
          <div className="bg-white rounded-[14px] border border-[var(--border-light)] overflow-hidden">
            <div className="px-5 py-4 border-b border-[var(--border-light)] flex items-center justify-between">
              <h3 className="font-semibold text-[15px]">Meus produtos</h3>
              <Button variant="secondary" size="sm">Gerenciar todos →</Button>
            </div>
            <SellerProductsTable />
          </div>
        </div>
      </div>
    </div>
  )
}

// Remove SellerSidebar - now provided by layout

// ---- Sub-components ----

function KpiCard({ label, value, delta, positive, icon, bgColor }: {
  label: string; value: string; delta: string; positive: boolean; icon: string; bgColor: string
}) {
  return (
    <div className="bg-white rounded-[14px] border border-[var(--border-light)] p-4">
      <div className="flex items-center justify-between mb-3">
        <span className="text-xs font-medium text-[var(--text-secondary)]">{label}</span>
        <div className={`w-8 h-8 rounded-[8px] ${bgColor} flex items-center justify-center text-sm`}>
          {icon}
        </div>
      </div>
      <p className="font-display text-2xl font-700 tracking-tight text-[var(--text-primary)]">
        {value}
      </p>
      <p className={`text-xs mt-1.5 font-medium ${positive ? 'text-success-500' : 'text-accent-500'}`}>
        {positive ? '▲' : '▼'} {delta}
      </p>
    </div>
  )
}

function MiniChart() {
  const data = [42, 58, 35, 72, 65, 89, 74, 95, 88, 102, 94, 115,
                108, 125, 118, 134, 127, 145, 138, 152, 148, 162, 158, 172,
                165, 178, 171, 188, 183, 195]

  const max = Math.max(...data)
  const w = 100 / data.length

  return (
    <div className="h-[120px] flex items-end gap-[2px]">
      {data.map((v, i) => (
        <div
          key={i}
          className="flex-1 rounded-t-[3px] transition-all duration-200 hover:opacity-80 cursor-pointer"
          style={{
            height: `${(v / max) * 100}%`,
            background: i === data.length - 1
              ? '#FF4B26'
              : v > max * 0.75 ? '#1A2FE8' : '#c4cdf9',
          }}
          title={`Dia ${i+1}: ${formatCurrency(v * 100)}`}
        />
      ))}
    </div>
  )
}

function SellerOrdersTable() {
  const { data: ordersData, isLoading } = useSellerOrders()

  const fallbackOrders = [
    { num: '#NX1729388', buyer: 'Maria R.', items: 2, total: 2199.00, status: 'PENDING',   date: '15 Jun' },
    { num: '#NX1729387', buyer: 'João P.',  items: 1, total: 649.00,  status: 'SHIPPED',   date: '14 Jun' },
    { num: '#NX1729385', buyer: 'Ana F.',   items: 3, total: 876.50,  status: 'DELIVERED', date: '13 Jun' },
    { num: '#NX1729384', buyer: 'Carlos S.',items: 1, total: 3200.00, status: 'DELIVERED', date: '12 Jun' },
    { num: '#NX1729382', buyer: 'Lucia M.', items: 1, total: 145.00,  status: 'CANCELLED', date: '12 Jun' },
  ]

  const orders = ordersData?.content?.length
    ? ordersData.content.map((o: any) => ({
        num: `#${o.orderNumber}`,
        buyer: o.buyerName ?? '—',
        items: o.itemCount ?? 1,
        total: o.total,
        status: o.status,
        date: formatDate(o.createdAt),
      }))
    : fallbackOrders

  return (
    <table className="w-full text-sm">
      <thead>
        <tr className="bg-[var(--bg-subtle)]">
          {['Pedido', 'Comprador', 'Itens', 'Total', 'Status', 'Data', 'Ação'].map(h => (
            <th key={h} className="px-4 py-3 text-left text-[11px] font-600 uppercase tracking-wider text-[var(--text-tertiary)] border-b border-[var(--border-light)]">
              {h}
            </th>
          ))}
        </tr>
      </thead>
      <tbody>
        {orders.map(o => (
          <tr key={o.num} className="hover:bg-[var(--bg-subtle)] transition-colors">
            <td className="px-4 py-3.5 border-b border-[rgba(11,15,26,.04)] font-mono text-[12px] text-nexus-600 font-500">
              {o.num}
            </td>
            <td className="px-4 py-3.5 border-b border-[rgba(11,15,26,.04)]">{o.buyer}</td>
            <td className="px-4 py-3.5 border-b border-[rgba(11,15,26,.04)] text-[var(--text-secondary)]">
              {o.items} {o.items === 1 ? 'item' : 'itens'}
            </td>
            <td className="px-4 py-3.5 border-b border-[rgba(11,15,26,.04)] font-display font-700">
              {formatCurrency(o.total)}
            </td>
            <td className="px-4 py-3.5 border-b border-[rgba(11,15,26,.04)]">
              <OrderStatusBadge status={o.status} />
            </td>
            <td className="px-4 py-3.5 border-b border-[rgba(11,15,26,.04)] text-[var(--text-tertiary)] text-xs">
              {o.date}
            </td>
            <td className="px-4 py-3.5 border-b border-[rgba(11,15,26,.04)]">
              {o.status === 'PENDING' && (
                <Button variant="outline" size="sm">Enviar</Button>
              )}
              {o.status !== 'PENDING' && (
                <Button variant="ghost" size="sm">Ver →</Button>
              )}
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  )
}

function SellerProductsTable() {
  const { data: productsData, isLoading } = useMyProducts()

  const fallbackProducts = [
    { name: 'Smartphone X15 Pro Max 256GB', sku: 'SMTX15-BK-256', price: 2199.00, stock: 45, status: 'ACTIVE' },
    { name: 'Fone Bluetooth NC-900 ANC',    sku: 'FNB-NC900-WH',  price: 649.00,  stock: 12, status: 'ACTIVE' },
    { name: 'Smart TV 55" 4K HDR',          sku: 'TV55-4K-BK',    price: 2197.00, stock: 8,  status: 'ACTIVE' },
    { name: 'Notebook Gamer RTX 4060',      sku: 'NB-RTX4060-GR', price: 4799.00, stock: 0,  status: 'INACTIVE' },
    { name: 'Câmera Mirrorless 24MP',       sku: 'CAM-MLS-24',    price: 3450.00, stock: 3,  status: 'PENDING_REVIEW' },
  ]

  const products = productsData?.content?.length
    ? productsData.content.map((p: any) => ({
        name: p.name,
        sku: p.variants?.[0]?.sku ?? '—',
        price: p.basePrice ?? p.promotionalPrice ?? 0,
        stock: p.variants?.reduce((sum: number, v: any) => sum + (v.stock ?? 0), 0) ?? 0,
        status: p.status ?? 'ACTIVE',
      }))
    : fallbackProducts

  return (
    <table className="w-full text-sm">
      <thead>
        <tr className="bg-[var(--bg-subtle)]">
          {['Produto', 'SKU', 'Preço', 'Estoque', 'Status', 'Ação'].map(h => (
            <th key={h} className="px-4 py-3 text-left text-[11px] font-600 uppercase tracking-wider text-[var(--text-tertiary)] border-b border-[var(--border-light)]">
              {h}
            </th>
          ))}
        </tr>
      </thead>
      <tbody>
        {products.map(p => (
          <tr key={p.sku} className="hover:bg-[var(--bg-subtle)] transition-colors">
            <td className="px-4 py-3.5 border-b border-[rgba(11,15,26,.04)] font-medium max-w-[220px] truncate">
              {p.name}
            </td>
            <td className="px-4 py-3.5 border-b border-[rgba(11,15,26,.04)] font-mono text-[12px] text-[var(--text-secondary)]">
              {p.sku}
            </td>
            <td className="px-4 py-3.5 border-b border-[rgba(11,15,26,.04)] font-display font-700">
              {formatCurrency(p.price)}
            </td>
            <td className="px-4 py-3.5 border-b border-[rgba(11,15,26,.04)]">
              <span className={`font-600 ${p.stock === 0 ? 'text-red-500' : p.stock < 5 ? 'text-amber-600' : 'text-[var(--text-primary)]'}`}>
                {p.stock === 0 ? 'Esgotado' : `${p.stock} un.`}
              </span>
            </td>
            <td className="px-4 py-3.5 border-b border-[rgba(11,15,26,.04)]">
              <ProductStatusBadge status={p.status} />
            </td>
            <td className="px-4 py-3.5 border-b border-[rgba(11,15,26,.04)]">
              <Button variant="ghost" size="sm">Editar →</Button>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  )
}

function ProductStatusBadge({ status }: { status: string }) {
  const map: Record<string, { label: string; variant: 'success' | 'warning' | 'danger' | 'neutral' | 'primary' }> = {
    ACTIVE:         { label: 'Ativo',         variant: 'success' },
    INACTIVE:       { label: 'Inativo',       variant: 'neutral' },
    PENDING_REVIEW: { label: 'Em revisão',    variant: 'warning' },
    REJECTED:       { label: 'Rejeitado',     variant: 'danger' },
    DRAFT:          { label: 'Rascunho',      variant: 'neutral' },
  }
  const { label, variant } = map[status] ?? { label: status, variant: 'neutral' as const }
  return <Badge variant={variant}>{label}</Badge>
}

// Sidebar is now provided by app/seller/layout.tsx
