import { LogOut, User } from 'lucide-react';
import { useAuth } from '@/contexts/AuthContext';

export default function Topbar() {
  const { user, logout } = useAuth();

  return (
    <header className="sticky top-0 z-20 flex h-16 items-center justify-between border-b bg-white px-6 shadow-sm">
      <div />
      <div className="flex items-center gap-4">
        <span className="flex items-center gap-2 text-sm text-gray-600">
          <User size={18} />
          {user?.nome ?? 'Usuário'}
        </span>
        <button
          onClick={logout}
          className="flex items-center gap-1 rounded-lg px-3 py-1.5 text-sm text-gray-500 hover:bg-gray-100 transition-colors"
        >
          <LogOut size={16} />
          Sair
        </button>
      </div>
    </header>
  );
}
