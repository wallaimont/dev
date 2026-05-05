"use client";

import { EmptyState } from "@/components/ui/empty-state";
import { FieldGroup } from "@/components/ui/field-group";
import { Panel } from "@/components/ui/panel";
import { fetchBanners } from "@/lib/api";
import { useQuery } from "@tanstack/react-query";
import { useState } from "react";

export default function AdminCmsPage() {
  const [showForm, setShowForm] = useState(false);

  const { data: banners = [], isLoading } = useQuery({
    queryKey: ["cms-banners"],
    queryFn: () => fetchBanners("HOME_HERO"),
  });

  const mockPages = [
    { id: "1", title: "Sobre nós", slug: "sobre-nos", status: "PUBLISHED" },
    { id: "2", title: "Política de privacidade", slug: "privacidade", status: "PUBLISHED" },
    { id: "3", title: "Termos de uso", slug: "termos", status: "DRAFT" },
  ];

  return (
    <main className="mx-auto min-h-screen max-w-6xl px-6 py-10">
      <h1 className="mb-8 text-3xl font-bold text-slate-900">Gestão de Conteúdo</h1>

      <div className="mb-10">
        <div className="mb-4 flex items-center justify-between">
          <h2 className="text-xl font-semibold text-slate-800">Banners</h2>
          <button
            onClick={() => setShowForm(!showForm)}
            className="rounded-full bg-amber-400 px-4 py-2 text-sm font-semibold text-slate-900"
          >
            {showForm ? "Cancelar" : "+ Novo banner"}
          </button>
        </div>

        {showForm && (
          <Panel title="Criar banner">
            <div className="grid gap-3 sm:grid-cols-2">
              <FieldGroup label="Título">
                <input placeholder="Título" className="w-full rounded-xl border border-slate-200 p-3 text-sm" />
              </FieldGroup>
              <FieldGroup label="URL da imagem">
                <input placeholder="URL da imagem" className="w-full rounded-xl border border-slate-200 p-3 text-sm" />
              </FieldGroup>
              <FieldGroup label="Link de destino">
                <input placeholder="Link de destino" className="w-full rounded-xl border border-slate-200 p-3 text-sm" />
              </FieldGroup>
              <FieldGroup label="Posição">
                <select className="w-full rounded-xl border border-slate-200 p-3 text-sm">
                  <option value="HOME_HERO">Home Hero</option>
                  <option value="HOME_SECONDARY">Home Secundário</option>
                  <option value="CATEGORY_TOP">Topo Categoria</option>
                </select>
              </FieldGroup>
              <button className="col-span-full rounded-xl bg-slate-900 py-3 font-medium text-white">
                Criar banner
              </button>
            </div>
          </Panel>
        )}

        {isLoading ? (
          <div className="flex justify-center py-10">
            <div className="h-6 w-6 animate-spin rounded-full border-4 border-amber-400 border-t-transparent" />
          </div>
        ) : banners.length === 0 ? (
          <EmptyState
            title="Nenhum banner cadastrado"
            description="Crie banners de destaque para hero, categoria ou campanhas sazonais."
          />
        ) : (
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {banners.map((b) => (
              <Panel key={b.id} title={b.title} subtitle={b.position}>
                <div className="aspect-video rounded-xl bg-slate-100 flex items-center justify-center text-xs text-slate-400">
                  {b.imageUrl || "Sem imagem"}
                </div>
              </Panel>
            ))}
          </div>
        )}
      </div>

      <div>
        <h2 className="mb-4 text-xl font-semibold text-slate-800">Páginas Estáticas</h2>
        <div className="overflow-x-auto rounded-2xl border border-slate-100 bg-white">
          <table className="w-full text-left text-sm">
            <thead>
              <tr className="border-b border-slate-100 text-slate-500">
                <th className="px-4 py-3 font-medium">Título</th>
                <th className="px-4 py-3 font-medium">Slug</th>
                <th className="px-4 py-3 font-medium">Status</th>
                <th className="px-4 py-3 font-medium">Ações</th>
              </tr>
            </thead>
            <tbody>
              {mockPages.map((p) => (
                <tr key={p.id} className="border-b border-slate-50 last:border-0">
                  <td className="px-4 py-3 font-medium text-slate-900">{p.title}</td>
                  <td className="px-4 py-3 font-mono text-xs text-slate-500">/{p.slug}</td>
                  <td className="px-4 py-3">
                    <span className={`rounded-full px-2.5 py-1 text-xs font-semibold ${
                      p.status === "PUBLISHED" ? "text-emerald-600 bg-emerald-50" : "text-amber-600 bg-amber-50"
                    }`}>
                      {p.status}
                    </span>
                  </td>
                  <td className="px-4 py-3">
                    <button className="text-xs text-amber-600 hover:underline">Editar</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </main>
  );
}
