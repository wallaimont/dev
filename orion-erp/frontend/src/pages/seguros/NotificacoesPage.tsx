import { useEffect, useState, useCallback } from 'react';
import { useCrud } from '@/hooks/useCrud';
import DataTable from '@/components/DataTable';
import PageHeader from '@/components/PageHeader';
import StatusBadge from '@/components/StatusBadge';
import type { NotificacaoSeguro, Column } from '@/types';
import api from '@/lib/api';

const columns: Column<NotificacaoSeguro>[] = [
  { key: 'id', label: 'ID', className: 'w-16' },
  { key: 'tipo', label: 'Tipo', render: (r) => <StatusBadge value={r.tipo} /> },
  { key: 'titulo', label: 'Título' },
  { key: 'descricao', label: 'Descrição' },
  { key: 'destinatario', label: 'Destinatário' },
  { key: 'lida', label: 'Lida', render: (r) => <StatusBadge value={r.lida ? 'SIM' : 'NÃO'} /> },
  { key: 'createdAt', label: 'Data' },
];

export default function NotificacoesPage() {
  const crud = useCrud<NotificacaoSeguro>({ endpoint: '/seguros/notificacoes', labelSingular: 'Notificação' });
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState('');

  const reload = useCallback(() => {
    const params: Record<string, string> = {};
    if (search) params.search = search;
    crud.list(page, 20, params);
  }, [page, search, crud.list]);

  useEffect(() => { reload(); }, [page, search]);

  const marcarComoLida = async (id: number) => {
    await api.patch(`/seguros/notificacoes/${id}/lida`);
    reload();
  };

  const allColumns: Column<NotificacaoSeguro>[] = [
    ...columns,
    {
      key: '_actions',
      label: 'Ações',
      className: 'w-28',
      render: (row) =>
        !row.lida ? (
          <button
            onClick={(e) => { e.stopPropagation(); marcarComoLida(row.id); }}
            className="text-xs text-primary-600 hover:underline"
          >
            Marcar lida
          </button>
        ) : null,
    },
  ];

  return (
    <div>
      <PageHeader title="Notificações de Seguros" search={search} onSearchChange={setSearch} />
      <DataTable
        columns={allColumns}
        data={crud.items}
        loading={crud.loading}
        page={page}
        totalPages={crud.totalPages}
        totalElements={crud.totalElements}
        onPageChange={setPage}
        keyExtractor={(r) => String(r.id)}
      />
    </div>
  );
}
