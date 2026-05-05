import { cn } from '@/lib/utils'

type BadgeVariant = 'primary' | 'success' | 'warning' | 'danger' | 'neutral' | 'accent'

interface BadgeProps {
  variant?: BadgeVariant
  children: React.ReactNode
  className?: string
}

const badgeVariants: Record<BadgeVariant, string> = {
  primary: 'bg-nexus-50 text-nexus-700',
  success: 'bg-green-50 text-green-700',
  warning: 'bg-amber-50 text-amber-700',
  danger:  'bg-red-50 text-red-600',
  neutral: 'bg-gray-100 text-gray-700',
  accent:  'bg-accent-50 text-accent-700',
}

export function Badge({ variant = 'neutral', children, className }: BadgeProps) {
  return (
    <span className={cn(
      'inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-medium',
      badgeVariants[variant], className
    )}>
      {children}
    </span>
  )
}

// ---- Status badge utility ----
export function OrderStatusBadge({ status }: { status: string }) {
  const map: Record<string, BadgeVariant> = {
    PENDING:    'warning',
    CONFIRMED:  'primary',
    PROCESSING: 'primary',
    SHIPPED:    'primary',
    DELIVERED:  'success',
    CANCELLED:  'danger',
    REFUNDED:   'neutral',
    PAID:       'success',
    FAILED:     'danger',
  }
  const labelMap: Record<string, string> = {
    PENDING:    'Aguardando',
    CONFIRMED:  'Confirmado',
    PROCESSING: 'Processando',
    SHIPPED:    'Enviado',
    DELIVERED:  'Entregue',
    CANCELLED:  'Cancelado',
    REFUNDED:   'Reembolsado',
    PAID:       'Pago',
    FAILED:     'Falhou',
  }
  return <Badge variant={map[status] ?? 'neutral'}>{labelMap[status] ?? status}</Badge>
}
