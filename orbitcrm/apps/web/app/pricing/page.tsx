import { Badge, Button, Card } from '@orbitcrm/ui';
import { PublicShell } from '../../components/public-shell';

const plans = [
  { name: 'Starter', price: 'R$ 149', info: 'até 3 usuários', features: ['Leads', 'Contatos', 'Oportunidades', 'Dashboard básico'] },
  { name: 'Growth', price: 'R$ 499', info: 'usuários ilimitados', features: ['Automações', 'Relatórios avançados', 'Tickets', 'API'] },
  { name: 'Enterprise', price: 'Sob consulta', info: 'controle máximo', features: ['Auditoria completa', 'Branding ampliado', 'SSO ready', 'Gestão avançada'] },
];

export default function PricingPage() {
  return (
    <PublicShell>
      <main className="mx-auto max-w-7xl px-6 py-16">
        <div className="text-center">
          <Badge className="bg-sky-500/20 text-sky-200">Planos prontos para Stripe</Badge>
          <h1 className="mt-4 text-4xl font-semibold">Escala conforme a maturidade do seu time</h1>
        </div>
        <div className="mt-10 grid gap-6 md:grid-cols-3">
          {plans.map((plan) => (
            <Card key={plan.name} className="border-white/10 bg-slate-900 text-slate-100">
              <h2 className="text-2xl font-semibold">{plan.name}</h2>
              <p className="mt-2 text-3xl font-bold">{plan.price}</p>
              <p className="mt-1 text-sm text-slate-400">{plan.info}</p>
              <ul className="mt-5 space-y-2 text-sm text-slate-300">
                {plan.features.map((feature) => <li key={feature}>• {feature}</li>)}
              </ul>
              <Button className="mt-6 w-full bg-sky-500 hover:bg-sky-400">Escolher plano</Button>
            </Card>
          ))}
        </div>
      </main>
    </PublicShell>
  );
}
