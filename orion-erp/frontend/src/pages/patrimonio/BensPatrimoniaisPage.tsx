import CrudPage from '@/pages/CrudPage';
import { InputField, SelectField } from '@/components/FormField';
import StatusBadge from '@/components/StatusBadge';
import type { BemPatrimonial, Column } from '@/types';

const columns: Column<BemPatrimonial>[] = [
  { key: 'id', label: 'ID' },
  { key: 'codigo', label: 'Código' },
  { key: 'descricao', label: 'Descrição' },
  { key: 'numeroPatrimonio', label: 'Nº Patrimônio' },
  { key: 'dataAquisicao', label: 'Data Aquisição' },
  { key: 'valorAquisicao', label: 'Valor Aquisição' },
  { key: 'status', label: 'Status', render: (v) => <StatusBadge value={v} /> },
];

export default function BensPatrimoniaisPage() {
  return (
    <CrudPage<BemPatrimonial>
      title="Bens Patrimoniais"
      labelSingular="Bem Patrimonial"
      endpoint="/patrimonio/bens"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-3 gap-4">
          <InputField label="Código" value={item.codigo} onChange={(v) => set({ ...item, codigo: v })} />
          <InputField label="Descrição" value={item.descricao} onChange={(v) => set({ ...item, descricao: v })} />
          <InputField label="Número Patrimônio" value={item.numeroPatrimonio} onChange={(v) => set({ ...item, numeroPatrimonio: v })} />
          <InputField label="Data Aquisição" type="date" value={item.dataAquisicao} onChange={(v) => set({ ...item, dataAquisicao: v })} />
          <InputField label="Valor Aquisição" type="number" value={item.valorAquisicao} onChange={(v) => set({ ...item, valorAquisicao: v })} />
          <InputField label="Valor Residual" type="number" value={item.valorResidual} onChange={(v) => set({ ...item, valorResidual: v })} />
          <InputField label="Vida Útil Meses" type="number" value={item.vidaUtilMeses} onChange={(v) => set({ ...item, vidaUtilMeses: v })} />
          <InputField label="Taxa Depreciação" type="number" value={item.taxaDepreciacao} onChange={(v) => set({ ...item, taxaDepreciacao: v })} />
          <InputField label="Grupo" value={item.grupo} onChange={(v) => set({ ...item, grupo: v })} />
          <InputField label="Localização" value={item.localizacao} onChange={(v) => set({ ...item, localizacao: v })} />
          <InputField label="Centro Custo ID" type="number" value={item.centroCustoId} onChange={(v) => set({ ...item, centroCustoId: v })} />
          <InputField label="Fornecedor ID" type="number" value={item.fornecedorId} onChange={(v) => set({ ...item, fornecedorId: v })} />
          <SelectField label="Status" value={item.status} onChange={(v) => set({ ...item, status: v })} options={['EM_USO', 'BAIXADO', 'EM_MANUTENCAO']} />
        </div>
      )}
    />
  );
}
