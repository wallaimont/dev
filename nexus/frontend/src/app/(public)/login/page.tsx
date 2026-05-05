'use client'

// ============================================================
// app/(public)/login/page.tsx
// ============================================================
import { useState } from 'react'
import Link from 'next/link'
import { useRouter } from 'next/navigation'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useMutation } from '@tanstack/react-query'
import toast from 'react-hot-toast'
import { authApi } from '@/lib/api/client'
import { useAuthStore } from '@/store'
import { parseApiError } from '@/lib/utils'

const loginSchema = z.object({
  email: z.string().email('E-mail invalido'),
  password: z.string().min(1, 'Senha obrigatoria'),
})

const registerSchema = z.object({
  firstName: z.string().min(2, 'Nome muito curto'),
  lastName: z.string().min(2, 'Sobrenome muito curto'),
  email: z.string().email('E-mail invalido'),
  phone: z.string().optional(),
  password: z.string().min(8, 'Senha deve ter ao menos 8 caracteres'),
  terms: z.boolean().refine((value) => value, 'Aceite os termos para continuar'),
})

type LoginForm = z.infer<typeof loginSchema>
type RegisterForm = z.infer<typeof registerSchema>

function getPostLoginRoute(roles: string[], fallback?: string | null) {
  if (fallback && fallback.startsWith('/')) return fallback
  return roles.includes('SELLER') ? '/seller/dashboard' : '/dashboard'
}

