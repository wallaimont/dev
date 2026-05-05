import { OrderSummary } from "@/lib/api";

const statusColors: Record<string, string> = {
  PENDING: "bg-yellow-100 text-yellow-700",
  PAID: "bg-blue-100 text-blue-700",
  PROCESSING: "bg-indigo-100 text-indigo-700",
  SHIPPED: "bg-purple-100 text-purple-700",
  DELIVERED: "bg-emerald-100 text-emerald-700",
  CANCELLED: "bg-red-100 text-red-700",
};

type OrderCardProps = {
  order: OrderSummary;
  title?: string;
  showTracking?: boolean;
};

export function OrderCard({ order, title, showTracking = true }: OrderCardProps) {
  return (
    <div className="rounded-2xl border border-slate-100 bg-slate-50 p-4">
      <div className="mb-2 flex items-center justify-between gap-4">
        <div>
          <p className="font-semibold text-slate-900">{title ?? `Pedido #${order.id.slice(0, 8)}`}</p>
          <span
            className={`mt-1 inline-flex rounded-full px-3 py-1 text-xs font-medium ${
              statusColors[order.status] ?? "bg-slate-100 text-slate-600"
            }`}
          >
            {order.status}
          </span>
        </div>
        <span className="text-sm font-bold text-slate-800">R$ {order.total.toFixed(2)}</span>
      </div>

      <div className="space-y-1">
        {order.items.map((item, idx) => (
          <p key={`${order.id}-${idx}`} className="text-sm text-slate-600">
            {item.productName} x{item.quantity} — R$ {item.unitPrice.toFixed(2)}
          </p>
        ))}
      </div>

      {showTracking && order.trackingCode ? (
        <p className="mt-2 text-xs text-slate-400">Rastreio: {order.trackingCode}</p>
      ) : null}
    </div>
  );
}