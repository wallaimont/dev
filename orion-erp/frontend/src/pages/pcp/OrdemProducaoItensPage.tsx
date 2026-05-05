import CrudPage from '@/pages/CrudPage';
import { InputField } from '@/components/FormField';
import type { OrdemProducaoItem, Column } from '@/types';

const columns: Column<OrdemProducaoItem>[] = [
  { key: 'id', label: 'ID' },
  { key: 'ordemProducaoId', label: 'Ordem Produção ID' },
  { key: 'produtoId', label: 'Produto ID' },
  { key: 'qtdPrevista', label: 'Qtd Prevista' },
  { key: 'qtdUtilizada', label: 'Qtd Utilizada' },
  { key: 'custoUnitario', label: 'Custo Unitário' },
];

export default function OrdemProducaoItensPage() {
  return (
    <CrudPage<OrdemProducaoItem>
      title="Itens de Ordem de Produção"
      labelSingular="Item de Ordem de Produção"
      endpoint="/pcp/ordem-producao-itens"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Ordem Produção ID" type="number" value={item.ordemProducaoId} onChange={(v) => set({ ...item, ordemProducaoId: v })} />
          <InputField label="Produto ID" type="number" value={item.produtoId} onChange={(v) => set({ ...item, produtoId: v })} />
          <InputField label="Qtd Prevista" type="number" value={item.qtdPrevista} onChange={(v) => set({ ...item, qtdPrevista: v })} />
          <InputField label="Qtd Utilizada" type="number" value={item.qtdUtilizada} onChange={(v) => set({ ...item, qtdUtilizada: v })} />
          <InputField label="Unidade Medida" value={item.unidadeMedida} onChange={(v) => set({ ...item, unidadeMedida: v })} />
          <InputField label="Custo Unitário" type="number" value={item.custoUnitario} onChange={(v) => set({ ...item, custoUnitario: v })} />
        </div>
      )}
    />
  );
}
