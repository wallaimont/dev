import CrudPage from '@/pages/CrudPage';
import { InputField } from '@/components/FormField';
import type { Depreciacao, Column } from '@/types';

const columns: Column<Depreciacao>[] = [
  { key: 'id', label: 'ID' },
  { key: 'bemPatrimonialId', label: 'Bem ID' },
  { key: 'dataDepreciacao', label: 'Data' },
  { key: 'valorDepreciacao', label: 'Valor Depreciação' },
  { key: 'valorAcumulado', label: 'Valor Acumulado' },
  { key: 'valorLiquido', label: 'Valor Líquido' },
];

export default function DepreciacoesPage() {
  return (
    <CrudPage<Depreciacao>
      title="Depreciações"
      labelSingular="Depreciação"
      endpoint="/patrimonio/depreciacoes"
      columns={columns}
      renderForm={(item, set) => (
        <div className="grid grid-cols-2 gap-4">
          <InputField label="Bem Patrimonial ID" type="number" value={item.bemPatrimonialId} onChange={(v) => set({ ...item, bemPatrimonialId: v })} />
          <InputField label="Data Depreciação" type="date" value={item.dataDepreciacao} onChange={(v) => set({ ...item, dataDepreciacao: v })} />
          <InputField label="Valor Depreciação" type="number" value={item.valorDepreciacao} onChange={(v) => set({ ...item, valorDepreciacao: v })} />
          <InputField label="Valor Acumulado" type="number" value={item.valorAcumulado} onChange={(v) => set({ ...item, valorAcumulado: v })} />
          <InputField label="Valor Líquido" type="number" value={item.valorLiquido} onChange={(v) => set({ ...item, valorLiquido: v })} />
          <InputField label="Mês Referência" type="number" value={item.mesReferencia} onChange={(v) => set({ ...item, mesReferencia: v })} />
          <InputField label="Ano Referência" type="number" value={item.anoReferencia} onChange={(v) => set({ ...item, anoReferencia: v })} />
        </div>
      )}
    />
  );
}
