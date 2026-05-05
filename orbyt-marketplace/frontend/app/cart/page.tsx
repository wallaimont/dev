"use client";

import { Panel } from "@/components/ui/panel";
import { fetchCart, removeFromCart } from "@/lib/api";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useCartStore } from "@/store/cart-store";
import Link from "next/link";

export default function CartPage() {
  const queryClient = useQueryClient();
  const decrement = useCartStore((s) => s.decrement);

  const { data: cart, isLoading } = useQuery({
    queryKey: ["cart"],
    queryFn: fetchCart
  });

  const removeMutation = useMutation({
    mutationFn: (itemId: string) => removeFromCart(itemId),
    onSuccess: () => {
      decrement();
      queryClient.invalidateQueries({ queryKey: ["cart"] });
    }
  });

  const items = cart?.items ?? [];
  const total = items.reduce((sum, item) => sum + item.subtotal, 0);

  return (
    <main className="mx-auto min-h-screen max-w-4xl px-6 py-10">
      <h1 className="mb-8 text-3xl font-bold text-slate-900">Carrinho</h1>

      {isLoading ? (
        <div className="flex justify-center py-20">
          <div className="h-8 w-8 animate-spin rounded-full border-4 border-amber-400 border-t-transparent" />
        </div>
      ) : items.length === 0 ? (
        <Panel title="Carrinho vazio" subtitle="Adicione produtos do catálogo para começar">
          <Link href="/catalog" className="text-amber-600 underline">
            Ir para o catálogo
          </Link>
        </Panel>
      ) : (
        <>
          <div className="space-y-4">
            {items.map((item) => (
              <div
                key={item.id}
                className="flex items-center justify-between rounded-2xl border border-slate-100 bg-white p-5"
              >
                <div>
                  <p className="font-semibold text-slate-900">{item.productName}</p>
                  <p className="text-sm text-slate-500">
                    {item.quantity}x R$ {item.unitPrice.toFixed(2)}
                  </p>
                </div>
                <div className="flex items-center gap-4">
                  <p className="font-bold text-slate-800">R$ {item.subtotal.toFixed(2)}</p>
                  <button
                    onClick={() => removeMutation.mutate(item.id)}
                    className="rounded-full bg-red-50 px-3 py-1.5 text-xs font-medium text-red-600 hover:bg-red-100"
                  >
                    Remover
                  </button>
                </div>
              </div>
            ))}
          </div>

          <div className="mt-8 flex items-center justify-between rounded-2xl bg-slate-950 px-8 py-6 text-white">
            <div>
              <p className="text-sm text-slate-400">Total do carrinho</p>
              <p className="text-3xl font-bold">R$ {total.toFixed(2)}</p>
            </div>
            <Link
              href="/checkout"
              className="rounded-full bg-amber-400 px-6 py-3 font-semibold text-slate-900 transition hover:bg-amber-500"
            >
              Finalizar compra
            </Link>
          </div>
        </>
      )}
    </main>
  );
}
