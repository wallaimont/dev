"use client";

import { OrderCard } from "@/components/ui/order-card";
import { Panel } from "@/components/ui/panel";
import { fetchSellerOrders } from "@/lib/api";
import { useQuery } from "@tanstack/react-query";

export default function SellerOrdersPage() {
  const { data: orders = [], isLoading } = useQuery({
    queryKey: ["seller-orders"],
    queryFn: () => fetchSellerOrders("current-seller-id")
  });

  return (
    <main className="mx-auto min-h-screen max-w-5xl px-6 py-10">
      <h1 className="mb-8 text-3xl font-bold text-slate-900">Pedidos recebidos</h1>

      {isLoading ? (
        <div className="flex justify-center py-20">
          <div className="h-8 w-8 animate-spin rounded-full border-4 border-amber-400 border-t-transparent" />
        </div>
      ) : orders.length === 0 ? (
        <Panel title="Nenhum pedido" subtitle="Aguardando suas primeiras vendas!" />
      ) : (
        <div className="space-y-4">
          {orders.map((order) => (
            <OrderCard key={order.id} order={order} showTracking={false} />
          ))}
        </div>
      )}
    </main>
  );
}
