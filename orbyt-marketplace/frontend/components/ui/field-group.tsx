import { ReactNode } from "react";

type FieldGroupProps = {
  label: string;
  children: ReactNode;
};

export function FieldGroup({ label, children }: FieldGroupProps) {
  return (
    <div>
      <label className="mb-1 block text-sm font-medium text-slate-700">{label}</label>
      {children}
    </div>
  );
}