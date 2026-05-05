import { useEffect, useState } from 'react'
import { getCompanies } from '../api/companies'
import type { Company } from '../types'

export function CompaniesPage() {
  const [companies, setCompanies] = useState<Company[]>([])

  useEffect(() => {
    getCompanies().then(setCompanies)
  }, [])

  return (
    <div className="page-grid">
      <header className="page-header">
        <div>
          <h1>Empresas</h1>
          <p className="muted">Base de clientes e organizações cadastradas no back-end.</p>
        </div>
      </header>

      <div className="card table-card">
        <div className="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Nome</th>
                <th>Segmento</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {companies.map((company) => (
                <tr key={company.id}>
                  <td>{company.id}</td>
                  <td>{company.name}</td>
                  <td>{company.segment || 'Não informado'}</td>
                  <td>
                    <span className={`badge ${company.active ? 'status-open' : 'status-closed'}`}>
                      {company.active ? 'Ativa' : 'Inativa'}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
