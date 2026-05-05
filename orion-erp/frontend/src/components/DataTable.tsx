import { ChevronLeft, ChevronRight } from 'lucide-react';
import type { Column } from '@/types';
import clsx from 'clsx';

interface DataTableProps<T> {
  columns: Column<T>[];
  data: T[];
  loading?: boolean;
  page: number;
  totalPages: number;
  totalElements: number;
  onPageChange: (page: number) => void;
  onRowClick?: (row: T) => void;
  keyExtractor: (row: T) => string | number;
}

export default function DataTable<T>({
  columns, data, loading, page, totalPages, totalElements,
  onPageChange, onRowClick, keyExtractor,
}: DataTableProps<T>) {
  return (
    <div className="overflow-hidden rounded-lg border bg-white shadow-sm">
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              {columns.map((col) => (
                <th
                  key={String(col.key)}
                  className={clsx('px-4 py-3 text-left text-xs font-semibold uppercase text-gray-500', col.className)}
                >
                  {col.label}
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {loading ? (
              <tr>
                <td colSpan={columns.length} className="px-4 py-8 text-center text-gray-400">
                  Carregando...
                </td>
              </tr>
            ) : !data || data.length === 0 ? (
              <tr>
                <td colSpan={columns.length} className="px-4 py-8 text-center text-gray-400">
                  Nenhum registro encontrado
                </td>
              </tr>
            ) : (
              data.map((row) => (
                <tr
                  key={keyExtractor(row)}
                  onClick={() => onRowClick?.(row)}
                  className={clsx('transition-colors', onRowClick && 'cursor-pointer hover:bg-gray-50')}
                >
                  {columns.map((col) => (
                    <td key={String(col.key)} className={clsx('px-4 py-3 text-sm text-gray-700', col.className)}>
                      {col.render ? col.render(row) : String((row as any)[col.key] ?? '')}
                    </td>
                  ))}
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
      <div className="flex items-center justify-between border-t px-4 py-3 text-sm text-gray-500">
        <span>{totalElements} registro(s)</span>
        <div className="flex items-center gap-2">
          <button
            disabled={page === 0}
            onClick={() => onPageChange(page - 1)}
            className="rounded p-1 hover:bg-gray-100 disabled:opacity-40"
          >
            <ChevronLeft size={18} />
          </button>
          <span>
            {page + 1} / {Math.max(totalPages, 1)}
          </span>
          <button
            disabled={page >= totalPages - 1}
            onClick={() => onPageChange(page + 1)}
            className="rounded p-1 hover:bg-gray-100 disabled:opacity-40"
          >
            <ChevronRight size={18} />
          </button>
        </div>
      </div>
    </div>
  );
}
