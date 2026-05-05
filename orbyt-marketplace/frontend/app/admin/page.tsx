"use client";

import { Panel } from "@/components/ui/panel";
import { fetchAdminDashboard, loginSeller } from "@/lib/api";
import { useAuthStore } from "@/store/auth-store";
import { useMutation, useQuery } from "@tanstack/react-query";

export default function AdminPage() {
  const { accessToken, setTokens } = useAuthStore();
  const loginMutation = useMutation({
    mutationFn: () => loginSeller("admin@orbyt.local", "Admin@123"),
    onSuccess: (data) => setTokens(data.accessToken, data.refreshToken)
  });

  const dashboard = useQuery({
    queryKey: ["admin-dashboard"],
    queryFn: fetchAdminDashboard,
    enabled: Boolean(accessToken)
  });

  const data = dashboard.data;

  return (
    <main className="mx-auto grid min-h-screen max-w-7xl gap-6 px-6 py-10 xl:grid-cols-3">
      <Panel title="Sessao admin" subtitle="JWT + tenant header">
        <button className="rounded-full bg-slate-900 px-4 py-2 text-sm font-medium text-white" type="button" onClick={() => loginMutation.mutate()}>
          {accessToken ? "Sessao ativa" : "Entrar como admin seed"}
        </button>
      </Panel>
      <Panel title="GMV" subtitle="Tenant atual">
        <p className="text-4xl font-semibold text-slate-900">R$ {Number(data?.gmv ?? 0).toLocaleString("pt-BR")}</p>
      </Panel>
      <Panel title="Antifraude" subtitle="Pedidos em analise">
        <p className="text-4xl font-semibold text-slate-900">{data?.riskAlerts ?? 0}</p>
      </Panel>
      <Panel title="Vendedores ativos" subtitle="Com score acima de 4.5">
        <p className="text-4xl font-semibold text-slate-900">{data?.stores ?? 0}</p>
      </Panel>
    </main>
  );
}
