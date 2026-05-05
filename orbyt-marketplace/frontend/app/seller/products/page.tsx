"use client";

import { Panel } from "@/components/ui/panel";
import { ProductCard } from "@/components/ui/product-card";
import { fetchProducts, createProduct } from "@/lib/api";
import { useAuthStore } from "@/store/auth-store";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";

export default function SellerProductsPage() {
  const queryClient = useQueryClient();
  const { accessToken } = useAuthStore();
  const [showForm, setShowForm] = useState(false);
  const [sku, setSku] = useState("");
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [price, setPrice] = useState("");

  const { data: products = [], isLoading } = useQuery({
    queryKey: ["catalog-products"],
    queryFn: fetchProducts,
  });

  const createMutation = useMutation({
    mutationFn: () => {
      const ref = products[0];
      return createProduct({
        storeId: ref?.storeId ?? "",
        categoryId: ref?.categoryId ?? "",
        sku,
        name,
        description,
        price: parseFloat(price),
        currencyCode: "BRL",
      });
    },
    onSuccess: () => {
      setSku("");
      setName("");
      setDescription("");
      setPrice("");
      setShowForm(false);
      queryClient.invalidateQueries({ queryKey: ["catalog-products"] });
    },
  });

  return (
    <main className="mx-auto min-h-screen max-w-6xl px-6 py-10">
      <div className="mb-8 flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-slate-900">Meus Produtos</h1>
          <p className="text-sm text-slate-500">{products.length} produtos cadastrados</p>
        </div>
        <button
          onClick={() => setShowForm(!showForm)}
          className="rounded-full bg-amber-400 px-5 py-2.5 text-sm font-semibold text-slate-900"
        >
          {showForm ? "Cancelar" : "+ Novo produto"}
        </button>
      </div>

      {showForm && (
        <Panel title="Cadastrar produto">
          <div className="grid gap-3 sm:grid-cols-2">
            <input value={sku} onChange={(e) => setSku(e.target.value)} placeholder="SKU" className="rounded-xl border border-slate-200 p-3 text-sm" />
            <input value={name} onChange={(e) => setName(e.target.value)} placeholder="Nome" className="rounded-xl border border-slate-200 p-3 text-sm" />
            <textarea value={description} onChange={(e) => setDescription(e.target.value)} placeholder="Descrição" className="col-span-full rounded-xl border border-slate-200 p-3 text-sm" rows={3} />
            <input value={price} onChange={(e) => setPrice(e.target.value)} type="number" step="0.01" placeholder="Preço" className="rounded-xl border border-slate-200 p-3 text-sm" />
            <button
              onClick={() => createMutation.mutate()}
              disabled={!sku || !name || !description || !price || !accessToken || createMutation.isPending}
              className="rounded-xl bg-slate-900 py-3 font-medium text-white disabled:opacity-50"
            >
              {createMutation.isPending ? "Criando..." : "Criar produto"}
            </button>
          </div>
          {createMutation.isError && <p className="mt-2 text-sm text-red-600">Erro ao criar produto.</p>}
          {createMutation.isSuccess && <p className="mt-2 text-sm text-emerald-600">Produto criado!</p>}
        </Panel>
      )}

      {isLoading ? (
        <div className="flex justify-center py-20">
          <div className="h-8 w-8 animate-spin rounded-full border-4 border-amber-400 border-t-transparent" />
        </div>
      ) : (
        <div className="mt-6 grid gap-5 sm:grid-cols-2 lg:grid-cols-3">
          {products.map((product) => (
            <ProductCard
              key={product.id}
              product={product}
              href={`/product/${product.id}`}
              hrefLabel="Ver detalhes"
            />
          ))}
        </div>
      )}
    </main>
  );
}
