import { Link, useLocation } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'
import { useTheme } from '../hooks/useTheme'

export function AppLayout({ children }: { children: React.ReactNode }) {
  const { pathname } = useLocation()
  const { user, logout } = useAuth()
  const { theme, toggleTheme } = useTheme()
  const canViewCompanies = user?.role !== 'client'

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div>
          <div className="brand">Enterprise Helpdesk</div>
          <p className="muted">React + FastAPI + PostgreSQL</p>
        </div>

        <nav className="nav-links">
          <Link className={pathname === '/' ? 'active' : ''} to="/">Dashboard</Link>
          <Link className={pathname === '/tickets' ? 'active' : ''} to="/tickets">Chamados</Link>
          {canViewCompanies && <Link className={pathname === '/companies' ? 'active' : ''} to="/companies">Empresas</Link>}
        </nav>

        <div className="sidebar-footer">
          <button className="secondary-button" onClick={toggleTheme}>Tema: {theme === 'dark' ? 'Escuro' : 'Claro'}</button>
          <div className="user-card">
            <strong>{user?.full_name}</strong>
            <span className="small">{user?.email}</span>
            <span className="small muted">Perfil: {user?.role}</span>
          </div>
          <button className="secondary-button" onClick={logout}>Sair</button>
        </div>
      </aside>

      <main className="content">{children}</main>
    </div>
  )
}
