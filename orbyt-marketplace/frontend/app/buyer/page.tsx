"use client";

import { Panel } from "@/components/ui/panel";
import { fetchProducts } from "@/lib/api";
import { useQuery } from "@tanstack/react-query";

export default function BuyerPage() {
  const { data: products = [] } = useQuery({
    queryKey: ["buyer-products"],
    queryFn: fetchProducts
  });

  return (
    <main className="mx-auto grid min-h-screen max-w-7xl gap-6 px-6 py-10 lg:grid-cols-[0.8fr_1.2fr]">
      <Panel title="Minha jornada" subtitle="Pedidos, favoritos, mensagens e rastreio">
        <div className="space-y-3 text-sm text-slate-600">
          <p>Pedidos recentes: 12</p>
          <p>Favoritos ativos: 48</p>
          <p>Cupons disponiveis: 4</p>
          <p>Produtos em vitrine API: {products.length}</p>
        </div>
      </Panel>
      <Panel title="Pedidos em andamento" subtitle="Experiencia mobile first com foco em clareza">
        <div className="space-y-4">
          {products.slice(0, 3).map((product) => (
            <div key={product.id} className="rounded-2xl bg-slate-50 p-4">
              <p className="font-medium text-slate-900">{product.name}</p>
              <p className="text-sm text-slate-500">{product.sku}</p>
            </div>
          ))}
        </div>
      </Panel>
    </main>
  );
}
