import { ReactNode } from "react";

type PanelProps = {
  title: string;
  subtitle?: string;
  children?: ReactNode;
};

export function Panel({ title, subtitle, children }: PanelProps) {
  return (
    <section className="rounded-[28px] border border-white/60 bg-white/75 p-6 shadow-float backdrop-blur">
      <div className="mb-4">
        <h2 className="text-xl font-semibold text-slate-900">{title}</h2>
        {subtitle ? <p className="text-sm text-slate-500">{subtitle}</p> : null}
      </div>
      {children}
    </section>
  );
}
