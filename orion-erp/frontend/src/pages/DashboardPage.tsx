import { useEffect, useState } from 'react';
import { DollarSign, Package, Users, TrendingUp, ShoppingCart, FileText } from 'lucide-react';
import api from '@/lib/api';

interface CardData {
  label: string;
  value: string;
  icon: React.ReactNode;
  color: string;
}

export default function DashboardPage() {
  const [cards, setCards] = useState<CardData[]>([]);

  useEffect(() => {
    async function load() {
      const results: CardData[] = [];
      const endpoints = [
        { url: '/financeiro/titulos?size=1&tipo=RECEBER', label: 'Títulos a Receber', icon: <DollarSign size={24} />, color: 'bg-green-500' },
        { url: '/financeiro/titulos?size=1&tipo=PAGAR', label: 'Títulos a Pagar', icon: <FileText size={24} />, color: 'bg-red-500' },
        { url: '/vendas/pedidos?size=1', label: 'Pedidos de Venda', icon: <ShoppingCart size={24} />, color: 'bg-blue-500' },
        { url: '/crm/leads?size=1', label: 'Leads', icon: <TrendingUp size={24} />, color: 'bg-purple-500' },
        { url: '/cadastros/clientes?size=1', label: 'Clientes', icon: <Users size={24} />, color: 'bg-indigo-500' },
        { url: '/cadastros/produtos?size=1', label: 'Produtos', icon: <Package size={24} />, color: 'bg-amber-500' },
      ];
      for (const ep of endpoints) {
        try {
          const { data } = await api.get(ep.url);
          const total = data.data?.totalElements ?? 0;
          results.push({ label: ep.label, value: String(total), icon: ep.icon, color: ep.color });
        } catch {
          results.push({ label: ep.label, value: '-', icon: ep.icon, color: ep.color });
        }
      }
      setCards(results);
    }
    load();
  }, []);

  return (
    <div>
      <h1 className="mb-6 text-2xl font-bold text-gray-900">Dashboard</h1>
      <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
        {cards.map((c) => (
          <div key={c.label} className="flex items-center gap-4 rounded-xl bg-white p-6 shadow-sm border">
            <div className={`flex h-12 w-12 items-center justify-center rounded-lg text-white ${c.color}`}>
              {c.icon}
            </div>
            <div>
              <p className="text-sm text-gray-500">{c.label}</p>
              <p className="text-2xl font-bold text-gray-900">{c.value}</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
