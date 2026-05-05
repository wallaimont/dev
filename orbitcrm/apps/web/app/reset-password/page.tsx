import { Button, Card, Input } from '@orbitcrm/ui';
import { PublicShell } from '../../components/public-shell';

export default function ResetPasswordPage() {
  return (
    <PublicShell>
      <main className="mx-auto flex min-h-[80vh] max-w-7xl items-center justify-center px-6 py-12">
        <Card className="w-full max-w-md">
          <h1 className="text-2xl font-semibold text-slate-950">Redefinir senha</h1>
          <div className="mt-6 space-y-4">
            <Input type="password" placeholder="Nova senha" />
            <Input type="password" placeholder="Confirmar senha" />
            <Button className="w-full">Salvar nova senha</Button>
          </div>
        </Card>
      </main>
    </PublicShell>
  );
}
