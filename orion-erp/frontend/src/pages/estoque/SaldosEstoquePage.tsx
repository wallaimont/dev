import CrudPage from '@/pages/CrudPage';
import type { SaldoEstoque, Column } from '@/types';

const columns: Column<SaldoEstoque>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'produtoNome', label: 'Produto' },
  { key: 'armazemNome', label: 'Armazém' },
  { key: 'quantidade', label: 'Quantidade' },
  { key: 'custoMedio', label: 'Custo Médio' },
  { key: 'reservado', label: 'Reservado' },
  { key: 'disponivel', label: 'Disponível' },
];

export default function SaldosEstoquePage() {
  return (
    <CrudPage<SaldoEstoque>
      title="Saldos de Estoque"
      labelSingular="Saldo"
      endpoint="/estoque/saldos"
      columns={columns}
      canDelete={false}
      renderForm={() => (
        <div className="p-4 text-sm text-gray-500">
          Saldos são calculados automaticamente.
        </div>
      )}
    />
  );
}
