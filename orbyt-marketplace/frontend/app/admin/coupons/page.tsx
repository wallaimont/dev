"use client";

import { FieldGroup } from "@/components/ui/field-group";
import { Panel } from "@/components/ui/panel";
import { useState } from "react";

const mockCoupons = [
  { id: "1", code: "ORBYT10", type: "PERCENTAGE", value: 10, minOrder: 100, maxUses: 500, used: 142, active: true, expiresAt: "2026-06-30" },
  { id: "2", code: "FRETE-GRATIS", type: "FREE_SHIPPING", value: 0, minOrder: 150, maxUses: 200, used: 87, active: true, expiresAt: "2026-04-30" },
  { id: "3", code: "WELCOME20", type: "PERCENTAGE", value: 20, minOrder: 0, maxUses: 1000, used: 1000, active: false, expiresAt: "2026-01-15" },
  { id: "4", code: "SALE50", type: "FIXED", value: 50, minOrder: 200, maxUses: 100, used: 34, active: true, expiresAt: "2026-12-31" },
];

export default function AdminCouponsPage() {
  const [showForm, setShowForm] = useState(false);

  return (
    <main className="mx-auto min-h-screen max-w-6xl px-6 py-10">
      <div className="mb-8 flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-slate-900">Cupons & Promoções</h1>
          <p className="text-sm text-slate-500">{mockCoupons.length} cupons cadastrados</p>
        </div>
        <button
          onClick={() => setShowForm(!showForm)}
          className="rounded-full bg-amber-400 px-5 py-2.5 text-sm font-semibold text-slate-900"
        >
          {showForm ? "Cancelar" : "+ Novo cupom"}
        </button>
      </div>

      {showForm && (
        <Panel title="Criar cupom">
          <div className="grid gap-3 sm:grid-cols-2">
            <FieldGroup label="Código">
              <input placeholder="Código (ex: ORBYT10)" className="w-full rounded-xl border border-slate-200 p-3 text-sm" />
            </FieldGroup>
            <FieldGroup label="Tipo">
              <select className="w-full rounded-xl border border-slate-200 p-3 text-sm">
                <option value="PERCENTAGE">Percentual (%)</option>
                <option value="FIXED">Valor fixo (R$)</option>
                <option value="FREE_SHIPPING">Frete grátis</option>
              </select>
            </FieldGroup>
            <FieldGroup label="Valor do desconto">
              <input type="number" placeholder="Valor do desconto" className="w-full rounded-xl border border-slate-200 p-3 text-sm" />
            </FieldGroup>
            <FieldGroup label="Pedido mínimo">
              <input type="number" placeholder="Pedido mínimo (R$)" className="w-full rounded-xl border border-slate-200 p-3 text-sm" />
            </FieldGroup>
            <FieldGroup label="Máximo de usos">
              <input type="number" placeholder="Máx. de usos" className="w-full rounded-xl border border-slate-200 p-3 text-sm" />
            </FieldGroup>
            <FieldGroup label="Expiração">
              <input type="date" className="w-full rounded-xl border border-slate-200 p-3 text-sm" />
            </FieldGroup>
            <button className="col-span-full rounded-xl bg-slate-900 py-3 font-medium text-white">
              Criar cupom
            </button>
          </div>
        </Panel>
      )}

      <div className="mt-6 overflow-x-auto rounded-2xl border border-slate-100 bg-white">
        <table className="w-full text-left text-sm">
          <thead>
            <tr className="border-b border-slate-100 text-slate-500">
              <th className="px-4 py-3 font-medium">Código</th>
              <th className="px-4 py-3 font-medium">Tipo</th>
              <th className="px-4 py-3 font-medium">Valor</th>
              <th className="px-4 py-3 font-medium">Pedido Mín.</th>
              <th className="px-4 py-3 font-medium">Uso</th>
              <th className="px-4 py-3 font-medium">Expira</th>
              <th className="px-4 py-3 font-medium">Status</th>
            </tr>
          </thead>
          <tbody>
            {mockCoupons.map((c) => (
              <tr key={c.id} className="border-b border-slate-50 last:border-0">
                <td className="px-4 py-3 font-mono font-semibold text-slate-900">{c.code}</td>
                <td className="px-4 py-3 text-slate-600">{c.type}</td>
                <td className="px-4 py-3 text-slate-700">
                  {c.type === "PERCENTAGE" ? `${c.value}%` : c.type === "FIXED" ? `R$ ${c.value}` : "—"}
                </td>
                <td className="px-4 py-3 text-slate-500">R$ {c.minOrder}</td>
                <td className="px-4 py-3 text-slate-600">{c.used}/{c.maxUses}</td>
                <td className="px-4 py-3 text-slate-500">{c.expiresAt}</td>
                <td className="px-4 py-3">
                  <span className={`rounded-full px-2.5 py-1 text-xs font-semibold ${c.active ? "text-emerald-600 bg-emerald-50" : "text-slate-500 bg-slate-100"}`}>
                    {c.active ? "ATIVO" : "EXPIRADO"}
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
