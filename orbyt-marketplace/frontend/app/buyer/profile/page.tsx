"use client";

import { EmptyState } from "@/components/ui/empty-state";
import { FieldGroup } from "@/components/ui/field-group";
import { Panel } from "@/components/ui/panel";
import { useAuthStore } from "@/store/auth-store";
import { useState } from "react";

export default function ProfilePage() {
  const { accessToken } = useAuthStore();
  const [name, setName] = useState("Usuário Orbyt");
  const [phone, setPhone] = useState("");
  const [cpf, setCpf] = useState("");
  const [saved, setSaved] = useState(false);

  function handleSave() {
    setSaved(true);
    setTimeout(() => setSaved(false), 3000);
  }

  return (
    <main className="mx-auto min-h-screen max-w-3xl px-6 py-10">
      <h1 className="mb-8 text-3xl font-bold text-slate-900">Meu Perfil</h1>

      <div className="space-y-6">
        <Panel title="Dados pessoais" subtitle="Informações básicas da conta">
          <div className="space-y-4">
            <FieldGroup label="Nome">
              <input
                type="text"
                value={name}
                onChange={(e) => setName(e.target.value)}
                className="w-full rounded-xl border border-slate-200 px-4 py-2.5 text-sm outline-none focus:border-amber-400"
              />
            </FieldGroup>
            <FieldGroup label="Telefone">
              <input
                type="tel"
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
                placeholder="(11) 99999-9999"
                className="w-full rounded-xl border border-slate-200 px-4 py-2.5 text-sm outline-none focus:border-amber-400"
              />
            </FieldGroup>
            <FieldGroup label="CPF">
              <input
                type="text"
                value={cpf}
                onChange={(e) => setCpf(e.target.value)}
                placeholder="000.000.000-00"
                className="w-full rounded-xl border border-slate-200 px-4 py-2.5 text-sm outline-none focus:border-amber-400"
              />
            </FieldGroup>
            <button
              onClick={handleSave}
              className="rounded-xl bg-slate-900 px-6 py-2.5 text-sm font-medium text-white transition hover:bg-slate-800"
            >
              Salvar alterações
            </button>
            {saved && (
              <p className="text-sm text-emerald-600">Perfil atualizado com sucesso!</p>
            )}
          </div>
        </Panel>

        <Panel title="Endereços" subtitle="Gerencie seus endereços de entrega">
          <EmptyState
            title="Nenhum endereço cadastrado"
            description="Cadastre endereços de entrega para acelerar o checkout e reduzir erros no pedido."
          />
        </Panel>

        <Panel title="Segurança" subtitle="Altere sua senha">
          <div className="space-y-3">
            <input
              type="password"
              placeholder="Senha atual"
              className="w-full rounded-xl border border-slate-200 px-4 py-2.5 text-sm outline-none focus:border-amber-400"
            />
            <input
              type="password"
              placeholder="Nova senha"
              className="w-full rounded-xl border border-slate-200 px-4 py-2.5 text-sm outline-none focus:border-amber-400"
            />
            <button className="rounded-xl bg-slate-900 px-6 py-2.5 text-sm font-medium text-white transition hover:bg-slate-800">
              Alterar senha
            </button>
          </div>
        </Panel>
      </div>
    </main>
  );
}
