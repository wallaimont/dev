import clsx from 'clsx';

const colorMap: Record<string, string> = {
  ATIVO: 'bg-green-100 text-green-800',
  ABERTO: 'bg-blue-100 text-blue-800',
  ABERTA: 'bg-blue-100 text-blue-800',
  APROVADO: 'bg-green-100 text-green-800',
  RASCUNHO: 'bg-gray-100 text-gray-800',
  CANCELADO: 'bg-red-100 text-red-800',
  REPROVADO: 'bg-red-100 text-red-800',
  QUITADO: 'bg-green-100 text-green-800',
  PARCIAL: 'bg-yellow-100 text-yellow-800',
  NOVO: 'bg-blue-100 text-blue-800',
  CONTATADO: 'bg-indigo-100 text-indigo-800',
  QUALIFICADO: 'bg-purple-100 text-purple-800',
  CONVERTIDO: 'bg-green-100 text-green-800',
  PERDIDO: 'bg-red-100 text-red-800',
  GANHA: 'bg-green-100 text-green-800',
  PERDIDA: 'bg-red-100 text-red-800',
  DEMITIDO: 'bg-red-100 text-red-800',
  AFASTADO: 'bg-yellow-100 text-yellow-800',
  FERIAS: 'bg-cyan-100 text-cyan-800',
  EM_CONTAGEM: 'bg-yellow-100 text-yellow-800',
  FINALIZADO: 'bg-green-100 text-green-800',
  RECEBIDO: 'bg-green-100 text-green-800',
  FATURADO: 'bg-green-100 text-green-800',
  ENTRADA: 'bg-green-100 text-green-800',
  SAIDA: 'bg-red-100 text-red-800',
  AJUSTE: 'bg-yellow-100 text-yellow-800',
  PAGAR: 'bg-red-100 text-red-800',
  RECEBER: 'bg-green-100 text-green-800',
};

export default function StatusBadge({ value }: { value: string }) {
  const colors = colorMap[value] ?? 'bg-gray-100 text-gray-800';
  return (
    <span className={clsx('inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium', colors)}>
      {value.replace(/_/g, ' ')}
    </span>
  );
}
