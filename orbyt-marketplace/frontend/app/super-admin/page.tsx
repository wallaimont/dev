import { Panel } from "@/components/ui/panel";

export default function SuperAdminPage() {
  return (
    <main className="mx-auto grid min-h-screen max-w-7xl gap-6 px-6 py-10 lg:grid-cols-2">
      <Panel title="Tenants gerenciados" subtitle="Visao global da plataforma">
        <p className="text-5xl font-semibold text-slate-900">28</p>
      </Panel>
      <Panel title="Receita da plataforma" subtitle="Billing, comissao e planos">
        <p className="text-5xl font-semibold text-slate-900">R$ 6,3M</p>
      </Panel>
    </main>
  );
}
