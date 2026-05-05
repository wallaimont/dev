import React, { useEffect, useState } from 'react';
import { Outlet, NavLink, useNavigate } from 'react-router-dom';
import api from '../services/api';
import { useAuthStore } from '../store/authStore';
import type { TenantContext } from '../types';
import {
  LayoutDashboard, Database, RefreshCw, Layers, BookOpen,
  BarChart3, Users, Building2, Shield, Bell, Settings,
  LogOut, Menu, X, Activity, CreditCard, FileSignature, Rocket
} from 'lucide-react';

const navItems = [
  { to: '/dashboard', label: 'Dashboard', icon: LayoutDashboard },
  { to: '/data-sources', label: 'Fontes de Dados', icon: Database },
  { to: '/integrations', label: 'Integrações', icon: RefreshCw },
  { to: '/etl', label: 'ETL Monitor', icon: Layers },
  { to: '/governance', label: 'Catálogo de Dados', icon: BookOpen },
  { to: '/kpis', label: 'KPIs', icon: BarChart3 },
  { to: '/users', label: 'Usuários', icon: Users },
  { to: '/companies', label: 'Empresas', icon: Building2 },
  { to: '/subscriptions', label: 'Billing SaaS', icon: CreditCard },
  { to: '/contracts', label: 'Contratos', icon: FileSignature },
  { to: '/onboarding', label: 'Onboarding', icon: Rocket },
  { to: '/audit', label: 'Auditoria', icon: Shield },
  { to: '/alerts', label: 'Alertas', icon: Bell },
  { to: '/settings', label: 'Prontidão Comercial', icon: Settings },
];

