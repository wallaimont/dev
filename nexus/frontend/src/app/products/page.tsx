'use client'

import { useState } from 'react'
import Link from 'next/link'
import { useSearchParams } from 'next/navigation'
import { useProductSearch } from '@/hooks'
import { Navbar } from '@/components/layout/Navbar'
import { formatCurrency } from '@/lib/utils'
import { Star, ShoppingCart, SlidersHorizontal, ChevronLeft, ChevronRight } from 'lucide-react'

export default function ProductsPage() {
  const searchParams = useSearchParams()
  const q = searchParams.get('q') ?? undefined
  const [page, setPage] = useState(0)
  const [sortBy, setSortBy] = useState('newest')

  const { data, isLoading } = useProductSearch({ q, page, size: 16, sortBy })

  return (
    <div className="min-h-screen bg-[var(--bg-page)]">
      <Navbar />
      <div className="max-w-7xl mx-auto px-4 py-6">
        {/* Header */}
        <div className="flex items-center justify-between mb-6">
          <div>
            <h1 className="section-title">{q ? `Resultados para "${q}"` : 'Todos os produtos'}</h1>
            {data && <p className="text-sm text-[var(--text-secondary)] mt-1">{data.totalElements} produto(s) encontrado(s)</p>}
          </div>
          <div className="flex items-center gap-3">
            <SlidersHorizontal className="w-4 h-4 text-[var(--text-tertiary)]" />
            <select
              value={sortBy}
              onChange={e => { setSortBy(e.target.value); setPage(0) }}
              className="input !w-auto !py-1.5 text-sm"
            >
              <option value="newest">Mais recentes</option>
              <option value="price_asc">Menor preco</option>
              <option value="price_desc">Maior preco</option>
              <option value="rating">Melhor avaliados</option>
              <option value="bestseller">Mais vendidos</option>
            </select>
          </div>
        </div>

        {/* Grid */}
        {isLoading ? (
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
            {Array.from({ length: 8 }).map((_, i) => (
              <div key={i} className="card p-4 space-y-3">
                <div className="skeleton aspect-square rounded-lg" />
                <div className="skeleton h-4 w-3/4" />
                <div className="skeleton h-5 w-1/3" />
              </div>
            ))}
          </div>
        ) : data?.content && data.content.length > 0 ? (
          <>
            <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
              {data.content.map(product => (
                <Link key={product.id} href={`/products/${product.slug}`} className="card-interactive overflow-hidden group">
                  <div className="aspect-square bg-[var(--bg-subtle)] relative overflow-hidden">
                    {product.mainImageUrl ? (
                      <img src={product.mainImageUrl} alt={product.name} className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300" />
                    ) : (
                      <div className="w-full h-full flex items-center justify-center">
                        <ShoppingCart className="w-10 h-10 text-[var(--text-tertiary)]" />
                      </div>
                    )}
                    {product.promotionalPrice && (
                      <span className="absolute top-2 left-2 badge-accent text-[10px]">
                        -{Math.round((1 - product.promotionalPrice / product.basePrice) * 100)}%
                      </span>
                    )}
                  </div>
                  <div className="p-3">
                    <p className="text-xs text-[var(--text-tertiary)] truncate">{product.storeName}</p>
                    <h3 className="text-sm font-medium mt-0.5 line-clamp-2 leading-snug">{product.name}</h3>
                    <div className="flex items-center gap-1 mt-1.5">
                      <Star className="w-3 h-3 fill-amber-400 text-amber-400" />
                      <span className="text-xs text-[var(--text-secondary)]">{product.avgRating?.toFixed(1)} ({product.totalReviews})</span>
                    </div>
                    <div className="mt-2">
                      {product.promotionalPrice ? (
                        <>
                          <span className="price-old">{formatCurrency(product.basePrice)}</span>
                          <span className="price text-base price-promo block">{formatCurrency(product.promotionalPrice)}</span>
                        </>
                      ) : (
                        <span className="price text-base">{formatCurrency(product.basePrice)}</span>
                      )}
                    </div>
                  </div>
                </Link>
              ))}
            </div>

            {/* Pagination */}
            {data.totalPages > 1 && (
              <div className="flex items-center justify-center gap-2 mt-8">
                <button
                  onClick={() => setPage(p => Math.max(0, p - 1))}
                  disabled={page === 0}
                  className="btn-icon disabled:opacity-30"
                >
                  <ChevronLeft className="w-4 h-4" />
                </button>
                <span className="text-sm text-[var(--text-secondary)]">
                  Pagina {page + 1} de {data.totalPages}
                </span>
                <button
                  onClick={() => setPage(p => Math.min(data.totalPages - 1, p + 1))}
                  disabled={page >= data.totalPages - 1}
                  className="btn-icon disabled:opacity-30"
                >
                  <ChevronRight className="w-4 h-4" />
                </button>
              </div>
            )}
          </>
        ) : (
          <div className="text-center py-20">
            <ShoppingCart className="w-16 h-16 text-[var(--text-tertiary)] mx-auto mb-4" />
            <h3 className="font-display text-xl font-[700]">Nenhum produto encontrado</h3>
            <p className="text-[var(--text-secondary)] mt-2">Tente outra busca ou explore as categorias.</p>
          </div>
        )}
      </div>
    </div>
  )
}
