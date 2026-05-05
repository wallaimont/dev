"use client";

import { EmptyState } from "@/components/ui/empty-state";
import { FieldGroup } from "@/components/ui/field-group";
import { Panel } from "@/components/ui/panel";
import { fetchTickets, createTicket } from "@/lib/api";
import { useChatSocket } from "@/lib/hooks/use-chat-socket";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";

export default function SupportPage() {
  const queryClient = useQueryClient();
  const [subject, setSubject] = useState("");
  const [category, setCategory] = useState("ORDER_ISSUE");
  const [message, setMessage] = useState("");

  const { data: tickets = [], isLoading } = useQuery({
    queryKey: ["support-tickets"],
    queryFn: () => fetchTickets()
  });

  useChatSocket({
    onMessage: () => queryClient.invalidateQueries({ queryKey: ["support-tickets"] })
  });

  const createMutation = useMutation({
    mutationFn: () => createTicket(subject, category, message),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["support-tickets"] });
      setSubject("");
      setMessage("");
    }
  });

  const priorityColors: Record<string, string> = {
    HIGH: "text-red-600",
    MEDIUM: "text-amber-600",
    LOW: "text-slate-500"
  };

  return (
    <main className="mx-auto min-h-screen max-w-4xl px-6 py-10">
      <h1 className="mb-8 text-3xl font-bold text-slate-900">Suporte</h1>

      <div className="grid gap-6 lg:grid-cols-[1fr_1fr]">
        <Panel title="Novo chamado">
          <div className="space-y-4">
            <FieldGroup label="Assunto">
              <input
                type="text"
                value={subject}
                onChange={(e) => setSubject(e.target.value)}
                placeholder="Assunto"
                className="w-full rounded-xl border border-slate-200 px-4 py-2.5 text-sm outline-none focus:border-amber-400"
              />
            </FieldGroup>
            <FieldGroup label="Categoria">
              <select
                value={category}
                onChange={(e) => setCategory(e.target.value)}
                className="w-full rounded-xl border border-slate-200 px-4 py-2.5 text-sm outline-none focus:border-amber-400"
              >
                <option value="ORDER_ISSUE">Problema com pedido</option>
                <option value="PAYMENT">Pagamento</option>
                <option value="SHIPPING">Entrega</option>
                <option value="PRODUCT">Produto</option>
                <option value="ACCOUNT">Conta</option>
                <option value="OTHER">Outro</option>
              </select>
            </FieldGroup>
            <FieldGroup label="Descrição">
              <textarea
                value={message}
                onChange={(e) => setMessage(e.target.value)}
                placeholder="Descreva seu problema..."
                rows={4}
                className="w-full rounded-xl border border-slate-200 px-4 py-2.5 text-sm outline-none focus:border-amber-400"
              />
            </FieldGroup>
            <button
              onClick={() => createMutation.mutate()}
              disabled={!subject || !message || createMutation.isPending}
              className="w-full rounded-xl bg-slate-900 py-3 font-medium text-white transition hover:bg-slate-800 disabled:opacity-50"
            >
              {createMutation.isPending ? "Enviando..." : "Abrir chamado"}
            </button>
          </div>
        </Panel>

        <div>
          <h2 className="mb-4 text-lg font-semibold text-slate-900">Seus chamados</h2>
          {isLoading ? (
            <div className="flex justify-center py-10">
              <div className="h-6 w-6 animate-spin rounded-full border-4 border-amber-400 border-t-transparent" />
            </div>
          ) : tickets.length === 0 ? (
            <EmptyState
              title="Nenhum chamado aberto"
              description="Quando você abrir um ticket, ele aparecerá aqui com status e prioridade."
            />
          ) : (
            <div className="space-y-3">
              {tickets.map((t) => (
                <div key={t.id} className="rounded-2xl border border-slate-100 bg-white p-4">
                  <div className="flex items-start justify-between">
                    <div>
                      <p className="font-medium text-slate-900">{t.subject}</p>
                      <p className="text-xs text-slate-500">{t.category}</p>
                    </div>
                    <span
                      className={`rounded-full bg-slate-100 px-3 py-1 text-xs font-semibold ${
                        t.status === "OPEN" ? "text-amber-600" : "text-emerald-600"
                      }`}
                    >
                      {t.status}
                    </span>
                  </div>
                  <p className={`mt-1 text-xs ${priorityColors[t.priority] ?? "text-slate-400"}`}>
                    Prioridade: {t.priority}
                  </p>
                  <p className="mt-1 text-xs text-slate-400">
                    {new Date(t.createdAt).toLocaleDateString("pt-BR")}
                  </p>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </main>
  );
}