export default function AppLayout() {
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [mobileOpen, setMobileOpen] = useState(false);
  const [tenantContext, setTenantContext] = useState<TenantContext | null>(null);
  const { user, logout, selectedCompanyId, setSelectedCompanyId } = useAuthStore();
  const navigate = useNavigate();

  useEffect(() => {
    const loadTenantContext = async () => {
      try {
        const response = await api.get<TenantContext>('/tenant/context', {
          params: { companyId: selectedCompanyId ?? undefined },
        });
        setTenantContext(response.data);
        if (selectedCompanyId == null && response.data.effectiveCompanyId) {
          setSelectedCompanyId(response.data.effectiveCompanyId);
        }
      } catch {
        setTenantContext(null);
      }
    };

    void loadTenantContext();
  }, [selectedCompanyId, setSelectedCompanyId]);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="flex h-screen overflow-hidden bg-surface-950">
      {/* Mobile overlay */}
      {mobileOpen && (
        <div
          className="fixed inset-0 bg-black/60 backdrop-blur-sm z-40 lg:hidden"
          onClick={() => setMobileOpen(false)}
        />
      )}

      {/* Sidebar */}
      <aside
        className={`
          fixed lg:static inset-y-0 left-0 z-50
          flex flex-col
          bg-surface-900/95 backdrop-blur-2xl border-r border-surface-700/50
          transition-all duration-300 ease-in-out
          ${sidebarOpen ? 'w-64' : 'w-20'}
          ${mobileOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'}
        `}
      >
        {/* Logo */}
        <div className="flex items-center justify-between px-4 py-5 border-b border-surface-700/50">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 bg-gradient-to-br from-brand-500 to-blue-400 rounded-xl flex items-center justify-center shadow-lg shadow-brand-500/30">
              <Activity size={20} className="text-white" />
            </div>
            {sidebarOpen && (
              <div className="animate-fade-in">
                <h1 className="text-sm font-bold gradient-text">Enterprise</h1>
                <p className="text-[10px] text-surface-400 font-medium">DATA PLATFORM</p>
              </div>
            )}
          </div>
          <button
            onClick={() => setSidebarOpen(!sidebarOpen)}
            className="hidden lg:flex p-1.5 rounded-lg hover:bg-surface-700/50 text-surface-400 transition-colors"
          >
            <Menu size={18} />
          </button>
          <button
            onClick={() => setMobileOpen(false)}
            className="lg:hidden p-1.5 rounded-lg hover:bg-surface-700/50 text-surface-400"
          >
            <X size={18} />
          </button>
        </div>

        {/* Navigation */}
        <nav className="flex-1 overflow-y-auto py-4 px-3 space-y-1">
          {navItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              onClick={() => setMobileOpen(false)}
              className={({ isActive }) =>
                `flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all duration-200
                ${isActive
                  ? 'bg-brand-600/20 text-brand-400 border border-brand-500/20 shadow-sm shadow-brand-500/10'
                  : 'text-surface-400 hover:text-white hover:bg-surface-700/50'
                }
                ${!sidebarOpen ? 'justify-center' : ''}`
              }
            >
              <item.icon size={20} />
              {sidebarOpen && <span className="animate-fade-in">{item.label}</span>}
            </NavLink>
          ))}
        </nav>

        {/* User section */}
        <div className="border-t border-surface-700/50 p-4">
          <div className={`flex items-center ${sidebarOpen ? 'gap-3' : 'justify-center'}`}>
            <div className="w-9 h-9 bg-gradient-to-br from-emerald-500 to-teal-400 rounded-xl flex items-center justify-center text-white text-sm font-bold shadow-md">
              {user?.name?.charAt(0) || 'A'}
            </div>
            {sidebarOpen && (
              <div className="flex-1 min-w-0 animate-fade-in">
                <p className="text-sm font-medium text-white truncate">{user?.name || 'Admin'}</p>
                <p className="text-xs text-surface-400 truncate">{user?.email || 'admin@edp.com'}</p>
              </div>
            )}
            {sidebarOpen && (
              <button
                onClick={handleLogout}
                className="p-2 rounded-lg hover:bg-red-500/20 text-surface-400 hover:text-red-400 transition-colors"
                title="Sair"
              >
                <LogOut size={18} />
              </button>
            )}
          </div>
        </div>
      </aside>

      {/* Main content */}
      <div className="flex-1 flex flex-col overflow-hidden">
        {/* Header */}
        <header className="flex items-center justify-between px-6 py-4 bg-surface-900/50 backdrop-blur-xl border-b border-surface-700/50">
          <div className="flex items-center gap-4">
            <button
              onClick={() => setMobileOpen(true)}
              className="lg:hidden p-2 rounded-lg hover:bg-surface-700/50 text-surface-400"
            >
              <Menu size={20} />
            </button>
            <div>
              <h2 className="text-lg font-semibold text-white">Bem-vindo, {user?.name?.split(' ')[0] || 'Admin'}</h2>
              <p className="text-xs text-surface-400">{tenantContext?.companyName || user?.companyName || 'Enterprise Corp'} • {new Date().toLocaleDateString('pt-BR', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}</p>
              <div className="mt-2 flex flex-wrap items-center gap-2">
                {tenantContext?.canSwitchTenant ? (
                  <select
                    className="rounded-lg border border-surface-700 bg-surface-800 px-2 py-1 text-xs text-surface-200"
                    value={selectedCompanyId ?? ''}
                    onChange={(event) => setSelectedCompanyId(event.target.value ? Number(event.target.value) : null)}
                  >
                    <option value="">Visão global</option>
                    {tenantContext.availableTenants.map((tenant) => (
                      <option key={tenant.companyId} value={tenant.companyId}>{tenant.companyName}</option>
                    ))}
                  </select>
                ) : (
                  <span className="rounded-full border border-brand-500/30 bg-brand-500/10 px-2 py-1 text-[11px] text-brand-200">
                    Tenant {tenantContext?.tenantCode || user?.companyName || 'principal'}
                  </span>
                )}
                {tenantContext?.subscriptionPlan ? (
                  <span className="rounded-full border border-surface-700/60 px-2 py-1 text-[11px] text-surface-300">
                    {tenantContext.subscriptionPlan} • {tenantContext.subscriptionStatus || 'ATIVO'}
                  </span>
                ) : null}
              </div>
            </div>
          </div>
          <NavLink
            to="/alerts"
            className="flex items-center gap-2 rounded-xl border border-surface-700/50 bg-surface-800/50 px-3 py-2 text-sm text-surface-300 transition-all hover:border-brand-500/30 hover:text-white"
          >
            <Bell size={16} className="text-surface-400" />
            <span>Central de alertas</span>
          </NavLink>
        </header>

        {/* Page content */}
        <main className="flex-1 overflow-y-auto p-6">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
