"use client";

import { Panel } from "@/components/ui/panel";
import { fetchProducts } from "@/lib/api";
import { useI18n } from "@/lib/i18n";
import { useQuery } from "@tanstack/react-query";

const metrics = [
  { label: "GMV anual projetado", value: "R$ 128M" },
  { label: "Sellers ativos", value: "4.280" },
  { label: "Pedidos por dia", value: "18.4k" },
  { label: "Latencia alvo", value: "< 180ms" }
];

export default function HomePage() {
  const { t } = useI18n();
  const { data: products = [] } = useQuery({
    queryKey: ["catalog-products"],
    queryFn: fetchProducts
  });

  return (
    <main className="mx-auto flex min-h-screen max-w-7xl flex-col gap-8 px-6 py-10">
      <section className="grid gap-6 lg:grid-cols-[1.3fr_0.7fr]">
        <div className="rounded-[36px] border border-white/70 bg-slate-950 px-8 py-10 text-white shadow-float">
          <p className="mb-3 text-sm uppercase tracking-[0.35em] text-amber-300">{t("common.appName")}</p>
          <h1 className="max-w-3xl text-4xl font-semibold leading-tight md:text-6xl">
            {t("home.title")}
          </h1>
          <p className="mt-6 max-w-2xl text-base text-slate-300 md:text-lg">
            {t("home.subtitle")}
          </p>
          <div className="mt-8 flex flex-wrap gap-3">
            <a className="rounded-full bg-amber-400 px-5 py-3 font-medium text-slate-950" href="/buyer">{t("home.buyer")}</a>
            <a className="rounded-full border border-white/20 px-5 py-3 font-medium text-white" href="/admin">{t("home.admin")}</a>
          </div>
        </div>
        <Panel title="Stack de producao" subtitle="Frontend, backend, mobile e infra alinhados">
          <div className="grid gap-3 text-sm text-slate-600">
            <p>Java 21, Spring Boot 3, PostgreSQL, Redis e Kafka</p>
            <p>Next.js, TypeScript, Tailwind, TanStack Query e Zustand</p>
            <p>React Native preparado para autenticacao, pedidos, chat e push</p>
            <p>Docker, Nginx, CI, health checks e arquitetura pronta para Kubernetes</p>
          </div>
        </Panel>
      </section>

      <section className="grid gap-5 md:grid-cols-2 xl:grid-cols-4">
        {metrics.map((item) => (
          <Panel key={item.label} title={item.value} subtitle={item.label} />
        ))}
      </section>

      <Panel title="Catalogo ao vivo" subtitle={`Produtos carregados da API: ${products.length}`}>
        <div className="grid gap-3 md:grid-cols-2">
          {products.map((product) => (
            <article key={product.id} className="rounded-2xl bg-slate-50 p-4">
              <p className="text-lg font-semibold text-slate-900">{product.name}</p>
              <p className="text-sm text-slate-500">{product.sku}</p>
              <p className="mt-2 text-sm font-medium text-slate-700">
                {product.currencyCode} {product.promotionalPrice ?? product.price}
              </p>
            </article>
          ))}
        </div>
      </Panel>
    </main>
  );
}
