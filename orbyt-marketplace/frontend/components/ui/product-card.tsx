import Link from "next/link";
import { Product } from "@/lib/api";

type ProductCardProps = {
  product: Product;
  href?: string;
  hrefLabel?: string;
  actionLabel?: string;
  actionDisabled?: boolean;
  onAction?: () => void;
};

export function ProductCard({
  product,
  href,
  hrefLabel = "Ver produto",
  actionLabel,
  actionDisabled,
  onAction,
}: ProductCardProps) {
  return (
    <article className="rounded-[28px] border border-white/60 bg-white/75 p-6 shadow-float backdrop-blur">
      <div className="mb-4 flex items-start justify-between gap-4">
        <div>
          {href ? (
            <Link href={href} className="text-lg font-semibold text-slate-900 transition hover:text-amber-600">
              {product.name}
            </Link>
          ) : (
            <h2 className="text-lg font-semibold text-slate-900">{product.name}</h2>
          )}
          <p className="mt-1 text-sm text-slate-500">{product.sku}</p>
        </div>
        <span className={`rounded-full px-2.5 py-1 text-xs font-semibold ${
          product.approvalStatus === "APPROVED" ? "bg-emerald-50 text-emerald-600" : "bg-amber-50 text-amber-600"
        }`}>
          {product.approvalStatus}
        </span>
      </div>

      <div className="flex items-end justify-between gap-4">
        <div>
          {product.promotionalPrice ? (
            <>
              <p className="text-xs text-slate-400 line-through">
                {product.currencyCode} {product.price.toFixed(2)}
              </p>
              <p className="text-lg font-bold text-emerald-600">
                {product.currencyCode} {product.promotionalPrice.toFixed(2)}
              </p>
            </>
          ) : (
            <p className="text-lg font-bold text-slate-800">
              {product.currencyCode} {product.price.toFixed(2)}
            </p>
          )}
        </div>

        <div className="flex items-center gap-2">
          {href ? (
            <Link
              href={href}
              className="rounded-full border border-slate-200 px-4 py-2 text-sm font-medium text-slate-700 transition hover:border-amber-300 hover:text-amber-600"
            >
              {hrefLabel}
            </Link>
          ) : null}
          {actionLabel && onAction ? (
            <button
              onClick={onAction}
              disabled={actionDisabled}
              className="rounded-full bg-amber-400 px-4 py-2 text-sm font-medium text-slate-900 transition hover:bg-amber-500 disabled:opacity-50"
            >
              {actionLabel}
            </button>
          ) : null}
        </div>
      </div>
    </article>
  );
}