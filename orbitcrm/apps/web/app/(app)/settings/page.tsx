'use client';

import { useEffect, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Badge, Button, Card, Input, StatCard } from '@orbitcrm/ui';
import { tenantProfile as initialTenantProfile } from '../../../lib/demo';
import { getCurrentTenant, updateCurrentTenant } from '../../../lib/api';

export default function SettingsPage() {
  const queryClient = useQueryClient();
  const { data: tenant = initialTenantProfile, isFetching } = useQuery({
    queryKey: ['tenant-profile'],
    queryFn: getCurrentTenant,
    initialData: initialTenantProfile,
    retry: 0,
  });

  const [name, setName] = useState(tenant.name);
  const [slug, setSlug] = useState(tenant.slug);
  const [primaryColor, setPrimaryColor] = useState(tenant.primaryColor);
  const [timezone, setTimezone] = useState(tenant.timezone);
  const [locale, setLocale] = useState(tenant.locale);
  const [savedMessage, setSavedMessage] = useState('');

  useEffect(() => {
    setName(tenant.name);
    setSlug(tenant.slug);
    setPrimaryColor(tenant.primaryColor);
    setTimezone(tenant.timezone);
    setLocale(tenant.locale);
  }, [tenant]);

  const updateMutation = useMutation({
    mutationFn: updateCurrentTenant,
    onSuccess: (updated) => {
      queryClient.setQueryData(['tenant-profile'], updated);
      setSavedMessage('Configurações salvas com sucesso.');
    },
  });

  function handleSave() {
    updateMutation.mutate({
      name,
      slug,
      primaryColor,
      timezone,
      locale,
      planName: tenant.planName,
    });
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-2 md:flex-row md:items-end md:justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-slate-950">Configurações do tenant</h1>
          <p className="text-sm text-slate-500">Branding, preferências e parâmetros organizacionais prontos para white-label.</p>
        </div>
        <p className="text-xs text-slate-500">{isFetching ? 'Sincronizando preferências...' : 'Tenant conectado com fallback demo.'}</p>
      </div>

      <div className="grid gap-4 md:grid-cols-3">
        <StatCard label="Plano ativo" value={tenant.planName} helper="escopo atual do workspace" />
        <StatCard label="Timezone" value={timezone} helper="operações e relatórios" />
        <StatCard label="Locale" value={locale} helper="formatação do produto" />
      </div>

      <Card>
        <div className="mb-4 flex items-center gap-2">
          <Badge>White-label ready</Badge>
          <Badge className="bg-sky-100 text-sky-700">Tenant settings</Badge>
        </div>
        <div className="grid gap-3 md:grid-cols-2">
          <Input placeholder="Nome do tenant" value={name} onChange={(e) => setName(e.target.value)} />
          <Input placeholder="Slug" value={slug} onChange={(e) => setSlug(e.target.value)} />
          <Input placeholder="Cor principal" value={primaryColor} onChange={(e) => setPrimaryColor(e.target.value)} />
          <Input placeholder="Timezone" value={timezone} onChange={(e) => setTimezone(e.target.value)} />
          <Input placeholder="Locale" value={locale} onChange={(e) => setLocale(e.target.value)} />
          <div className="flex items-center rounded-xl border border-slate-200 bg-slate-50 px-3 text-sm text-slate-700">
            Plano atual: <span className="ml-2 font-semibold">{tenant.planName}</span>
          </div>
        </div>
        <div className="mt-4 flex items-center justify-between gap-3">
          <div className="text-sm text-slate-500">
            {savedMessage || 'Ajuste branding, locale e parâmetros centrais do tenant.'}
          </div>
          <Button onClick={handleSave} disabled={updateMutation.isPending}>
            {updateMutation.isPending ? 'Salvando...' : 'Salvar configurações'}
          </Button>
        </div>
      </Card>
    </div>
  );
}
