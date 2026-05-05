'use client';

import { useState } from 'react';
import { useMutation, useQuery } from '@tanstack/react-query';
import { Badge, Button, Card, StatCard } from '@orbitcrm/ui';
import { formatCurrency } from '@orbitcrm/utils';
import { billingPlans as initialPlans, currentSubscription as initialSubscription } from '../../../lib/demo';
import { getBillingPlans, getCurrentSubscription, prepareCheckoutSession } from '../../../lib/api';

export default function BillingPage() {
  const { data: plans = initialPlans, isFetching } = useQuery({
    queryKey: ['billing-plans'],
    queryFn: getBillingPlans,
    initialData: initialPlans,
    retry: 0,
  });

  const { data: subscription = initialSubscription } = useQuery({
    queryKey: ['billing-subscription'],
    queryFn: getCurrentSubscription,
    initialData: initialSubscription,
    retry: 0,
  });

  const [statusMessage, setStatusMessage] = useState('');

  const checkoutMutation = useMutation({
    mutationFn: prepareCheckoutSession,
    onSuccess: (result, planCode) => {
      setStatusMessage(`${planCode}: ${result.message}`);
    },
  });

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-2 md:flex-row md:items-end md:justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-slate-950">Cobrança e assinatura</h1>
          <p className="text-sm text-slate-500">Arquitetura pronta para Stripe Checkout, portal do cliente e webhooks.</p>
        </div>
        <p className="text-xs text-slate-500">{isFetching ? 'Sincronizando billing...' : 'Billing conectado com fallback demo.'}</p>
      </div>

      <div className="grid gap-4 md:grid-cols-3">
        <StatCard label="Plano atual" value={subscription.planName} helper={`status: ${subscription.status}`} />
        <StatCard label="Renovação" value={subscription.currentPeriodEnd} helper="fim do ciclo vigente" />
        <StatCard label="Customer ID" value={subscription.stripeCustomerId} helper="referência Stripe-ready" />
      </div>

      <Card>
        <div className="flex items-start justify-between gap-3">
          <div>
            <p className="text-sm font-semibold text-slate-900">Resumo da assinatura</p>
            <p className="mt-1 text-sm text-slate-500">Use este fluxo para preparar upgrade, downgrade e portal do cliente.</p>
          </div>
          <Badge>{subscription.status}</Badge>
        </div>
        <p className="mt-3 text-sm text-slate-600">{statusMessage || 'Selecione um plano para preparar a próxima sessão de checkout.'}</p>
      </Card>

      <div className="grid gap-4 md:grid-cols-3">
        {plans.map((plan) => (
          <Card key={plan.code} className={plan.code === subscription.planCode ? 'border-sky-500' : ''}>
            <div className="flex items-center justify-between">
              <h2 className="text-lg font-semibold text-slate-950">{plan.name}</h2>
              <Badge>{plan.code === subscription.planCode ? 'Atual' : 'Stripe-ready'}</Badge>
            </div>
            <p className="mt-3 text-2xl font-semibold text-slate-950">
              {plan.priceCents > 0 ? formatCurrency(plan.priceCents / 100) : 'Sob consulta'}
            </p>
            <p className="mt-1 text-xs text-slate-500">
              {plan.userLimit ? `Até ${plan.userLimit} usuários` : 'Usuários ilimitados'}
            </p>
            <ul className="mt-4 space-y-2 text-sm text-slate-600">
              {plan.features.map((feature) => (
                <li key={feature} className="rounded-lg bg-slate-50 px-3 py-2">{feature}</li>
              ))}
            </ul>
            <Button
              className="mt-4 w-full"
              variant={plan.code === subscription.planCode ? 'secondary' : 'primary'}
              onClick={() => checkoutMutation.mutate(plan.code)}
              disabled={checkoutMutation.isPending}
            >
              {checkoutMutation.isPending ? 'Preparando...' : 'Gerenciar plano'}
            </Button>
          </Card>
        ))}
      </div>
    </div>
  );
}
