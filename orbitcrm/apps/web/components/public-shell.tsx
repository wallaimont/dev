import Link from 'next/link';
import { Button } from '@orbitcrm/ui';
import { publicNav } from '../lib/demo';

export function PublicShell({ children }: { children: React.ReactNode }) {
  return (
    <div className="min-h-screen bg-slate-950 text-white">
      <header className="sticky top-0 z-30 border-b border-white/10 bg-slate-950/80 backdrop-blur">
        <div className="mx-auto flex max-w-7xl items-center justify-between px-6 py-4">
          <Link href="/" className="flex items-center gap-2 text-lg font-semibold">
            <span className="rounded-xl bg-sky-500 px-2 py-1">Orbit</span>CRM
          </Link>
          <nav className="hidden items-center gap-6 md:flex">
            {publicNav.map((item) => (
              <Link key={item.href} href={item.href} className="text-sm text-slate-300 hover:text-white">
                {item.label}
              </Link>
            ))}
          </nav>
          <div className="flex items-center gap-3">
            <Link href="/login">
              <Button variant="ghost" className="text-white hover:bg-white/10">Entrar</Button>
            </Link>
            <Link href="/signup">
              <Button className="bg-sky-500 hover:bg-sky-400">Teste grátis</Button>
            </Link>
          </div>
        </div>
      </header>
      {children}
    </div>
  );
}
