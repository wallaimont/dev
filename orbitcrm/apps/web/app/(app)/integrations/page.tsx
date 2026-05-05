import { Badge, Button, Card, StatCard } from '@orbitcrm/ui';

const integrations = [
  {
    id: 'email',
    name: 'Resend / SendGrid',
    category: 'Mensageria',
    status: 'pronto para configurar',
    description: 'Entrega de e-mails transacionais para onboarding, cobrança e alertas.',
    envKey: 'RESEND_API_KEY / SENDGRID_API_KEY',
  },
  {
    id: 'billing',
    name: 'Stripe',
    category: 'Billing',
    status: 'pronto para configurar',
    description: 'Assinaturas, checkout e gestão de plano Growth / Enterprise.',
    envKey: 'STRIPE_SECRET_KEY',
  },
  {
    id: 'storage',
    name: 'S3 / MinIO',
    category: 'Storage',
    status: 'ativo no ambiente local',
    description: 'Upload de anexos e documentos com compatibilidade S3.',
    envKey: 'S3_ENDPOINT / S3_BUCKET',
  },
  {
    id: 'webhooks',
    name: 'Webhooks externos',
    category: 'Automação',
    status: 'em expansão',
    description: 'Recebimento e despacho de eventos para ERPs, CS e parceiros.',
    envKey: 'WEBHOOK_SECRET',
  },
];

export default function IntegrationsPage() {
  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-2 md:flex-row md:items-end md:justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-slate-950">Integrações</h1>
          <p className="text-sm text-slate-500">Hub para e-mail, billing, storage e webhooks corporativos.</p>
        </div>
        <Button>Gerar token de API</Button>
      </div>

      <div className="grid gap-4 md:grid-cols-3">
        <StatCard label="Conectores mapeados" value="4" helper="Prontos para operação enterprise" />
        <StatCard label="Provisionados" value="1" helper="Ativo no ambiente local" />
        <StatCard label="Próxima onda" value="Webhooks" helper="Expansão recomendada" />
      </div>

      <div className="grid gap-4 lg:grid-cols-2">
        {integrations.map((item) => (
          <Card key={item.id}>
            <div className="flex items-start justify-between gap-3">
              <div>
                <p className="text-xs font-semibold uppercase tracking-wide text-sky-700">{item.category}</p>
                <h2 className="mt-1 text-lg font-semibold text-slate-950">{item.name}</h2>
              </div>
              <Badge>{item.status}</Badge>
            </div>
            <p className="mt-3 text-sm text-slate-600">{item.description}</p>
            <div className="mt-4 rounded-xl border border-dashed border-slate-200 bg-slate-50 px-3 py-2 text-xs text-slate-500">
              Variáveis esperadas: <span className="font-medium text-slate-700">{item.envKey}</span>
            </div>
          </Card>
        ))}
      </div>
    </div>
  );
}
