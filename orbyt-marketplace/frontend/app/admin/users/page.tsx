"use client";

import { Panel } from "@/components/ui/panel";
import { fetchAdminDashboard } from "@/lib/api";
import { useAuthStore } from "@/store/auth-store";
import { useQuery } from "@tanstack/react-query";

export default function AdminUsersPage() {
  const { accessToken } = useAuthStore();

  const { data } = useQuery({
    queryKey: ["admin-dashboard"],
    queryFn: fetchAdminDashboard,
    enabled: Boolean(accessToken),
  });

  const mockUsers = [
    { id: "1", name: "Admin Orbyt", email: "admin@orbyt.local", role: "ADMIN", status: "ACTIVE" },
    { id: "2", name: "Maria Vendedora", email: "maria@seller.com", role: "SELLER", status: "ACTIVE" },
    { id: "3", name: "João Comprador", email: "joao@buyer.com", role: "BUYER", status: "ACTIVE" },
    { id: "4", name: "Ana Moderadora", email: "ana@admin.com", role: "ADMIN", status: "ACTIVE" },
    { id: "5", name: "Carlos Vendedor", email: "carlos@seller.com", role: "SELLER", status: "SUSPENDED" },
  ];

  const roleColors: Record<string, string> = {
    ADMIN: "text-purple-600 bg-purple-50",
    SELLER: "text-blue-600 bg-blue-50",
    BUYER: "text-emerald-600 bg-emerald-50",
  };

  return (
    <main className="mx-auto min-h-screen max-w-6xl px-6 py-10">
      <div className="mb-8 flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-slate-900">Gestão de Usuários</h1>
          <p className="text-sm text-slate-500">{data?.users ?? 0} usuários no tenant</p>
        </div>
        <button className="rounded-full bg-amber-400 px-5 py-2.5 text-sm font-semibold text-slate-900">
          + Novo usuário
        </button>
      </div>

      <div className="mb-6 grid gap-5 sm:grid-cols-3">
        <Panel title="Total" subtitle="Todos os papéis">
          <p className="text-3xl font-semibold text-slate-900">{data?.users ?? mockUsers.length}</p>
        </Panel>
        <Panel title="Vendedores" subtitle="Contas seller ativas">
          <p className="text-3xl font-semibold text-blue-600">
            {mockUsers.filter((u) => u.role === "SELLER" && u.status === "ACTIVE").length}
          </p>
        </Panel>
        <Panel title="Admins" subtitle="Acesso administrativo">
          <p className="text-3xl font-semibold text-purple-600">
            {mockUsers.filter((u) => u.role === "ADMIN").length}
          </p>
        </Panel>
      </div>

      <div className="overflow-x-auto rounded-2xl border border-slate-100 bg-white">
        <table className="w-full text-left text-sm">
          <thead>
            <tr className="border-b border-slate-100 text-slate-500">
              <th className="px-4 py-3 font-medium">Nome</th>
              <th className="px-4 py-3 font-medium">Email</th>
              <th className="px-4 py-3 font-medium">Papel</th>
              <th className="px-4 py-3 font-medium">Status</th>
              <th className="px-4 py-3 font-medium">Ações</th>
            </tr>
          </thead>
          <tbody>
            {mockUsers.map((u) => (
              <tr key={u.id} className="border-b border-slate-50 last:border-0">
                <td className="px-4 py-3 font-medium text-slate-900">{u.name}</td>
                <td className="px-4 py-3 text-slate-600">{u.email}</td>
                <td className="px-4 py-3">
                  <span className={`rounded-full px-2.5 py-1 text-xs font-semibold ${roleColors[u.role] ?? ""}`}>
                    {u.role}
                  </span>
                </td>
                <td className="px-4 py-3">
                  <span className={`text-xs font-semibold ${u.status === "ACTIVE" ? "text-emerald-600" : "text-red-500"}`}>
                    {u.status}
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
    </main>
  );
}
