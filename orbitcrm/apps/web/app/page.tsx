import Link from 'next/link';
import { Badge, Button, Card } from '@orbitcrm/ui';
import { PublicShell } from '../components/public-shell';

const modules = [
  'Leads, contas, contatos e oportunidades',
  'Pipeline visual com foco em performance comercial',
  'Tickets, SLA, fila e histórico de atendimento',
  'Automações, alertas e analytics executivos',
];

export default function HomePage() {
  return (
    <PublicShell>
      <main>
        <section className="gradient-orbit">
          <div className="mx-auto grid max-w-7xl gap-10 px-6 py-20 lg:grid-cols-[1.15fr_0.85fr] lg:items-center">
            <div>
              <Badge className="bg-sky-500/20 text-sky-200">CRM SaaS enterprise original</Badge>
              <h1 className="mt-6 text-4xl font-semibold leading-tight md:text-6xl">
                Vendas, atendimento e operação em uma única órbita.
              </h1>
              <p className="mt-5 max-w-2xl text-lg text-slate-300">
                O OrbitCRM centraliza pipeline, tickets, automações e inteligência operacional com arquitetura multi-tenant pronta para evolução comercial real.
              </p>
              <div className="mt-8 flex flex-wrap gap-3">
                <Link href="/signup"><Button className="bg-sky-500 hover:bg-sky-400">Começar teste grátis</Button></Link>
                <Link href="/dashboard"><Button variant="secondary">Ver dashboard demo</Button></Link>
              </div>
              <div className="mt-10 grid gap-3 md:grid-cols-2">
                {modules.map((item) => (
                  <div key={item} className="rounded-2xl border border-white/10 bg-white/5 px-4 py-3 text-sm text-slate-100">
                    {item}
                  </div>
                ))}
              </div>
            </div>

            <Card className="border-white/10 bg-slate-900/70 text-slate-100 shadow-soft">
              <div className="mb-6 flex items-center justify-between">
                <div>
                  <p className="text-sm text-slate-400">Pipeline Orbit</p>
                  <h2 className="text-xl font-semibold">R$ 1,28M em negociação</h2>
                </div>
                <Badge className="bg-emerald-500/20 text-emerald-300">+18% QoQ</Badge>
              </div>
              <div className="space-y-3">
                {[
                  ['Qualification', '6 deals'],
                  ['Proposal', '9 deals'],
                  ['Negotiation', '4 deals'],
                  ['Won', '12 deals'],
                ].map(([label, info]) => (
                  <div key={label} className="rounded-xl border border-white/10 bg-white/5 p-3">
                    <div className="flex items-center justify-between text-sm">
                      <span>{label}</span>
                      <span className="text-slate-300">{info}</span>
                    </div>
                  </div>
                ))}
              </div>
            </Card>
          </div>
        </section>

        <section className="mx-auto max-w-7xl px-6 py-16">
          <div className="grid gap-6 md:grid-cols-3">
            {[
              ['Multi-tenant by design', 'Isolamento por tenant, planos, branding e permissões desde a base.'],
              ['Execução comercial', 'Pipeline, metas, agenda, produtividade e automações práticas para B2B.'],
              ['Operação confiável', 'NestJS, Prisma, PostgreSQL, Redis, BullMQ, Docker e observabilidade pronta.'],
            ].map(([title, text]) => (
              <Card key={title}>
                <h3 className="text-lg font-semibold text-slate-950">{title}</h3>
                <p className="mt-2 text-sm text-slate-500">{text}</p>
              </Card>
            ))}
          </div>
        </section>

        <section className="mx-auto max-w-7xl px-6 pb-16">
          <div className="grid gap-6 lg:grid-cols-[1fr_0.9fr]">
            <Card>
              <h2 className="text-2xl font-semibold text-slate-950">FAQ</h2>
              <div className="mt-4 space-y-4 text-sm text-slate-600">
                <div>
                  <p className="font-medium text-slate-900">O OrbitCRM suporta múltiplas empresas?</p>
                  <p>Sim. O núcleo foi estruturado para multi-tenant com isolamento por `tenant_id`.</p>
                </div>
                <div>
                  <p className="font-medium text-slate-900">É possível evoluir para billing real?</p>
                  <p>Sim. O módulo de billing já está preparado para Stripe Checkout e webhooks.</p>
                </div>
                <div>
                  <p className="font-medium text-slate-900">Há base para atendimento e operação?</p>
                  <p>Sim. Tickets, SLA, automações, relatórios e auditoria já fazem parte do starter.</p>
                </div>
              </div>
            </Card>
            <Card className="gradient-orbit border-0 text-white">
              <p className="text-sm text-sky-200">Pronto para explorar?</p>
              <h2 className="mt-2 text-3xl font-semibold">Abra a demo e conheça a experiência OrbitCRM.</h2>
              <p className="mt-3 text-sm text-slate-200">Use o workspace demo para navegar entre dashboard, pipeline, tickets e módulos administrativos.</p>
              <div className="mt-6 flex gap-3">
                <Link href="/login"><Button className="bg-sky-500 hover:bg-sky-400">Acessar demo</Button></Link>
                <Link href="/pricing"><Button variant="secondary">Ver planos</Button></Link>
              </div>
            </Card>
          </div>
        </section>
      </main>
    </PublicShell>
  );
}
