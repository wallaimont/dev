"use client";

import { Panel } from "@/components/ui/panel";
import { fetchProducts, fetchReviews, addToCart, createReview } from "@/lib/api";
import { useCartStore } from "@/store/cart-store";
import { useAuthStore } from "@/store/auth-store";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useParams } from "next/navigation";
import Link from "next/link";
import { useState } from "react";

export default function ProductDetailPage() {
  const { id } = useParams<{ id: string }>();
  const queryClient = useQueryClient();
  const increment = useCartStore((s) => s.increment);
  const { accessToken } = useAuthStore();
  const [rating, setRating] = useState(5);
  const [title, setTitle] = useState("");
  const [comment, setComment] = useState("");

  const { data: products = [] } = useQuery({
    queryKey: ["catalog-products"],
    queryFn: fetchProducts,
  });

  const product = products.find((p) => p.id === id);

  const { data: reviews = [] } = useQuery({
    queryKey: ["product-reviews", id],
    queryFn: () => fetchReviews(id),
    enabled: Boolean(id),
  });

  const addMutation = useMutation({
    mutationFn: () => addToCart(id, 1),
    onSuccess: () => {
      increment();
      queryClient.invalidateQueries({ queryKey: ["cart"] });
    },
  });

  const reviewMutation = useMutation({
    mutationFn: () => createReview(id, rating, title, comment),
    onSuccess: () => {
      setTitle("");
      setComment("");
      queryClient.invalidateQueries({ queryKey: ["product-reviews", id] });
    },
  });

  if (!product) {
    return (
      <main className="mx-auto min-h-screen max-w-4xl px-6 py-10">
        <p className="text-sm text-slate-500">Produto não encontrado ou carregando...</p>
        <Link href="/catalog" className="mt-4 inline-block text-sm text-amber-600 hover:underline">
          Voltar ao catálogo
        </Link>
      </main>
    );
  }

  return (
    <main className="mx-auto min-h-screen max-w-5xl px-6 py-10">
      <Link href="/catalog" className="mb-6 inline-block text-sm text-amber-600 hover:underline">
        ← Voltar ao catálogo
      </Link>

      <div className="grid gap-8 lg:grid-cols-[1fr_1fr]">
        <div className="flex aspect-square items-center justify-center rounded-3xl bg-slate-100">
          <span className="text-6xl text-slate-300">📦</span>
        </div>

        <div>
          <h1 className="text-3xl font-bold text-slate-900">{product.name}</h1>
          <p className="mb-2 text-sm text-slate-500">SKU: {product.sku}</p>
          <span className={`rounded-full px-2.5 py-1 text-xs font-semibold ${
            product.approvalStatus === "APPROVED" ? "text-emerald-600 bg-emerald-50" : "text-amber-600 bg-amber-50"
          }`}>
            {product.approvalStatus}
          </span>

          <div className="mt-6">
            {product.promotionalPrice ? (
              <>
                <p className="text-sm text-slate-400 line-through">
                  {product.currencyCode} {product.price.toFixed(2)}
                </p>
                <p className="text-3xl font-bold text-emerald-600">
                  {product.currencyCode} {product.promotionalPrice.toFixed(2)}
                </p>
              </>
            ) : (
              <p className="text-3xl font-bold text-slate-900">
                {product.currencyCode} {product.price.toFixed(2)}
              </p>
            )}
          </div>

          <button
            onClick={() => addMutation.mutate()}
            disabled={addMutation.isPending}
            className="mt-6 w-full rounded-xl bg-amber-400 py-3 font-semibold text-slate-900 transition hover:bg-amber-500 disabled:opacity-50"
          >
            Adicionar ao carrinho
          </button>
          {addMutation.isSuccess && <p className="mt-2 text-sm text-emerald-600">Adicionado ao carrinho!</p>}
        </div>
      </div>

      {/* Reviews section */}
      <div className="mt-12">
        <h2 className="mb-6 text-2xl font-semibold text-slate-900">Avaliações</h2>

        {accessToken && (
          <Panel title="Avaliar produto">
            <div className="space-y-3">
              <div className="flex gap-1">
                {[1, 2, 3, 4, 5].map((star) => (
                  <button
                    key={star}
                    onClick={() => setRating(star)}
                    className={`text-2xl ${star <= rating ? "text-amber-400" : "text-slate-300"}`}
                  >
                    ★
                  </button>
                ))}
              </div>
              <input
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                placeholder="Título da avaliação"
                className="w-full rounded-xl border border-slate-200 px-4 py-2.5 text-sm outline-none focus:border-amber-400"
              />
              <textarea
                value={comment}
                onChange={(e) => setComment(e.target.value)}
                placeholder="Seu comentário..."
                rows={3}
                className="w-full rounded-xl border border-slate-200 px-4 py-2.5 text-sm outline-none focus:border-amber-400"
              />
              <button
                onClick={() => reviewMutation.mutate()}
                disabled={!title || !comment || reviewMutation.isPending}
                className="rounded-xl bg-slate-900 px-6 py-2.5 text-sm font-medium text-white disabled:opacity-50"
              >
                Enviar avaliação
              </button>
            </div>
          </Panel>
        )}

        <div className="mt-6 space-y-4">
          {reviews.length === 0 ? (
            <p className="text-sm text-slate-500">Ainda sem avaliações para este produto.</p>
          ) : (
            reviews.map((r) => (
              <div key={r.id} className="rounded-2xl border border-slate-100 bg-white p-4">
                <div className="flex items-center gap-1 mb-1">
                  {[1, 2, 3, 4, 5].map((star) => (
                    <span key={star} className={`text-sm ${star <= r.rating ? "text-amber-400" : "text-slate-300"}`}>
                      ★
                    </span>
                  ))}
                </div>
                <p className="font-medium text-slate-900">{r.title}</p>
                <p className="text-sm text-slate-600">{r.comment}</p>
                <p className="mt-1 text-xs text-slate-400">
                  {new Date(r.createdAt).toLocaleDateString("pt-BR")}
                </p>
              </div>
            ))
          )}
        </div>
      </div>
    </main>
  );
}
