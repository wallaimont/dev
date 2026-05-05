import { Badge, Button, Card, EmptyState } from '@orbitcrm/ui';

export function ModulePage({
  title,
  description,
  items,
}: {
  title: string;
  description: string;
  items?: string[];
}) {
  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-slate-950">{title}</h1>
          <p className="text-sm text-slate-500">{description}</p>
        </div>
        <Button>Novo registro</Button>
      </div>

      <Card>
        <div className="mb-4 flex items-center gap-2">
          <Badge>Enterprise-ready</Badge>
          <Badge className="bg-sky-100 text-sky-700">Multi-tenant</Badge>
        </div>
        <ul className="grid gap-3 md:grid-cols-2">
          {(items ?? []).map((item) => (
            <li key={item} className="rounded-xl border border-slate-100 bg-slate-50 px-4 py-3 text-sm text-slate-700">
              {item}
            </li>
          ))}
        </ul>
      </Card>

      <EmptyState
        title="Fluxo inicial pronto para evolução"
        description="Este módulo já possui rota, layout, área segura, identidade visual e pontos de extensão para conectar CRUD real e integrações futuras."
      />
    </div>
  );
}
