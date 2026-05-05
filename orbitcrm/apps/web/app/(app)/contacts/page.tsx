'use client';

import { useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import type { Contact } from '@orbitcrm/types';
import { Button, Card, Input } from '@orbitcrm/ui';
import { contacts as initialContacts } from '../../../lib/demo';
import { createContact, deleteContact, getContacts } from '../../../lib/api';

export default function ContactsPage() {
  const queryClient = useQueryClient();
  const [localItems, setLocalItems] = useState<Contact[]>([]);
  const [removedIds, setRemovedIds] = useState<string[]>([]);
  const { data: serverItems = initialContacts, isFetching } = useQuery({
    queryKey: ['contacts'],
    queryFn: getContacts,
    initialData: initialContacts,
    retry: 0,
  });
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [title, setTitle] = useState('');
  const [account, setAccount] = useState('');

  const createMutation = useMutation({
    mutationFn: createContact,
    onSuccess: (created) => {
      setLocalItems((current) => [created, ...current.filter((item) => item.id !== created.id)]);
      queryClient.invalidateQueries({ queryKey: ['contacts'] });
    },
  });

  const deleteMutation = useMutation({ mutationFn: deleteContact });

  const items = [
    ...localItems,
    ...serverItems.filter((item) => !removedIds.includes(item.id) && !localItems.some((local) => local.id === item.id)),
  ];

  function addContact() {
    if (!name || !email) return;

    createMutation.mutate({ name, email, title, account });
    setName('');
    setEmail('');
    setTitle('');
    setAccount('');
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-2 md:flex-row md:items-end md:justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-slate-950">Contatos</h1>
          <p className="text-sm text-slate-500">Decisores, influenciadores e stakeholders conectados ao ciclo comercial.</p>
        </div>
        <p className="text-xs text-slate-500">{isFetching ? 'Sincronizando com a API...' : 'Relacionamentos ativos com fallback demo.'}</p>
      </div>

      <Card>
        <div className="grid gap-3 md:grid-cols-5">
          <Input placeholder="Nome" value={name} onChange={(e) => setName(e.target.value)} />
          <Input placeholder="E-mail" value={email} onChange={(e) => setEmail(e.target.value)} />
          <Input placeholder="Cargo" value={title} onChange={(e) => setTitle(e.target.value)} />
          <Input placeholder="Conta" value={account} onChange={(e) => setAccount(e.target.value)} />
          <Button onClick={addContact} disabled={createMutation.isPending}>{createMutation.isPending ? 'Salvando...' : 'Adicionar contato'}</Button>
        </div>
      </Card>

      <Card className="overflow-hidden p-0">
        <div className="overflow-x-auto">
          <table className="min-w-full text-sm">
            <thead className="bg-slate-50 text-left text-slate-500">
              <tr>
                <th className="px-4 py-3">Contato</th>
                <th className="px-4 py-3">Cargo</th>
                <th className="px-4 py-3">Conta</th>
                <th className="px-4 py-3">Telefone</th>
                <th className="px-4 py-3">Owner</th>
                <th className="px-4 py-3 text-right">Ações</th>
              </tr>
            </thead>
            <tbody>
              {items.map((contact) => (
                <tr key={contact.id} className="border-t border-slate-100">
                  <td className="px-4 py-3">
                    <p className="font-medium text-slate-900">{contact.name}</p>
                    <p className="text-xs text-slate-500">{contact.email}</p>
                  </td>
                  <td className="px-4 py-3 text-slate-700">{contact.title}</td>
                  <td className="px-4 py-3 text-slate-700">{contact.account}</td>
                  <td className="px-4 py-3 text-slate-700">{contact.phone}</td>
                  <td className="px-4 py-3 text-slate-700">{contact.owner}</td>
                  <td className="px-4 py-3 text-right">
                    <button
                      type="button"
                      onClick={() => {
                        setLocalItems((current) => current.filter((item) => item.id !== contact.id));
                        setRemovedIds((current) => [...new Set([...current, contact.id])]);
                        queryClient.setQueryData<Contact[]>(['contacts'], (current = initialContacts) => current.filter((item) => item.id !== contact.id));
                        deleteMutation.mutate(contact.id);
                      }}
                      className="text-xs font-medium text-rose-600 hover:text-rose-700"
                    >
                      Excluir
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </Card>
    </div>
  );
}
