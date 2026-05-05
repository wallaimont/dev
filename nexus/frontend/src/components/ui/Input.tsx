import { forwardRef, InputHTMLAttributes } from 'react'
import { cn } from '@/lib/utils'

export interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  label?: string
  error?: string
  hint?: string
}

export const Input = forwardRef<HTMLInputElement, InputProps>(
  ({ className, label, error, hint, id, ...props }, ref) => {
    const inputId = id ?? label?.toLowerCase().replace(/\s+/g, '-')

    return (
      <div className="w-full">
        {label && (
          <label htmlFor={inputId} className="block text-sm font-medium text-[var(--text-secondary)] mb-1.5">
            {label}
          </label>
        )}
        <input
          id={inputId}
          ref={ref}
          className={cn(
            'w-full px-3.5 py-2.5 text-sm bg-[var(--bg-input)] border rounded-[10px]',
            'text-[var(--text-primary)] placeholder-[var(--text-tertiary)]',
            'focus:outline-none focus:ring-2 transition-colors duration-150',
            error
              ? 'border-red-300 focus:border-red-400 focus:ring-red-100'
              : 'border-[var(--border-light)] focus:border-nexus-400 focus:ring-nexus-100',
            className
          )}
          {...props}
        />
        {error && <p className="mt-1.5 text-xs text-red-500">{error}</p>}
        {hint && !error && <p className="mt-1.5 text-xs text-[var(--text-tertiary)]">{hint}</p>}
      </div>
    )
  }
)
Input.displayName = 'Input'
