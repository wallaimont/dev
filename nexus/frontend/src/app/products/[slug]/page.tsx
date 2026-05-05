'use client'

import { useParams } from 'next/navigation'
import Link from 'next/link'
import { useProduct, useRelatedProducts, useAddToCart } from '@/hooks'
import { useAuthStore } from '@/store'
import { Navbar } from '@/components/layout/Navbar'
import { formatCurrency } from '@/lib/utils'
import { Button } from '@/components/ui/Button'
import { Badge } from '@/components/ui/Badge'
import { Star, ShoppingCart, Truck, Shield, ChevronRight, Package } from 'lucide-react'
import { useState } from 'react'

export default function ProductDetailPage() {
  const { slug } = useParams<{ slug: string }>()
  const { data: product, isLoading } = useProduct(slug)
  const { data: related } = useRelatedProducts(product?.id ?? '')
  const addToCart = useAddToCart()
  const isAuthenticated = useAuthStore(s => s.isAuthenticated)
  const [selectedVariant, setSelectedVariant] = useState(0)
  const [selectedImage, setSelectedImage] = useState(0)

  if (isLoading) {
    return (
      <div className="min-h-screen bg-[var(--bg-page)]">
        <Navbar />
        <div className="max-w-7xl mx-auto px-4 py-8">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            <div className="skeleton aspect-square rounded-2xl" />
            <div className="space-y-4">
              <div className="skeleton h-8 w-3/4" />
              <div className="skeleton h-4 w-1/2" />
              <div className="skeleton h-10 w-1/3" />
              <div className="skeleton h-32 w-full" />
            </div>
          </div>
        </div>
      </div>
    )
  }

  if (!product) {
    return (
      <div className="min-h-screen bg-[var(--bg-page)]">
        <Navbar />
        <div className="max-w-7xl mx-auto px-4 py-20 text-center">
          <Package className="w-16 h-16 text-[var(--text-tertiary)] mx-auto mb-4" />
          <h2 className="font-display text-2xl font-[700]">Produto nao encontrado</h2>
          <Link href="/products" className="btn-primary mt-6 inline-flex">Voltar a loja</Link>
        </div>
      </div>
    )
  }

  const variant = product.variants?.[selectedVariant]
  const images = product.images?.length > 0 ? product.images : [{ id: '0', url: '', isMain: true }]
  const displayPrice = variant?.price ?? product.basePrice

  return (
    <div className="min-h-screen bg-[var(--bg-page)]">
      <Navbar />

      {/* Breadcrumb */}
      <div className="max-w-7xl mx-auto px-4 py-3 flex items-center gap-1.5 text-sm text-[var(--text-tertiary)]">
        <Link href="/" className="hover:text-[var(--text-primary)]">Home</Link>
        <ChevronRight className="w-3 h-3" />
        <Link href="/products" className="hover:text-[var(--text-primary)]">Produtos</Link>
        <ChevronRight className="w-3 h-3" />
        <span className="text-[var(--text-primary)] truncate max-w-[200px]">{product.name}</span>
      </div>

      <div className="max-w-7xl mx-auto px-4 pb-12">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
          {/* Images */}
          <div>
            <div className="card aspect-square rounded-2xl overflow-hidden bg-[var(--bg-subtle)]">
              {images[selectedImage]?.url ? (
                <img src={images[selectedImage].url} alt={product.name} className="w-full h-full object-cover" />
              ) : (
                <div className="w-full h-full flex items-center justify-center">
                  <Package className="w-20 h-20 text-[var(--text-tertiary)]" />
                </div>
              )}
            </div>
            {images.length > 1 && (
              <div className="flex gap-2 mt-3">
                {images.map((img, i) => (
                  <button
                    key={img.id}
                    onClick={() => setSelectedImage(i)}
                    className={`w-16 h-16 rounded-lg overflow-hidden border-2 transition-colors ${
                      i === selectedImage ? 'border-nexus-500' : 'border-transparent hover:border-[var(--border-default)]'
                    }`}
                  >
                    <img src={img.url} alt="" className="w-full h-full object-cover" />
                  </button>
                ))}
              </div>
            )}
          </div>

          {/* Info */}
          <div>
            <p className="text-sm text-[var(--text-tertiary)]">{product.storeName}</p>
            <h1 className="font-display text-2xl md:text-3xl font-[700] tracking-tight mt-1">{product.name}</h1>

            {/* Rating */}
            <div className="flex items-center gap-2 mt-2">
              <div className="flex items-center gap-0.5">
                {Array.from({ length: 5 }).map((_, i) => (
                  <Star key={i} className={`w-4 h-4 ${i < Math.round(product.avgRating) ? 'fill-amber-400 text-amber-400' : 'text-gray-200'}`} />
                ))}
              </div>
              <span className="text-sm text-[var(--text-secondary)]">
                {product.avgRating?.toFixed(1)} ({product.totalReviews} avaliacoes)
              </span>
            </div>

            {/* Category/brand */}
            <div className="flex gap-2 mt-3">
              {product.category && <Badge variant="primary">{product.category}</Badge>}
              {product.brand && <Badge variant="neutral">{product.brand}</Badge>}
            </div>

            {/* Price */}
            <div className="mt-6 p-4 bg-[var(--bg-subtle)] rounded-xl">
              {product.promotionalPrice ? (
                <>
                  <span className="price-old text-base">{formatCurrency(product.basePrice)}</span>
                  <div className="price text-3xl price-promo">{formatCurrency(product.promotionalPrice)}</div>
                </>
              ) : (
                <div className="price text-3xl">{formatCurrency(displayPrice)}</div>
              )}
              <p className="text-xs text-[var(--text-secondary)] mt-1">
                em ate 12x de {formatCurrency(displayPrice / 12)} sem juros
              </p>
            </div>

            {/* Variants */}
            {product.variants && product.variants.length > 1 && (
              <div className="mt-4">
                <p className="label">Variacao</p>
                <div className="flex flex-wrap gap-2">
                  {product.variants.map((v, i) => (
                    <button
                      key={v.id}
                      onClick={() => setSelectedVariant(i)}
                      className={`px-3 py-1.5 text-sm rounded-lg border transition-colors ${
                        i === selectedVariant
                          ? 'border-nexus-500 bg-nexus-50 text-nexus-700 font-medium'
                          : 'border-[var(--border-light)] hover:border-nexus-300'
                      }`}
                    >
                      {v.sku} {v.attributes ? `- ${Object.values(v.attributes).join(' ')}` : ''}
                    </button>
                  ))}
                </div>
              </div>
            )}

            {/* Stock */}
            {variant && (
              <p className="text-sm mt-3">
                {variant.stock > 0 ? (
                  <span className="text-green-600">Em estoque ({variant.stock} unidades)</span>
                ) : (
                  <span className="text-red-500">Fora de estoque</span>
                )}
              </p>
            )}

            {/* Add to cart */}
            <div className="mt-6 flex gap-3">
              <Button
                size="lg"
                className="flex-1"
                disabled={variant?.stock === 0}
                isLoading={addToCart.isPending}
                onClick={() => {
                  if (!isAuthenticated) return window.location.href = '/login'
                  if (variant) addToCart.mutate({ variantId: variant.id, sellerId: '', quantity: 1 })
                }}
              >
                <ShoppingCart className="w-5 h-5" /> Adicionar ao carrinho
              </Button>
            </div>

            {/* Trust */}
            <div className="mt-6 space-y-2.5">
              <div className="flex items-center gap-2 text-sm text-[var(--text-secondary)]">
                <Truck className="w-4 h-4 text-nexus-500" /> Entrega garantida para todo o Brasil
              </div>
              <div className="flex items-center gap-2 text-sm text-[var(--text-secondary)]">
                <Shield className="w-4 h-4 text-nexus-500" /> Compra 100% segura — Nexus Protect
              </div>
            </div>

            {/* Description */}
            <div className="mt-8">
              <h3 className="font-display font-[700] text-lg mb-2">Descricao</h3>
              <div className="text-sm text-[var(--text-secondary)] leading-relaxed whitespace-pre-line">
                {product.description || 'Sem descricao disponivel.'}
              </div>
            </div>
          </div>
        </div>

        {/* Related */}
        {related && related.length > 0 && (
          <section className="mt-16">
            <h2 className="section-title mb-6">Produtos relacionados</h2>
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
              {related.slice(0, 4).map(p => (
                <Link key={p.id} href={`/products/${p.slug}`} className="card-interactive overflow-hidden">
                  <div className="aspect-square bg-[var(--bg-subtle)] flex items-center justify-center">
                    {p.mainImageUrl ? <img src={p.mainImageUrl} alt={p.name} className="w-full h-full object-cover" /> : <Package className="w-8 h-8 text-[var(--text-tertiary)]" />}
                  </div>
                  <div className="p-3">
                    <h3 className="text-sm font-medium line-clamp-2">{p.name}</h3>
                    <span className="price text-sm mt-1 block">{formatCurrency(p.basePrice)}</span>
                  </div>
                </Link>
              ))}
            </div>
          </section>
        )}
      </div>
    </div>
  )
}
