'use client'

import Link from 'next/link'
import { useProductSearch } from '@/hooks'
import { useAuthStore } from '@/store'
import { Navbar } from '@/components/layout/Navbar'
import { formatCurrency } from '@/lib/utils'
import { Star, ArrowRight, ShoppingCart, Shield, Truck, CreditCard } from 'lucide-react'

export default function HomePage() {
  const isAuthenticated = useAuthStore(s => s.isAuthenticated)
  const { data: products, isLoading } = useProductSearch({ size: 12, sortBy: 'newest' })

  return (
    <div className="min-h-screen bg-[var(--bg-page)]">
      <Navbar />

      {/* Hero */}
      <section className="bg-nexus-900 text-white">
        <div className="max-w-7xl mx-auto px-4 py-16 md:py-24">
          <div className="max-w-2xl">
            <h1 className="font-display text-4xl md:text-5xl font-[800] tracking-tight leading-tight">
              O marketplace que conecta voce ao{' '}
              <span className="text-yellow-200">melhor preco.</span>
            </h1>
            <p className="mt-4 text-white/60 text-lg leading-relaxed">
              Compre de milhares de vendedores verificados. Pagamento seguro,
              entrega rastreada, garantia em cada produto.
            </p>
            <div className="mt-8 flex gap-3">
              <Link href="/products" className="btn-primary btn-lg">
                Explorar produtos <ArrowRight className="w-4 h-4" />
              </Link>
              {!isAuthenticated && (
                <Link href="/login" className="btn-lg bg-white/10 text-white hover:bg-white/20 inline-flex items-center gap-2 rounded-[12px] px-7 py-3.5 text-base font-medium transition-all">
                  Criar conta gratis
                </Link>
              )}
            </div>
          </div>
        </div>
      </section>

      {/* Trust bar */}
      <section className="bg-white border-b border-[var(--border-light)]">
        <div className="max-w-7xl mx-auto px-4 py-4 flex flex-wrap justify-center gap-8 text-sm text-[var(--text-secondary)]">
          <div className="flex items-center gap-2"><Shield className="w-4 h-4 text-nexus-500" /> Compra segura</div>
          <div className="flex items-center gap-2"><Truck className="w-4 h-4 text-nexus-500" /> Frete rastreado</div>
          <div className="flex items-center gap-2"><CreditCard className="w-4 h-4 text-nexus-500" /> PIX, cartao e boleto</div>
          <div className="flex items-center gap-2"><Star className="w-4 h-4 text-nexus-500" /> Avaliacoes verificadas</div>
        </div>
      </section>

      {/* Products */}
      <section className="max-w-7xl mx-auto px-4 py-10">
        <div className="flex items-center justify-between mb-6">
          <h2 className="section-title">Produtos em destaque</h2>
          <Link href="/products" className="text-sm text-nexus-500 font-medium hover:underline flex items-center gap-1">
            Ver todos <ArrowRight className="w-3.5 h-3.5" />
          </Link>
        </div>

        {isLoading ? (
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
            {Array.from({ length: 8 }).map((_, i) => (
              <div key={i} className="card p-4 space-y-3">
                <div className="skeleton aspect-square rounded-lg" />
                <div className="skeleton h-4 w-3/4" />
                <div className="skeleton h-3 w-1/2" />
                <div className="skeleton h-5 w-1/3" />
              </div>
            ))}
          </div>
        ) : products?.content && products.content.length > 0 ? (
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
            {products.content.map(product => (
              <ProductCard key={product.id} product={product} />
            ))}
          </div>
        ) : (
          <EmptyProducts />
        )}
      </section>

      {/* Footer */}
      <footer className="bg-nexus-900 text-white/50 mt-20">
        <div className="max-w-7xl mx-auto px-4 py-12 grid grid-cols-2 md:grid-cols-4 gap-8">
          <div>
            <span className="font-display text-lg font-[800] text-white">NEX<span className="text-nexus-400">US</span></span>
            <p className="mt-2 text-sm">O marketplace que conecta voce ao melhor preco.</p>
          </div>
          <div>
            <p className="text-white text-sm font-medium mb-3">Comprador</p>
            <ul className="space-y-2 text-sm">
              <li><Link href="/products" className="hover:text-white">Produtos</Link></li>
              <li><Link href="/dashboard" className="hover:text-white">Meus pedidos</Link></li>
            </ul>
          </div>
          <div>
            <p className="text-white text-sm font-medium mb-3">Vendedor</p>
            <ul className="space-y-2 text-sm">
              <li><Link href="/seller/dashboard" className="hover:text-white">Painel</Link></li>
              <li><Link href="/login" className="hover:text-white">Venda no Nexus</Link></li>
            </ul>
          </div>
          <div>
            <p className="text-white text-sm font-medium mb-3">Legal</p>
            <ul className="space-y-2 text-sm">
              <li><Link href="/terms" className="hover:text-white">Termos de uso</Link></li>
              <li><Link href="/privacy" className="hover:text-white">Privacidade</Link></li>
            </ul>
          </div>
        </div>
        <div className="border-t border-white/10 py-4 text-center text-xs">
          &copy; {new Date().getFullYear()} Nexus Marketplace. Todos os direitos reservados.
        </div>
      </footer>
    </div>
  )
}

function ProductCard({ product }: { product: any }) {
  return (
    <Link href={`/products/${product.slug}`} className="card-interactive overflow-hidden group">
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
        <h3 className="text-sm font-medium text-[var(--text-primary)] mt-0.5 line-clamp-2 leading-snug">
          {product.name}
        </h3>
        <div className="flex items-center gap-1 mt-1.5">
          <Star className="w-3 h-3 fill-amber-400 text-amber-400" />
          <span className="text-xs text-[var(--text-secondary)]">
            {product.avgRating?.toFixed(1) ?? '-'} ({product.totalReviews ?? 0})
          </span>
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
  )
}

function EmptyProducts() {
  return (
    <div className="text-center py-20">
      <ShoppingCart className="w-16 h-16 text-[var(--text-tertiary)] mx-auto mb-4" />
      <h3 className="font-display text-xl font-[700]">Nenhum produto ainda</h3>
      <p className="text-[var(--text-secondary)] mt-2 max-w-md mx-auto">
        O marketplace esta sendo preparado. Em breve havera produtos incriveis aqui.
      </p>
    </div>
  )
}