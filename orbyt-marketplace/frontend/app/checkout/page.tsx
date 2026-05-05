"use client";

import { Panel } from "@/components/ui/panel";
import { fetchCart, checkout, validateCoupon } from "@/lib/api";
import { useQuery, useMutation } from "@tanstack/react-query";
import { useState } from "react";
import { useRouter } from "next/navigation";

export default function CheckoutPage() {
  const router = useRouter();
  const [paymentMethod, setPaymentMethod] = useState("PIX");
  const [couponCode, setCouponCode] = useState("");
  const [couponDiscount, setCouponDiscount] = useState(0);
  const [couponError, setCouponError] = useState("");

  const { data: cart } = useQuery({
    queryKey: ["cart"],
    queryFn: fetchCart
  });

  const checkoutMutation = useMutation({
    mutationFn: () => checkout(paymentMethod),
    onSuccess: () => router.push("/buyer/orders")
  });

  const items = cart?.items ?? [];
  const subtotal = items.reduce((sum, item) => sum + item.subtotal, 0);
  const total = Math.max(0, subtotal - couponDiscount);

  const applyCoupon = async () => {
    try {
      setCouponError("");
      const result = await validateCoupon(couponCode, subtotal);
      setCouponDiscount(result.discountAmount);
    } catch {
      setCouponError("Cupom inválido ou expirado");
      setCouponDiscount(0);
    }
  };

  return (
    <main className="mx-auto min-h-screen max-w-4xl px-6 py-10">
      <h1 className="mb-8 text-3xl font-bold text-slate-900">Checkout</h1>

      <div className="grid gap-6 lg:grid-cols-[1.2fr_0.8fr]">
        <div className="space-y-6">
          <Panel title="Resumo do pedido" subtitle={`${items.length} itens`}>
            <div className="space-y-3">
              {items.map((item) => (
                <div key={item.id} className="flex justify-between text-sm">
                  <span className="text-slate-700">
                    {item.productName} x{item.quantity}
                  </span>
                  <span className="font-medium text-slate-900">R$ {item.subtotal.toFixed(2)}</span>
                </div>
              ))}
            </div>
          </Panel>

          <Panel title="Cupom de desconto">
            <div className="flex gap-3">
              <input
                type="text"
                value={couponCode}
                onChange={(e) => setCouponCode(e.target.value)}
                placeholder="Digite o cupom"
                className="flex-1 rounded-xl border border-slate-200 px-4 py-2.5 text-sm outline-none focus:border-amber-400"
              />
              <button
                onClick={applyCoupon}
                className="rounded-xl bg-slate-900 px-5 py-2.5 text-sm font-medium text-white hover:bg-slate-800"
              >
                Aplicar
              </button>
            </div>
            {couponError && <p className="mt-2 text-sm text-red-500">{couponError}</p>}
            {couponDiscount > 0 && (
              <p className="mt-2 text-sm text-emerald-600">
                Desconto aplicado: -R$ {couponDiscount.toFixed(2)}
              </p>
            )}
          </Panel>
        </div>

        <div className="space-y-6">
          <Panel title="Pagamento">
            <div className="space-y-3">
              {["PIX", "CREDIT_CARD", "BOLETO"].map((method) => (
                <button
                  key={method}
                  onClick={() => setPaymentMethod(method)}
                  className={`w-full rounded-xl border px-4 py-3 text-left text-sm font-medium transition ${
                    paymentMethod === method
                      ? "border-amber-400 bg-amber-50 text-amber-700"
                      : "border-slate-200 text-slate-700 hover:bg-slate-50"
                  }`}
                >
                  {method === "PIX"
                    ? "PIX (aprovação instantânea)"
                    : method === "CREDIT_CARD"
                    ? "Cartão de crédito"
                    : "Boleto bancário"}
                </button>
              ))}
            </div>
          </Panel>

          <div className="rounded-2xl bg-slate-950 p-6 text-white">
            <div className="space-y-2 text-sm">
              <div className="flex justify-between">
                <span className="text-slate-400">Subtotal</span>
                <span>R$ {subtotal.toFixed(2)}</span>
              </div>
              {couponDiscount > 0 && (
                <div className="flex justify-between text-emerald-400">
                  <span>Desconto</span>
                  <span>-R$ {couponDiscount.toFixed(2)}</span>
                </div>
              )}
              <div className="flex justify-between border-t border-white/10 pt-2 text-lg font-bold">
                <span>Total</span>
                <span>R$ {total.toFixed(2)}</span>
              </div>
            </div>
            <button
              onClick={() => checkoutMutation.mutate()}
              disabled={checkoutMutation.isPending || items.length === 0}
              className="mt-4 w-full rounded-full bg-amber-400 py-3 font-semibold text-slate-900 transition hover:bg-amber-500 disabled:opacity-50"
            >
              {checkoutMutation.isPending ? "Processando..." : "Confirmar pedido"}
            </button>
          </div>
        </div>
      </div>
    </main>
  );
}
