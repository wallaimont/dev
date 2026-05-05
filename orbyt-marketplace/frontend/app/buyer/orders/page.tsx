"use client";

import { OrderCard } from "@/components/ui/order-card";
import { Panel } from "@/components/ui/panel";
import { fetchMyOrders } from "@/lib/api";
import { useAuthStore } from "@/store/auth-store";
import { useQuery } from "@tanstack/react-query";
import Link from "next/link";

export default function BuyerOrdersPage() {
  const accessToken = useAuthStore((s) => s.accessToken);
  const buyerId = extractUserIdFromToken(accessToken);

  const { data: orders = [], isLoading } = useQuery({
    queryKey: ["my-orders", buyerId],
    enabled: !!buyerId,
    queryFn: () => fetchMyOrders(buyerId!)
  });

  return (
    <main className="mx-auto min-h-screen max-w-5xl px-6 py-10">
      <div className="mb-8 flex items-center justify-between">
        <h1 className="text-3xl font-bold text-slate-900">Meus Pedidos</h1>
        <Link
          href="/catalog"
          className="rounded-full bg-amber-400 px-5 py-2.5 text-sm font-medium text-slate-900"
        >
          Continuar comprando
        </Link>
      </div>

      {isLoading ? (
        <div className="flex justify-center py-20">
          <div className="h-8 w-8 animate-spin rounded-full border-4 border-amber-400 border-t-transparent" />
        </div>
      ) : orders.length === 0 ? (
        <Panel title="Nenhum pedido" subtitle="Você ainda não fez nenhuma compra" />
      ) : (
        <div className="space-y-5">
          {orders.map((group) => (
            <Panel
              key={group.id}
              title={`Pedido #${group.id.slice(0, 8)}`}
              subtitle={new Date(group.createdAt).toLocaleDateString("pt-BR")}
            >
              <div className="space-y-4">
                {group.orders.map((order) => (
                  <OrderCard
                    key={order.id}
                    order={order}
                    title={`Pedido #${order.id.slice(0, 8)}`}
                  />
                ))}
                <div className="text-right text-lg font-bold text-slate-900">
                  Total: R$ {group.grandTotal.toFixed(2)}
                </div>
              </div>
            </Panel>
          ))}
        </div>
      )}
    </main>
  );
}

function extractUserIdFromToken(token: string | null): string | null {
  if (!token) {
    return null;
  }
  try {
    const payload = token.split(".")[1];
    if (!payload) {
      return null;
    }
    const decoded = JSON.parse(atob(payload.replace(/-/g, "+").replace(/_/g, "/"))) as { sub?: string };
    return decoded.sub ?? null;
  } catch {
    return null;
  }
}
