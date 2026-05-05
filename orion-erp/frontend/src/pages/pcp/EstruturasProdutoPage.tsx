import CrudPage from '@/pages/CrudPage';
import { InputField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { EstruturaProduto, Column } from '@/types';

const columns: Column<EstruturaProduto>[] = [
  { key: 'id', label: 'ID' },
  { key: 'produtoPaiId', label: 'Produto Pai ID' },
  { key: 'produtoFilhoId', label: 'Produto Filho ID' },
  { key: 'quantidade', label: 'Quantidade' },
  { key: 'perdaPercentual', label: 'Perda %' },
  { key: 'ativo', label: 'Status', render: (v) => <StatusBadge value={v} /> },
];

export default function EstruturasProdutoPage() {
  return (
    <CrudPage<EstruturaProduto>
      title="Estruturas de Produto"
      labelSingular="Estrutura de Produto"
      endpoint="/pcp/estruturas-produto"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Produto Pai ID" type="number" value={item.produtoPaiId} onChange={(v) => set({ ...item, produtoPaiId: v })} />
          <InputField label="Produto Filho ID" type="number" value={item.produtoFilhoId} onChange={(v) => set({ ...item, produtoFilhoId: v })} />
          <InputField label="Quantidade" type="number" value={item.quantidade} onChange={(v) => set({ ...item, quantidade: v })} />
          <InputField label="Unidade Medida" value={item.unidadeMedida} onChange={(v) => set({ ...item, unidadeMedida: v })} />
          <InputField label="Perda Percentual" type="number" value={item.perdaPercentual} onChange={(v) => set({ ...item, perdaPercentual: v })} />
          <InputField label="Observação" value={item.observacao} onChange={(v) => set({ ...item, observacao: v })} />
        </div>
      )}
    />
  );
}
