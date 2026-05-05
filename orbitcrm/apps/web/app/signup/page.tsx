'use client';

import Link from 'next/link';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { Button, Card, Input } from '@orbitcrm/ui';
import { PublicShell } from '../../components/public-shell';

const schema = z.object({
  company: z.string().min(2, 'Informe a empresa'),
  name: z.string().min(2, 'Informe seu nome'),
  email: z.string().email('E-mail inválido'),
  password: z.string().min(8, 'Use ao menos 8 caracteres'),
});

type FormValues = z.infer<typeof schema>;

export default function SignupPage() {
  const { register, handleSubmit, formState: { errors, isSubmitSuccessful } } = useForm<FormValues>({
    resolver: zodResolver(schema),
  });

  return (
    <PublicShell>
      <main className="mx-auto flex min-h-[80vh] max-w-7xl items-center justify-center px-6 py-12">
        <Card className="w-full max-w-lg">
          <h1 className="text-2xl font-semibold text-slate-950">Criar workspace OrbitCRM</h1>
          <p className="mt-2 text-sm text-slate-500">Fluxo pronto para criação do tenant e usuário admin.</p>
          <form onSubmit={handleSubmit(() => undefined)} className="mt-6 grid gap-4 md:grid-cols-2">
            <div className="md:col-span-2">
              <label className="mb-1 block text-sm font-medium">Empresa</label>
              <Input {...register('company')} />
              {errors.company ? <p className="mt-1 text-xs text-rose-500">{errors.company.message}</p> : null}
            </div>
            <div>
              <label className="mb-1 block text-sm font-medium">Nome</label>
              <Input {...register('name')} />
            </div>
            <div>
              <label className="mb-1 block text-sm font-medium">E-mail</label>
              <Input {...register('email')} />
            </div>
            <div className="md:col-span-2">
              <label className="mb-1 block text-sm font-medium">Senha</label>
              <Input type="password" {...register('password')} />
            </div>
            <div className="md:col-span-2">
              <Button className="w-full">Criar conta</Button>
            </div>
          </form>
          {isSubmitSuccessful ? <p className="mt-4 text-sm text-emerald-600">Conta preparada com sucesso.</p> : null}
          <p className="mt-4 text-sm text-slate-500">Já possui acesso? <Link href="/login" className="text-sky-600">Entrar</Link></p>
        </Card>
      </main>
    </PublicShell>
  );
}
