'use client'

import { useState } from 'react'
import Link from 'next/link'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useMutation } from '@tanstack/react-query'
import toast from 'react-hot-toast'
import { authApi } from '@/lib/api/client'
import { parseApiError } from '@/lib/utils'

const schema = z.object({
  email: z.string().email('E-mail inválido'),
})

type ForgotForm = z.infer<typeof schema>

export default function ForgotPasswordPage() {
  const [sent, setSent] = useState(false)
  const form = useForm<ForgotForm>({ resolver: zodResolver(schema) })

  const mutation = useMutation({
    mutationFn: (data: ForgotForm) => authApi.forgotPassword(data.email),
    onSuccess: () => setSent(true),
    onError: (error) => toast.error(parseApiError(error)),
  })

  return (
    <div className="min-h-screen flex items-center justify-center bg-[var(--bg-page)] px-4">
      <div className="w-full max-w-[420px] bg-white rounded-[20px] border border-[var(--border-light)] p-8 shadow-[var(--shadow-card)]">
        <Link href="/login" className="text-sm text-nexus-500 font-medium">← Voltar ao login</Link>

        {sent ? (
          <div className="mt-6">
            <h1 className="font-display text-2xl font-800 tracking-tight">E-mail enviado</h1>
            <p className="text-[var(--text-secondary)] text-sm mt-2 leading-relaxed">
              Se o e-mail informado estiver cadastrado, você receberá um link para redefinir sua senha em instantes.
            </p>
            <Link href="/login" className="btn-primary w-full mt-6 btn-lg font-display inline-block text-center">
              Voltar ao login
            </Link>
          </div>
        ) : (
          <div className="mt-6">
            <h1 className="font-display text-2xl font-800 tracking-tight">Esqueceu a senha?</h1>
            <p className="text-[var(--text-secondary)] text-sm mt-2 mb-6">
              Informe seu e-mail e enviaremos um link para redefinição.
            </p>
            <form onSubmit={form.handleSubmit((d) => mutation.mutate(d))}>
              <div>
                <label className="label">E-mail</label>
                <input {...form.register('email')} className="input" placeholder="seu@email.com" />
                {form.formState.errors.email && (
                  <p className="text-xs text-red-500 mt-1">{form.formState.errors.email.message}</p>
                )}
              </div>
              <button type="submit" disabled={mutation.isPending} className="btn-primary w-full mt-5 btn-lg font-display">
                {mutation.isPending ? 'Enviando...' : 'Enviar link de redefinição'}
              </button>
            </form>
          </div>
        )}
      </div>
    </div>
  )
}
