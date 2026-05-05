"use client";

import { Panel } from "@/components/ui/panel";

const mockPayouts = [
  { id: "1", period: "01/03 – 15/03/2026", gross: 45200.0, commission: 6780.0, net: 38420.0, status: "PAID" },
  { id: "2", period: "16/03 – 31/03/2026", gross: 32100.0, commission: 4815.0, net: 27285.0, status: "PENDING" },
  { id: "3", period: "01/04 – 15/04/2026", gross: 0, commission: 0, net: 0, status: "SCHEDULED" },
];

export default function SellerPayoutsPage() {
  const statusColors: Record<string, string> = {
    PAID: "text-emerald-600 bg-emerald-50",
    PENDING: "text-amber-600 bg-amber-50",
    SCHEDULED: "text-slate-500 bg-slate-50",
  };

  const totalNet = mockPayouts.reduce((sum, p) => sum + p.net, 0);

  return (
    <main className="mx-auto min-h-screen max-w-5xl px-6 py-10">
      <h1 className="mb-8 text-3xl font-bold text-slate-900">Repasses</h1>

      <div className="mb-8 grid gap-5 sm:grid-cols-3">
        <Panel title="Total recebido" subtitle="Repasses confirmados">
          <p className="text-3xl font-semibold text-emerald-600">
            R$ {mockPayouts.filter((p) => p.status === "PAID").reduce((s, p) => s + p.net, 0).toLocaleString("pt-BR", { minimumFractionDigits: 2 })}
          </p>
        </Panel>
        <Panel title="Pendente" subtitle="Aguardando liberação">
          <p className="text-3xl font-semibold text-amber-600">
            R$ {mockPayouts.filter((p) => p.status === "PENDING").reduce((s, p) => s + p.net, 0).toLocaleString("pt-BR", { minimumFractionDigits: 2 })}
          </p>
        </Panel>
        <Panel title="Comissão total" subtitle="15% de taxa marketplace">
          <p className="text-3xl font-semibold text-slate-600">
            R$ {mockPayouts.reduce((s, p) => s + p.commission, 0).toLocaleString("pt-BR", { minimumFractionDigits: 2 })}
          </p>
        </Panel>
      </div>

      <div className="overflow-x-auto rounded-2xl border border-slate-100 bg-white">
        <table className="w-full text-left text-sm">
          <thead>
            <tr className="border-b border-slate-100 text-slate-500">
              <th className="px-4 py-3 font-medium">Período</th>
              <th className="px-4 py-3 font-medium">Bruto</th>
              <th className="px-4 py-3 font-medium">Comissão</th>
              <th className="px-4 py-3 font-medium">Líquido</th>
              <th className="px-4 py-3 font-medium">Status</th>
            </tr>
          </thead>
          <tbody>
            {mockPayouts.map((p) => (
              <tr key={p.id} className="border-b border-slate-50 last:border-0">
                <td className="px-4 py-3 text-slate-700">{p.period}</td>
                <td className="px-4 py-3 text-slate-700">R$ {p.gross.toLocaleString("pt-BR", { minimumFractionDigits: 2 })}</td>
                <td className="px-4 py-3 text-red-500">-R$ {p.commission.toLocaleString("pt-BR", { minimumFractionDigits: 2 })}</td>
                <td className="px-4 py-3 font-medium text-slate-900">R$ {p.net.toLocaleString("pt-BR", { minimumFractionDigits: 2 })}</td>
                <td className="px-4 py-3">
                  <span className={`rounded-full px-2.5 py-1 text-xs font-semibold ${statusColors[p.status] ?? ""}`}>
                    {p.status}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </main>
  );
}
