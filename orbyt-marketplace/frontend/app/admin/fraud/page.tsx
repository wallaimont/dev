"use client";

import { Panel } from "@/components/ui/panel";
import { useAuthStore } from "@/store/auth-store";

const mockAnalyses = [
  { id: "1", orderId: "ORD-0042", score: 92, decision: "APPROVED", reason: "Endereço verificado, histórico limpo", createdAt: "2026-03-28T14:30:00" },
  { id: "2", orderId: "ORD-0043", score: 35, decision: "REJECTED", reason: "IP em blacklist, email descartável", createdAt: "2026-03-28T14:45:00" },
  { id: "3", orderId: "ORD-0044", score: 68, decision: "MANUAL_REVIEW", reason: "Valor alto para primeiro pedido", createdAt: "2026-03-28T15:00:00" },
  { id: "4", orderId: "ORD-0045", score: 88, decision: "APPROVED", reason: "Comprador recorrente, score alto", createdAt: "2026-03-28T15:20:00" },
  { id: "5", orderId: "ORD-0046", score: 12, decision: "REJECTED", reason: "CPF inválido, device fingerprint suspeito", createdAt: "2026-03-28T15:35:00" },
];

export default function AdminFraudPage() {
  const { accessToken } = useAuthStore();

  const approved = mockAnalyses.filter((a) => a.decision === "APPROVED").length;
  const rejected = mockAnalyses.filter((a) => a.decision === "REJECTED").length;
  const review = mockAnalyses.filter((a) => a.decision === "MANUAL_REVIEW").length;
  const avgScore = Math.round(mockAnalyses.reduce((s, a) => s + a.score, 0) / mockAnalyses.length);

  const decisionColors: Record<string, string> = {
    APPROVED: "text-emerald-600 bg-emerald-50",
    REJECTED: "text-red-600 bg-red-50",
    MANUAL_REVIEW: "text-amber-600 bg-amber-50",
  };

  const scoreColor = (score: number) => {
    if (score >= 70) return "text-emerald-600";
    if (score >= 40) return "text-amber-600";
    return "text-red-600";
  };

  return (
    <main className="mx-auto min-h-screen max-w-6xl px-6 py-10">
      <h1 className="mb-8 text-3xl font-bold text-slate-900">Painel Antifraude</h1>

      <div className="mb-8 grid gap-5 sm:grid-cols-4">
        <Panel title="Score médio" subtitle="Última hora">
          <p className={`text-4xl font-semibold ${scoreColor(avgScore)}`}>{avgScore}</p>
        </Panel>
        <Panel title="Aprovados" subtitle="Aprovação automática">
          <p className="text-4xl font-semibold text-emerald-600">{approved}</p>
        </Panel>
        <Panel title="Rejeitados" subtitle="Bloqueio automático">
          <p className="text-4xl font-semibold text-red-600">{rejected}</p>
        </Panel>
        <Panel title="Revisão manual" subtitle="Aguardando analista">
          <p className="text-4xl font-semibold text-amber-600">{review}</p>
        </Panel>
      </div>

      <div className="overflow-x-auto rounded-2xl border border-slate-100 bg-white">
        <table className="w-full text-left text-sm">
          <thead>
            <tr className="border-b border-slate-100 text-slate-500">
              <th className="px-4 py-3 font-medium">Pedido</th>
              <th className="px-4 py-3 font-medium">Score</th>
              <th className="px-4 py-3 font-medium">Decisão</th>
              <th className="px-4 py-3 font-medium">Motivo</th>
              <th className="px-4 py-3 font-medium">Data</th>
            </tr>
          </thead>
          <tbody>
            {mockAnalyses.map((a) => (
              <tr key={a.id} className="border-b border-slate-50 last:border-0">
                <td className="px-4 py-3 font-mono text-xs font-semibold text-slate-900">{a.orderId}</td>
                <td className={`px-4 py-3 font-bold ${scoreColor(a.score)}`}>{a.score}</td>
                <td className="px-4 py-3">
                  <span className={`rounded-full px-2.5 py-1 text-xs font-semibold ${decisionColors[a.decision] ?? ""}`}>
                    {a.decision}
                  </span>
                </td>
                <td className="px-4 py-3 text-slate-600">{a.reason}</td>
                <td className="px-4 py-3 text-xs text-slate-400">
                  {new Date(a.createdAt).toLocaleString("pt-BR")}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </main>
  );
}
