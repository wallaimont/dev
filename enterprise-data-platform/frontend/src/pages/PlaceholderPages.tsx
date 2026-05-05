import React from 'react';
import { Building2, Users, Shield, BookOpen, Bell, Settings, RefreshCw } from 'lucide-react';

// Generic page placeholder used for secondary pages
export function PlaceholderPage({ title, description, icon: Icon }: { title: string; description: string; icon: any }) {
  return (
    <div className="space-y-6 animate-fade-in">
      <div>
        <h1 className="text-2xl font-bold text-white">{title}</h1>
        <p className="text-sm text-surface-400 mt-1">{description}</p>
      </div>
      <div className="glass-card p-12 flex flex-col items-center justify-center text-center">
        <div className="p-4 rounded-2xl bg-brand-600/20 text-brand-400 mb-4">
          <Icon size={40} />
        </div>
        <h3 className="text-lg font-semibold text-white mb-2">Módulo em construção</h3>
        <p className="text-sm text-surface-400 max-w-md">
          Este módulo está sendo desenvolvido e será disponibilizado em breve com funcionalidades completas de {title.toLowerCase()}.
        </p>
      </div>
    </div>
  );
}

export const IntegrationsPage = () => <PlaceholderPage title="Monitor de Integrações" description="Acompanhe as integrações em tempo real" icon={RefreshCw} />;
export const GovernancePage = () => <PlaceholderPage title="Catálogo de Dados" description="Dicionário de dados e linhagem" icon={BookOpen} />;
export const UsersPage = () => <PlaceholderPage title="Gestão de Usuários" description="Usuários, perfis e permissões" icon={Users} />;
export const CompaniesPage = () => <PlaceholderPage title="Empresas e Filiais" description="Cadastro de empresas e filiais" icon={Building2} />;
export const AuditPage = () => <PlaceholderPage title="Auditoria" description="Logs de ações e rastreabilidade" icon={Shield} />;
export const AlertsPage = () => <PlaceholderPage title="Alertas" description="Configuração de alertas automáticos" icon={Bell} />;
export const SettingsPage = () => <PlaceholderPage title="Configurações" description="Parâmetros gerais do sistema" icon={Settings} />;
