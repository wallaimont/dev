import { useEffect, useState, useCallback } from 'react';
import { useCrud } from '@/hooks/useCrud';
import DataTable from '@/components/DataTable';
import PageHeader from '@/components/PageHeader';
import Modal from '@/components/Modal';
import ConfirmDialog from '@/components/ConfirmDialog';
import type { Column } from '@/types';

interface CrudPageProps<T extends { id?: number | string }> {
  title: string;
  endpoint: string;
  columns: Column<T>[];
  renderForm: (item: Partial<T>, onChange: (field: string, value: any) => void) => React.ReactNode;
  keyExtractor?: (row: T) => string | number;
  labelSingular?: string;
  canDelete?: boolean;
  extraActions?: (item: T, reload: () => void) => React.ReactNode;
}

export default function CrudPage<T extends { id?: number | string }>({
  title, endpoint, columns, renderForm, keyExtractor, labelSingular, canDelete = true, extraActions,
}: CrudPageProps<T>) {
  const crud = useCrud<T>({ endpoint, labelSingular: labelSingular ?? title });
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<Partial<T>>({});
  const [deleteId, setDeleteId] = useState<number | string | null>(null);

  const reload = useCallback(() => {
    const params: Record<string, string> = {};
    if (search) params.search = search;
    crud.list(page, 20, params);
  }, [page, search, crud.list]);

  useEffect(() => { reload(); }, [page, search]);

  const handleSave = async () => {
    const isEdit = editing.id != null;
    const result = isEdit ? await crud.update(editing.id!, editing) : await crud.create(editing);
    if (result) {
      setModalOpen(false);
      setEditing({});
      reload();
    }
  };

  const handleDelete = async () => {
    if (deleteId != null) {
      const ok = await crud.remove(deleteId);
      if (ok) reload();
      setDeleteId(null);
    }
  };

  const handleFieldChange = (field: string, value: any) => {
    setEditing((prev) => ({ ...prev, [field]: value }));
  };

  const allColumns: Column<T>[] = [
    ...columns,
    ...(canDelete || extraActions
      ? [{
          key: '_actions' as any,
          label: 'Ações',
          className: 'w-32',
          render: (row: T) => (
            <div className="flex items-center gap-2">
              <button
                onClick={(e) => { e.stopPropagation(); setEditing(row); setModalOpen(true); }}
                className="text-xs text-primary-600 hover:underline"
              >
                Editar
              </button>
              {canDelete && (
                <button
                  onClick={(e) => { e.stopPropagation(); setDeleteId((row as any).id); }}
                  className="text-xs text-red-600 hover:underline"
                >
                  Excluir
                </button>
              )}
              {extraActions?.(row, reload)}
            </div>
          ),
        }]
      : []),
  ];

  const defaultKeyExtractor = (row: T) => String((row as any).id);

  return (
    <div>
      <PageHeader
        title={title}
        onAdd={() => { setEditing({}); setModalOpen(true); }}
        search={search}
        onSearchChange={setSearch}
      />
      <DataTable
        columns={allColumns}
        data={crud.items}
        loading={crud.loading}
        page={page}
        totalPages={crud.totalPages}
        totalElements={crud.totalElements}
        onPageChange={setPage}
        onRowClick={(row) => { setEditing(row); setModalOpen(true); }}
        keyExtractor={keyExtractor ?? defaultKeyExtractor}
      />
      <Modal
        open={modalOpen}
        onClose={() => { setModalOpen(false); setEditing({}); }}
        title={editing.id != null ? `Editar ${labelSingular ?? title}` : `Novo ${labelSingular ?? title}`}
        wide
      >
        <div className="space-y-4">
          {renderForm(editing, handleFieldChange)}
          <div className="flex justify-end gap-3 pt-4 border-t">
            <button
              onClick={() => { setModalOpen(false); setEditing({}); }}
              className="rounded-lg border px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50"
            >
              Cancelar
            </button>
            <button
              onClick={handleSave}
              className="rounded-lg bg-primary-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-primary-700"
            >
              Salvar
            </button>
          </div>
        </div>
      </Modal>
      <ConfirmDialog
        open={deleteId != null}
        onClose={() => setDeleteId(null)}
        onConfirm={handleDelete}
        message={`Deseja realmente excluir este registro?`}
        confirmLabel="Excluir"
        danger
      />
    </div>
  );
}
