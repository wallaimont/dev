import { useState, useCallback } from 'react';
import api from '@/lib/api';
import type { ApiResponse, PageResponse } from '@/types';
import toast from 'react-hot-toast';

interface UseCrudOptions {
  endpoint: string;
  labelSingular?: string;
}

export function useCrud<T extends { id?: number | string }>({ endpoint, labelSingular = 'Registro' }: UseCrudOptions) {
  const [items, setItems] = useState<T[]>([]);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(false);

  const list = useCallback(async (page = 0, size = 20, params?: Record<string, string>) => {
    setLoading(true);
    try {
      const { data } = await api.get<ApiResponse<PageResponse<T>>>(endpoint, {
        params: { page, size, ...params },
      });
      const pg = data.data;
      setItems(pg.items ?? []);
      setTotalPages(pg.totalPages ?? 0);
      setTotalElements(pg.totalElements ?? 0);
    } catch {
      toast.error(`Erro ao carregar ${labelSingular.toLowerCase()}s`);
    } finally {
      setLoading(false);
    }
  }, [endpoint, labelSingular]);

  const get = useCallback(async (id: number | string): Promise<T | null> => {
    try {
      const { data } = await api.get<ApiResponse<T>>(`${endpoint}/${id}`);
      return data.data;
    } catch {
      toast.error(`Erro ao carregar ${labelSingular.toLowerCase()}`);
      return null;
    }
  }, [endpoint, labelSingular]);

  const create = useCallback(async (payload: Partial<T>): Promise<T | null> => {
    try {
      const { data } = await api.post<ApiResponse<T>>(endpoint, payload);
      toast.success(`${labelSingular} criado com sucesso`);
      return data.data;
    } catch (err: any) {
      toast.error(err.response?.data?.message ?? `Erro ao criar ${labelSingular.toLowerCase()}`);
      return null;
    }
  }, [endpoint, labelSingular]);

  const update = useCallback(async (id: number | string, payload: Partial<T>): Promise<T | null> => {
    try {
      const { data } = await api.put<ApiResponse<T>>(`${endpoint}/${id}`, payload);
      toast.success(`${labelSingular} atualizado com sucesso`);
      return data.data;
    } catch (err: any) {
      toast.error(err.response?.data?.message ?? `Erro ao atualizar ${labelSingular.toLowerCase()}`);
      return null;
    }
  }, [endpoint, labelSingular]);

  const remove = useCallback(async (id: number | string): Promise<boolean> => {
    try {
      await api.delete(`${endpoint}/${id}`);
      toast.success(`${labelSingular} removido com sucesso`);
      return true;
    } catch (err: any) {
      toast.error(err.response?.data?.message ?? `Erro ao remover ${labelSingular.toLowerCase()}`);
      return false;
    }
  }, [endpoint, labelSingular]);

  return { items, totalPages, totalElements, loading, list, get, create, update, remove };
}
