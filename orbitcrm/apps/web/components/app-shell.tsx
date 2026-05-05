'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useQuery } from '@tanstack/react-query';
import { Bell, Building2, Search } from 'lucide-react';
import { appNav } from '../lib/demo';
import { cn, initials } from '@orbitcrm/utils';
import { getStoredUser, signOut } from '../lib/auth';
import { getCurrentUser } from '../lib/api';

export function AppShell({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();
  const fallbackUser = getStoredUser() ?? {
    name: 'Orbit Admin',
    email: 'admin@orbitcrm.demo',
    tenantId: 'tenant_demo',
  };
  const { data: currentUser = fallbackUser } = useQuery({
    queryKey: ['current-user'],
    queryFn: getCurrentUser,
    initialData: fallbackUser,
    retry: 0,
  });
  const displayName = currentUser?.name ?? 'Orbit Admin';
  const displayEmail = currentUser?.email ?? 'admin@orbitcrm.demo';
  const tenantLabel = currentUser?.tenantId === 'tenant_demo' ? 'Orbit Demo Industries' : currentUser?.tenantId ? `Tenant ${currentUser.tenantId.slice(0, 8)}` : 'Demo tenant';

  return (
    <div className="min-h-screen bg-slate-50">
      <div className="flex min-h-screen">
        <aside className="hidden w-72 border-r border-slate-200 bg-slate-950 p-5 text-slate-100 lg:block">
          <div className="mb-8 flex items-center gap-3">
            <div className="rounded-2xl bg-sky-500 p-2">
              <Building2 className="h-5 w-5" />
            </div>
            <div>
              <p className="text-sm font-semibold">OrbitCRM</p>
              <p className="text-xs text-slate-400">{tenantLabel}</p>
            </div>
          </div>

          <nav className="space-y-1">
            {appNav.map((item) => {
              const active = pathname === item.href;
              return (
                <Link
                  key={item.href}
                  href={item.href}
                  className={cn(
                    'flex items-center justify-between rounded-xl px-3 py-2 text-sm transition-colors',
                    active ? 'bg-sky-500 text-white' : 'text-slate-300 hover:bg-white/10 hover:text-white',
                  )}
                >
                  <span>{item.label}</span>
                  {item.badge ? <span className="rounded-full bg-white/10 px-2 py-0.5 text-xs">{item.badge}</span> : null}
                </Link>
              );
            })}
          </nav>
        </aside>

        <main className="flex-1">
          <header className="border-b border-slate-200 bg-white px-4 py-4 md:px-6">
            <div className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
              <div className="flex items-center gap-3 rounded-xl border border-slate-200 bg-slate-50 px-3 py-2 md:w-[360px]">
                <Search className="h-4 w-4 text-slate-400" />
                <input className="w-full bg-transparent text-sm outline-none" placeholder="Buscar leads, contas, tickets..." />
              </div>
              <div className="flex items-center gap-3">
                <button className="rounded-xl border border-slate-200 p-2 text-slate-600 hover:bg-slate-50">
                  <Bell className="h-4 w-4" />
                </button>
                <Link href="/profile" className="flex items-center gap-3 rounded-xl border border-slate-200 px-3 py-2">
                  <span className="flex h-8 w-8 items-center justify-center rounded-full bg-sky-100 text-xs font-semibold text-sky-700">
                    {initials(displayName)}
                  </span>
                  <div className="hidden text-left md:block">
                    <p className="text-sm font-medium">{displayName}</p>
                    <p className="text-xs text-slate-500">{displayEmail}</p>
                  </div>
                </Link>
                <button onClick={signOut} className="text-sm text-slate-500 hover:text-slate-900">Sair</button>
              </div>
            </div>
          </header>

          <div className="p-4 md:p-6">{children}</div>
        </main>
      </div>
    </div>
  );
}
