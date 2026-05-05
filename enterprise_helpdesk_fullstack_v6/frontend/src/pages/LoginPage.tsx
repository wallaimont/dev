import { useState } from 'react'
import { useAuth } from '../hooks/useAuth'

export function LoginPage() {
  const { login, isLoading } = useAuth()
  const [email, setEmail] = useState('admin@empresa.com')
  const [password, setPassword] = useState('Admin1234')
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  async function handleSubmit(event: React.FormEvent) {
    event.preventDefault()
    setSubmitting(true)
    setError('')

    try {
      await login({ email, password })
    } catch {
      setError('Não foi possível autenticar. Verifique e-mail e senha.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="login-page">
      <form className="login-card" onSubmit={handleSubmit}>
        <div>
          <h1>Enterprise Helpdesk</h1>
          <p className="muted">Entre com sua conta para acessar o painel full stack.</p>
        </div>

        <label>
          <span>E-mail</span>
          <input value={email} onChange={(e) => setEmail(e.target.value)} type="email" required />
        </label>

        <label>
          <span>Senha</span>
          <input value={password} onChange={(e) => setPassword(e.target.value)} type="password" required />
        </label>

        {error ? <div className="error-box">{error}</div> : null}

        <button className="primary-button" type="submit" disabled={submitting || isLoading}>
          {submitting ? 'Entrando...' : 'Entrar'}
        </button>

        <div className="hint-box">
          <strong>Login padrão</strong>
          <span>admin@empresa.com / Admin1234</span>
        </div>
      </form>
    </div>
  )
}
