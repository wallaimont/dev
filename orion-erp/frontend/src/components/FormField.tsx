import clsx from 'clsx';
import type { InputHTMLAttributes, SelectHTMLAttributes, TextareaHTMLAttributes } from 'react';

interface BaseProps {
  label: string;
  error?: string;
}

type InputProps = BaseProps & InputHTMLAttributes<HTMLInputElement>;
type SelectProps = BaseProps & SelectHTMLAttributes<HTMLSelectElement> & { options: { label: string; value: string | number }[] };
type TextAreaProps = BaseProps & TextareaHTMLAttributes<HTMLTextAreaElement>;

const base = 'w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500';

export function InputField({ label, error, className, ...rest }: InputProps) {
  return (
    <div className={className}>
      <label className="mb-1 block text-sm font-medium text-gray-700">{label}</label>
      <input {...rest} className={clsx(base, error && 'border-red-400')} />
      {error && <p className="mt-1 text-xs text-red-500">{error}</p>}
    </div>
  );
}

export function SelectField({ label, error, options, className, ...rest }: SelectProps) {
  return (
    <div className={className}>
      <label className="mb-1 block text-sm font-medium text-gray-700">{label}</label>
      <select {...rest} className={clsx(base, error && 'border-red-400')}>
        <option value="">Selecione...</option>
        {options.map((o) => (
          <option key={o.value} value={o.value}>{o.label}</option>
        ))}
      </select>
      {error && <p className="mt-1 text-xs text-red-500">{error}</p>}
    </div>
  );
}

export function TextAreaField({ label, error, className, ...rest }: TextAreaProps) {
  return (
    <div className={className}>
      <label className="mb-1 block text-sm font-medium text-gray-700">{label}</label>
      <textarea {...rest} rows={3} className={clsx(base, error && 'border-red-400')} />
      {error && <p className="mt-1 text-xs text-red-500">{error}</p>}
    </div>
  );
}
