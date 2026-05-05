import { forwardRef, ButtonHTMLAttributes } from 'react'
import { cva, type VariantProps } from 'class-variance-authority'
import { cn } from '@/lib/utils'

const buttonVariants = cva(
  'inline-flex items-center justify-center gap-2 font-medium rounded-[10px] ' +
  'transition-all duration-150 select-none disabled:opacity-50 disabled:cursor-not-allowed ' +
  'active:scale-[0.98]',
  {
    variants: {
      variant: {
        primary:   'bg-nexus-500 text-white hover:bg-nexus-600 shadow-[0_1px_2px_rgba(26,47,232,0.3)]',
        secondary: 'bg-white text-nexus-900 border border-[var(--border-default)] hover:bg-[var(--bg-subtle)]',
        accent:    'bg-accent-500 text-white hover:bg-accent-600',
        ghost:     'text-nexus-700 hover:bg-nexus-50',
        danger:    'bg-red-500 text-white hover:bg-red-600',
        outline:   'border border-nexus-300 text-nexus-700 hover:bg-nexus-50',
      },
      size: {
        sm:   'px-3.5 py-1.5 text-xs rounded-[8px]',
        md:   'px-5 py-2.5 text-sm',
        lg:   'px-7 py-3.5 text-base rounded-[12px]',
        icon: 'w-9 h-9 rounded-[8px] p-0',
      },
    },
    defaultVariants: { variant: 'primary', size: 'md' },
  }
)

export interface ButtonProps
  extends ButtonHTMLAttributes<HTMLButtonElement>,
    VariantProps<typeof buttonVariants> {
  isLoading?: boolean
}

export const Button = forwardRef<HTMLButtonElement, ButtonProps>(
  ({ className, variant, size, isLoading, children, disabled, ...props }, ref) => (
    <button
      ref={ref}
      disabled={disabled || isLoading}
      className={cn(buttonVariants({ variant, size }), className)}
      {...props}
    >
      {isLoading && (
        <svg className="animate-spin h-4 w-4" viewBox="0 0 24 24" fill="none">
          <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="3" strokeOpacity=".25"/>
          <path d="M12 2a10 10 0 0 1 10 10" stroke="currentColor" strokeWidth="3" strokeLinecap="round"/>
        </svg>
      )}
      {children}
    </button>
  )
)
Button.displayName = 'Button'
