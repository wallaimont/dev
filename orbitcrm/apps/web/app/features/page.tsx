import { Card } from '@orbitcrm/ui';
import { PublicShell } from '../../components/public-shell';

const featureGroups = {
  Comercial: ['Leads', 'Contas', 'Contatos', 'Oportunidades', 'Pipeline kanban'],
  Atendimento: ['Tickets', 'Filas', 'SLA', 'Histórico', 'Comentários internos'],
  Gestão: ['Dashboard', 'Relatórios', 'Auditoria', 'Billing', 'Permissões'],
  Plataforma: ['Multi-tenant', 'API REST', 'Redis + filas', 'Upload S3', 'Swagger'],
};

export default function FeaturesPage() {
  return (
    <PublicShell>
      <main className="mx-auto max-w-7xl px-6 py-16">
        <div className="max-w-2xl">
          <h1 className="text-4xl font-semibold">Funcionalidades pensadas para crescimento real</h1>
          <p className="mt-4 text-slate-300">
            OrbitCRM entrega uma base SaaS original para vendas, atendimento, operação e gestão com foco em times B2B.
          </p>
        </div>

        <div className="mt-10 grid gap-6 md:grid-cols-2">
          {Object.entries(featureGroups).map(([group, items]) => (
            <Card key={group} className="border-white/10 bg-slate-900 text-slate-100">
              <h2 className="text-xl font-semibold">{group}</h2>
              <ul className="mt-4 space-y-2 text-sm text-slate-300">
                {items.map((item) => <li key={item}>• {item}</li>)}
              </ul>
            </Card>
          ))}
        </div>
      </main>
    </PublicShell>
  );
}
