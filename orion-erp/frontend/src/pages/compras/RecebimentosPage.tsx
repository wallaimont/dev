import PageHeader from '@/components/PageHeader';

export default function RecebimentosPage() {
  return (
    <div>
      <PageHeader title="Recebimentos" subtitle="Recebimentos de mercadorias vinculados a pedidos de compra" />
      <div className="rounded-lg border bg-white p-8 text-center text-gray-400">
        Módulo de recebimentos — disponível via integração com Pedidos de Compra.
      </div>
    </div>
  );
}
