"use client";

import { Panel } from "@/components/ui/panel";
import { createProduct, fetchProducts, loginSeller } from "@/lib/api";
import { useAuthStore } from "@/store/auth-store";
import { zodResolver } from "@hookform/resolvers/zod";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useForm } from "react-hook-form";
import { z } from "zod";

const formSchema = z.object({
  sku: z.string().min(3),
  name: z.string().min(3),
  description: z.string().min(10),
  price: z.coerce.number().positive(),
  promotionalPrice: z.coerce.number().positive().optional()
});

type FormInput = z.infer<typeof formSchema>;

export default function SellerPage() {
  const queryClient = useQueryClient();
  const { accessToken, setTokens } = useAuthStore();
  const { data: products = [] } = useQuery({ queryKey: ["catalog-products"], queryFn: fetchProducts });

  const loginMutation = useMutation({
    mutationFn: () => loginSeller("admin@orbyt.local", "Admin@123"),
    onSuccess: (data) => {
      setTokens(data.accessToken, data.refreshToken);
    }
  });

  const form = useForm<FormInput>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      sku: "",
      name: "",
      description: "",
      price: 0
    }
  });

  const createMutation = useMutation({
    mutationFn: async (values: FormInput) => {
      const ref = products[0];
      if (!ref) {
        throw new Error("Sem produto de referencia para store/category");
      }
      return createProduct({
        storeId: ref.storeId,
        categoryId: ref.categoryId,
        sku: values.sku,
        name: values.name,
        description: values.description,
        price: values.price,
        promotionalPrice: values.promotionalPrice,
        currencyCode: "BRL"
      });
    },
    onSuccess: async () => {
      form.reset();
      await queryClient.invalidateQueries({ queryKey: ["catalog-products"] });
    }
  });

  return (
    <main className="mx-auto grid min-h-screen max-w-7xl gap-6 px-6 py-10 lg:grid-cols-3">
      <Panel title="Receita liquida" subtitle="Ultimos 30 dias">
        <p className="text-3xl font-semibold text-slate-900">R$ 284.912</p>
      </Panel>
      <Panel title="Pedidos a expedir" subtitle="SLA e reputacao em primeiro plano">
        <p className="text-3xl font-semibold text-slate-900">94</p>
      </Panel>
      <Panel title="Repasse previsto" subtitle="Split e retencoes">
        <p className="text-3xl font-semibold text-slate-900">R$ 71.450</p>
      </Panel>

      <Panel title="Sessao API" subtitle="Auth real com JWT e refresh token">
        <button
          className="rounded-full bg-slate-900 px-4 py-2 text-sm font-medium text-white"
          onClick={() => loginMutation.mutate()}
          type="button"
        >
          {accessToken ? "Sessao ativa" : "Entrar como vendedor seed"}
        </button>
        {loginMutation.isError ? <p className="mt-2 text-sm text-red-600">Falha ao autenticar.</p> : null}
      </Panel>

      <Panel title="Novo produto" subtitle="CRUD conectado em /v1/catalog/products">
        <form className="grid gap-3" onSubmit={form.handleSubmit((values) => createMutation.mutate(values))}>
          <input className="rounded-xl border border-slate-200 p-3 text-sm" placeholder="SKU" {...form.register("sku")} />
          <input className="rounded-xl border border-slate-200 p-3 text-sm" placeholder="Nome" {...form.register("name")} />
          <textarea className="rounded-xl border border-slate-200 p-3 text-sm" placeholder="Descricao" {...form.register("description")} />
          <input className="rounded-xl border border-slate-200 p-3 text-sm" placeholder="Preco" type="number" step="0.01" {...form.register("price")} />
          <input className="rounded-xl border border-slate-200 p-3 text-sm" placeholder="Preco promocional (opcional)" type="number" step="0.01" {...form.register("promotionalPrice")} />
          <button className="rounded-full bg-amber-400 px-4 py-2 text-sm font-semibold text-slate-900" type="submit" disabled={!accessToken || createMutation.isPending}>
            Criar produto
          </button>
          {createMutation.isError ? <p className="text-sm text-red-600">Nao foi possivel criar o produto. Verifique sessao e tenant.</p> : null}
          {createMutation.isSuccess ? <p className="text-sm text-emerald-600">Produto criado com sucesso.</p> : null}
        </form>
      </Panel>
    </main>
  );
}
