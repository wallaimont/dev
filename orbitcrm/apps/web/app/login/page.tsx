'use client';

import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import Link from 'next/link';
import { Button, Card, Input } from '@orbitcrm/ui';
import { PublicShell } from '../../components/public-shell';
import { signIn } from '../../lib/auth';

const schema = z.object({
  email: z.string().email('Informe um e-mail válido'),
  password: z.string().min(6, 'Mínimo de 6 caracteres'),
});

type FormValues = z.infer<typeof schema>;

export default function LoginPage() {
  const [error, setError] = useState<string | null>(null);
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { email: 'admin@orbitcrm.demo', password: 'Admin@123' },
  });

  const onSubmit = handleSubmit(async (values) => {
    setError(null);
    try {
      await signIn(values.email, values.password);
      window.location.href = '/dashboard';
    } catch {
      setError('Não foi possível autenticar. Use as credenciais demo ou suba a API local.');
    }
  });

  return (
    <PublicShell>
      <main className="mx-auto flex min-h-[80vh] max-w-7xl items-center justify-center px-6 py-12">
        <Card className="w-full max-w-md">
          <h1 className="text-2xl font-semibold text-slate-950">Entrar no OrbitCRM</h1>
          <p className="mt-2 text-sm text-slate-500">Demo: admin@orbitcrm.demo / Admin@123</p>
          <form onSubmit={onSubmit} method="post" noValidate className="mt-6 space-y-4">
            <div>
              <label className="mb-1 block text-sm font-medium">E-mail</label>
              <Input autoComplete="email" placeholder="admin@orbitcrm.demo" {...register('email')} />
              {errors.email ? <p className="mt-1 text-xs text-rose-500">{errors.email.message}</p> : null}
            </div>
            <div>
              <label className="mb-1 block text-sm font-medium">Senha</label>
              <Input type="password" autoComplete="current-password" placeholder="••••••••" {...register('password')} />
              {errors.password ? <p className="mt-1 text-xs text-rose-500">{errors.password.message}</p> : null}
            </div>
            {error ? <p className="text-sm text-rose-500">{error}</p> : null}
            <Button type="submit" className="w-full" disabled={isSubmitting}>{isSubmitting ? 'Entrando...' : 'Entrar'}</Button>
          </form>
          <div className="mt-4 flex justify-between text-sm text-slate-500">
            <Link href="/forgot-password">Esqueci minha senha</Link>
            <Link href="/signup">Criar conta</Link>
          </div>
        </Card>
      </main>
    </PublicShell>
  );
}