export default function AuthPage() {
  const [tab, setTab] = useState<'login' | 'register'>('login')
  const router = useRouter()
  const setAuth = useAuthStore((state) => state.setAuth)

  const loginForm = useForm<LoginForm>({
    resolver: zodResolver(loginSchema),
  })

  const registerForm = useForm<RegisterForm>({
    resolver: zodResolver(registerSchema),
    defaultValues: { terms: false },
  })

  const loginMutation = useMutation({
    mutationFn: (data: LoginForm) =>
      authApi.login({ ...data, deviceInfo: navigator.userAgent }).then((response) => response.data),
    onSuccess: (data) => {
      const fallback = typeof window !== 'undefined'
        ? new URLSearchParams(window.location.search).get('redirect')
        : null
      setAuth(data)
      toast.success('Bem-vindo de volta!')
      router.push(getPostLoginRoute(data.roles, fallback))
    },
    onError: (error) => toast.error(parseApiError(error)),
  })

  const registerMutation = useMutation({
    mutationFn: (data: RegisterForm) => authApi.register(data).then((response) => response.data),
    onSuccess: (data) => {
      const fallback = typeof window !== 'undefined'
        ? new URLSearchParams(window.location.search).get('redirect')
        : null
      setAuth(data)
      toast.success('Conta criada! Verifique seu e-mail.')
      router.push(getPostLoginRoute(data.roles, fallback))
    },
    onError: (error) => toast.error(parseApiError(error)),
  })

  return (
    <div className="min-h-screen grid grid-cols-2">
      <div className="bg-[#0B0F1A] p-12 flex flex-col justify-between">
        <Link href="/" className="flex items-center gap-2.5">
          <div className="w-9 h-9 bg-white/10 rounded-[9px] flex items-center justify-center">
            <span className="font-display font-800 text-nexus-500">N</span>
          </div>
          <span className="font-display text-xl font-800 text-white tracking-tight">
            NEX<span className="text-nexus-500">US</span>
          </span>
        </Link>

        <div>
          <h2 className="font-display text-[38px] font-800 text-white leading-tight tracking-tight mb-4">
            O marketplace que<br />conecta voce ao<br />
            <span className="text-yellow-200/85">melhor preco.</span>
          </h2>
          <p className="text-white/50 text-[15px] font-300 leading-relaxed max-w-sm">
            Compre de milhares de vendedores verificados. Pagamento seguro,
            entrega rastreada, garantia em cada produto.
          </p>
        </div>

        <div className="flex gap-8">
          {[
            { value: '50k+', label: 'Produtos' },
            { value: '1.2k', label: 'Vendedores' },
            { value: '98%', label: 'Satisfacao' },
          ].map((stat) => (
            <div key={stat.label}>
              <div className="font-display text-[22px] font-800 text-white">{stat.value}</div>
              <div className="text-[12px] text-white/40 mt-0.5">{stat.label}</div>
            </div>
          ))}
        </div>
      </div>

      <div className="bg-white flex items-center justify-center p-10">
        <div className="w-full max-w-[400px]">
          <div className="flex bg-[var(--bg-subtle)] rounded-xl p-1 mb-7 gap-1">
            {(['login', 'register'] as const).map((panel) => (
              <button
                key={panel}
                onClick={() => setTab(panel)}
                className={`flex-1 py-2.5 rounded-[10px] text-sm font-600 transition-all ${
                  tab === panel
                    ? 'bg-white text-[var(--text-primary)] shadow-nexus'
                    : 'text-[var(--text-tertiary)]'
                }`}
              >
                {panel === 'login' ? 'Entrar' : 'Criar conta'}
              </button>
            ))}
          </div>

          {tab === 'login' && (
            <form onSubmit={loginForm.handleSubmit((data) => loginMutation.mutate(data))}>
              <h1 className="font-display text-2xl font-800 tracking-tight mb-1">Bem-vindo de volta</h1>
              <p className="text-[var(--text-secondary)] text-sm mb-6">Entre na sua conta para continuar</p>

              <div className="space-y-4">
                <div>
                  <label className="label">E-mail</label>
                  <input {...loginForm.register('email')} className="input" placeholder="seu@email.com" />
                  {loginForm.formState.errors.email && (
                    <p className="text-xs text-red-500 mt-1">{loginForm.formState.errors.email.message}</p>
                  )}
                </div>
                <div>
                  <label className="label">Senha</label>
                  <input
                    {...loginForm.register('password')}
                    type="password"
                    className="input"
                    placeholder="••••••••"
                  />
                  <div className="text-right mt-1">
                    <Link href="/forgot-password" className="text-xs text-nexus-500">Esqueceu a senha?</Link>
                  </div>
                </div>
              </div>

              <button type="submit" disabled={loginMutation.isPending} className="btn-primary w-full mt-5 btn-lg font-display">
                {loginMutation.isPending ? 'Entrando...' : 'Entrar na conta ->'}
              </button>
            </form>
          )}

          {tab === 'register' && (
            <form onSubmit={registerForm.handleSubmit((data) => registerMutation.mutate(data))}>
              <h1 className="font-display text-2xl font-800 tracking-tight mb-1">Criar conta gratis</h1>
              <p className="text-[var(--text-secondary)] text-sm mb-6">Junte-se a +150k compradores</p>

              <div className="space-y-3.5">
                <div className="grid grid-cols-2 gap-3">
                  <div>
                    <label className="label">Nome</label>
                    <input {...registerForm.register('firstName')} className="input" placeholder="Joao" />
                  </div>
                  <div>
                    <label className="label">Sobrenome</label>
                    <input {...registerForm.register('lastName')} className="input" placeholder="Silva" />
                  </div>
                </div>
                <div>
                  <label className="label">E-mail</label>
                  <input {...registerForm.register('email')} type="email" className="input" placeholder="joao@email.com" />
                </div>
                <div>
                  <label className="label">Telefone (opcional)</label>
                  <input {...registerForm.register('phone')} type="tel" className="input" placeholder="(11) 99999-9999" />
                </div>
                <div>
                  <label className="label">Senha</label>
                  <input {...registerForm.register('password')} type="password" className="input" placeholder="Minimo 8 caracteres" />
                  {registerForm.formState.errors.password && (
                    <p className="text-xs text-red-500 mt-1">{registerForm.formState.errors.password.message}</p>
                  )}
                </div>
                <label className="flex items-start gap-2.5 cursor-pointer">
                  <input {...registerForm.register('terms')} type="checkbox" className="mt-1 rounded" />
                  <span className="text-xs text-[var(--text-secondary)] leading-relaxed">
                    Li e concordo com os{' '}
                    <Link href="/terms" className="text-nexus-500">Termos de Uso</Link>
                    {' '}e{' '}
                    <Link href="/privacy" className="text-nexus-500">Politica de Privacidade</Link>
                  </span>
                </label>
              </div>

              <button type="submit" disabled={registerMutation.isPending} className="btn-primary w-full mt-5 btn-lg font-display">
                {registerMutation.isPending ? 'Criando conta...' : 'Criar minha conta ->'}
              </button>

              <p className="text-center text-xs text-[var(--text-tertiary)] mt-4 leading-relaxed">
                Seus dados sao protegidos conforme a <Link href="/privacy" className="text-nexus-500">LGPD</Link>.
              </p>
            </form>
          )}
        </div>
      </div>
    </div>
  )
}
