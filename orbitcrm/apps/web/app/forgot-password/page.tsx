import { Button, Card, Input } from '@orbitcrm/ui';
import { PublicShell } from '../../components/public-shell';

export default function ForgotPasswordPage() {
  return (
    <PublicShell>
      <main className="mx-auto flex min-h-[80vh] max-w-7xl items-center justify-center px-6 py-12">
        <Card className="w-full max-w-md">
          <h1 className="text-2xl font-semibold text-slate-950">Recuperar senha</h1>
          <p className="mt-2 text-sm text-slate-500">Fluxo preparado para envio por Resend ou SendGrid.</p>
          <div className="mt-6 space-y-4">
            <Input placeholder="seu@email.com" />
            <Button className="w-full">Enviar link de recuperação</Button>
          </div>
        </Card>
      </main>
    </PublicShell>
  );
}
