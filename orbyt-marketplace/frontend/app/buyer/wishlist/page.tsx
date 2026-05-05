"use client";

import { EmptyState } from "@/components/ui/empty-state";
import { Panel } from "@/components/ui/panel";
import { ProductCard } from "@/components/ui/product-card";
import { fetchProducts } from "@/lib/api";
import { useQuery } from "@tanstack/react-query";

export default function WishlistPage() {
  const { data: products = [] } = useQuery({
    queryKey: ["catalog-products"],
    queryFn: fetchProducts,
  });

  return (
    <main className="mx-auto min-h-screen max-w-5xl px-6 py-10">
      <h1 className="mb-8 text-3xl font-bold text-slate-900">Meus Favoritos</h1>

      {products.length === 0 ? (
        <Panel title="Lista vazia" subtitle="Adicione produtos aos favoritos pelo catálogo">
          <EmptyState
            title="Nenhum favorito salvo"
            description="Adicione produtos aos favoritos pelo catálogo para acompanhar preço e disponibilidade."
            actionHref="/catalog"
            actionLabel="Ir para o catálogo"
          />
        </Panel>
      ) : (
        <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-3">
          {products.slice(0, 6).map((product) => (
            <ProductCard
              key={product.id}
              product={product}
              href={`/product/${product.id}`}
            />
          ))}
        </div>
      )}
    </main>
  );
}
