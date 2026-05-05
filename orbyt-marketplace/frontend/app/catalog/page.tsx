"use client";

import { ProductCard } from "@/components/ui/product-card";
import { fetchProducts, addToCart } from "@/lib/api";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useCartStore } from "@/store/cart-store";
import { useState } from "react";

export default function CatalogPage() {
  const queryClient = useQueryClient();
  const increment = useCartStore((s) => s.increment);
  const [search, setSearch] = useState("");

  const { data: products = [], isLoading } = useQuery({
    queryKey: ["catalog-products"],
    queryFn: fetchProducts
  });

  const addMutation = useMutation({
    mutationFn: (productId: string) => addToCart(productId, 1),
    onSuccess: () => {
      increment();
      queryClient.invalidateQueries({ queryKey: ["cart"] });
    }
  });

  const filtered = products.filter(
    (p) =>
      p.name.toLowerCase().includes(search.toLowerCase()) ||
      p.sku.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <main className="mx-auto min-h-screen max-w-7xl px-6 py-10">
      <div className="mb-8 flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-slate-900">Catálogo</h1>
          <p className="text-sm text-slate-500">{products.length} produtos disponíveis</p>
        </div>
        <input
          type="text"
          placeholder="Buscar produtos..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="rounded-full border border-slate-200 px-5 py-2.5 text-sm outline-none focus:border-amber-400 focus:ring-1 focus:ring-amber-400"
        />
      </div>

      {isLoading ? (
        <div className="flex justify-center py-20">
          <div className="h-8 w-8 animate-spin rounded-full border-4 border-amber-400 border-t-transparent" />
        </div>
      ) : (
        <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
          {filtered.map((product) => (
            <ProductCard
              key={product.id}
              product={product}
              href={`/product/${product.id}`}
              hrefLabel="Detalhes"
              actionLabel="+ Carrinho"
              actionDisabled={addMutation.isPending}
              onAction={() => addMutation.mutate(product.id)}
            />
          ))}
        </div>
      )}
    </main>
  );
}
