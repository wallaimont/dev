'use client'

import { useState } from 'react'
import Link from 'next/link'
import { useMyProducts } from '@/hooks'
import { formatCurrency } from '@/lib/utils'
import { Button } from '@/components/ui/Button'
import { Badge } from '@/components/ui/Badge'
import { Plus, Search, Package } from 'lucide-react'

const STATUS_MAP: Record<string, { label: string; variant: 'success' | 'warning' | 'danger' | 'neutral' }> = {
  ACTIVE:         { label: 'Ativo',      variant: 'success' },
  INACTIVE:       { label: 'Inativo',    variant: 'neutral' },
  PENDING_REVIEW: { label: 'Em revisao', variant: 'warning' },
  REJECTED:       { label: 'Rejeitado',  variant: 'danger' },
  DRAFT:          { label: 'Rascunho',   variant: 'neutral' },
}

export default function SellerProductsPage() {
  const [statusFilter, setStatusFilter] = useState<string>()
  const [search, setSearch] = useState('')
  const { data, isLoading } = useMyProducts(statusFilter)

  const products = data?.content ?? []
  const filtered = search
    ? products.filter((p: any) => p.name.toLowerCase().includes(search.toLowerCase()))
    : products

  return (
    <div>
      {/* Header */}
      <div className="bg-white border-b border-[var(--border-light)] px-6 py-4 flex items-center justify-between">
        <div>
          <h1 className="font-display text-xl font-[700] tracking-tight">Meus Produtos</h1>
          <p className="text-sm text-[var(--text-secondary)] mt-0.5">
            {data?.totalElements ?? 0} produtos cadastrados
          </p>
        </div>
        <Button size="sm"><Plus className="w-4 h-4" /> Novo produto</Button>
      </div>

      <div className="p-6 space-y-4">
        {/* Filters */}
        <div className="flex items-center gap-3">
          <div className="relative flex-1 max-w-sm">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-[var(--text-tertiary)]" />
            <input
              type="text"
              placeholder="Buscar produto..."
              value={search}
              onChange={e => setSearch(e.target.value)}
              className="input pl-9 w-full"
            />
          </div>
          <div className="flex gap-1.5">
            {[
              { key: undefined, label: 'Todos' },
              { key: 'ACTIVE', label: 'Ativos' },
              { key: 'INACTIVE', label: 'Inativos' },
              { key: 'PENDING_REVIEW', label: 'Em revisao' },
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
            {Array.from({ length: 5 }).map((_, i) => (
              <div key={i} className="skeleton h-14 rounded-xl" />
            ))}
          </div>
        ) : filtered.length === 0 ? (
          <div className="bg-white rounded-[14px] border border-[var(--border-light)] p-12 text-center">
            <Package className="w-12 h-12 text-[var(--text-tertiary)] mx-auto mb-3" />
            <p className="font-display font-[700] text-lg">Nenhum produto encontrado</p>
            <p className="text-sm text-[var(--text-secondary)] mt-1">Cadastre seu primeiro produto para comecar a vender.</p>
            <Button size="sm" className="mt-4"><Plus className="w-4 h-4" /> Novo produto</Button>
          </div>
        ) : (
          <div className="bg-white rounded-[14px] border border-[var(--border-light)] overflow-hidden">
            <table className="w-full text-sm">
              <thead>
                <tr className="bg-[var(--bg-subtle)]">
                  {['Produto', 'SKU', 'Preco', 'Estoque', 'Status', 'Acao'].map(h => (
                    <th key={h} className="px-4 py-3 text-left text-[11px] font-semibold uppercase tracking-wider text-[var(--text-tertiary)] border-b border-[var(--border-light)]">
                      {h}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {filtered.map((p: any) => {
                  const stock = p.variants?.reduce((s: number, v: any) => s + (v.stock ?? 0), 0) ?? 0
                  const sku = p.variants?.[0]?.sku ?? '-'
                  const st = STATUS_MAP[p.status] ?? { label: p.status, variant: 'neutral' as const }
                  return (
                    <tr key={p.id} className="hover:bg-[var(--bg-subtle)] transition-colors">
                      <td className="px-4 py-3.5 border-b border-[rgba(11,15,26,.04)]">
                        <div className="flex items-center gap-3">
                          <div className="w-10 h-10 rounded-lg bg-[var(--bg-subtle)] flex-shrink-0 overflow-hidden">
                            {p.mainImageUrl ? <img src={p.mainImageUrl} alt="" className="w-full h-full object-cover" /> : <Package className="w-5 h-5 text-[var(--text-tertiary)] m-auto" />}
                          </div>
                          <span className="font-medium truncate max-w-[200px]">{p.name}</span>
                        </div>
                      </td>
                      <td className="px-4 py-3.5 border-b border-[rgba(11,15,26,.04)] font-mono text-xs text-[var(--text-secondary)]">{sku}</td>
                      <td className="px-4 py-3.5 border-b border-[rgba(11,15,26,.04)] font-display font-[700]">{formatCurrency(p.basePrice)}</td>
                      <td className="px-4 py-3.5 border-b border-[rgba(11,15,26,.04)]">
                        <span className={stock === 0 ? 'text-red-500 font-semibold' : stock < 5 ? 'text-amber-600 font-semibold' : ''}>
                          {stock === 0 ? 'Esgotado' : `${stock} un.`}
                        </span>
                      </td>
                      <td className="px-4 py-3.5 border-b border-[rgba(11,15,26,.04)]">
                        <Badge variant={st.variant}>{st.label}</Badge>
                      </td>
                      <td className="px-4 py-3.5 border-b border-[rgba(11,15,26,.04)]">
                        <Button variant="ghost" size="sm">Editar</Button>
                      </td>
                    </tr>
                  )
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  )
}
