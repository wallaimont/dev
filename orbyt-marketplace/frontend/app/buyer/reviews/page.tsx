"use client";

import { Panel } from "@/components/ui/panel";
import { fetchProducts } from "@/lib/api";
import { useQuery } from "@tanstack/react-query";

export default function ReviewsPage() {
  const { data: products = [] } = useQuery({
    queryKey: ["catalog-products"],
    queryFn: fetchProducts,
  });

  return (
    <main className="mx-auto min-h-screen max-w-4xl px-6 py-10">
      <h1 className="mb-8 text-3xl font-bold text-slate-900">Minhas Avaliações</h1>

      <div className="space-y-4">
        {products.length === 0 ? (
          <Panel title="Sem avaliações" subtitle="Faça compras para avaliar produtos" />
        ) : (
          products.slice(0, 5).map((product) => (
            <Panel key={product.id} title={product.name} subtitle={`SKU: ${product.sku}`}>
              <div className="flex items-center gap-1 mb-2">
                {[1, 2, 3, 4, 5].map((star) => (
                  <span
                    key={star}
                    className={`text-lg ${star <= 4 ? "text-amber-400" : "text-slate-300"}`}
                  >
                    ★
                  </span>
                ))}
                <span className="ml-2 text-sm text-slate-500">4.0</span>
              </div>
              <p className="text-sm text-slate-600">
                Produto de excelente qualidade, entrega dentro do prazo.
              </p>
              <p className="mt-2 text-xs text-slate-400">Avaliado em 15/03/2026</p>
            </Panel>
          ))
        )}
      </div>
    </main>
  );
}
