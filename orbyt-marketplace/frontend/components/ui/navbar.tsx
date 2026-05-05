"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import type { Route } from "next";
import { useAuthStore } from "@/store/auth-store";
import { useCartStore } from "@/store/cart-store";
import { useI18n } from "@/lib/i18n";

const links: { href: Route; labelKey: string }[] = [
  { href: "/", labelKey: "nav.home" },
  { href: "/catalog", labelKey: "nav.catalog" },
  { href: "/cart", labelKey: "nav.cart" },
  { href: "/buyer", labelKey: "nav.buyer" },
  { href: "/buyer/orders", labelKey: "nav.orders" },
  { href: "/buyer/chat", labelKey: "nav.chat" },
  { href: "/buyer/notifications", labelKey: "nav.notifications" },
  { href: "/buyer/support", labelKey: "nav.support" },
  { href: "/seller", labelKey: "nav.seller" },
  { href: "/seller/products", labelKey: "nav.products" },
  { href: "/admin", labelKey: "nav.admin" }
];

export function Navbar() {
  const { t } = useI18n();
  const pathname = usePathname();
  const { accessToken, clear } = useAuthStore();
  const itemCount = useCartStore((s) => s.itemCount);

  return (
    <nav className="sticky top-0 z-50 border-b border-slate-100 bg-white/80 backdrop-blur">
      <div className="mx-auto flex max-w-7xl items-center justify-between px-6 py-3">
        <Link href="/" className="text-lg font-bold text-slate-900">
          <span className="text-amber-500">Orbyt</span> Market
        </Link>

        <div className="flex items-center gap-1">
          {links.map((link) => (
            <Link
              key={link.href}
              href={link.href}
              className={`rounded-full px-3 py-1.5 text-sm font-medium transition ${
                pathname === link.href
                  ? "bg-amber-100 text-amber-700"
                  : "text-slate-600 hover:bg-slate-100"
              }`}
            >
              {t(link.labelKey)}
              {link.href === "/cart" && itemCount > 0 && (
                <span className="ml-1 inline-flex h-5 w-5 items-center justify-center rounded-full bg-amber-400 text-xs font-bold text-slate-900">
                  {itemCount}
                </span>
              )}
            </Link>
          ))}
        </div>

        <div>
          {accessToken ? (
            <button
              onClick={clear}
              className="rounded-full border border-slate-200 px-4 py-1.5 text-sm font-medium text-slate-600 hover:bg-slate-50"
            >
              {t("common.logout")}
            </button>
          ) : (
            <Link
              href="/auth/login"
              className="rounded-full bg-slate-900 px-4 py-1.5 text-sm font-medium text-white"
            >
              {t("common.login")}
            </Link>
          )}
        </div>
      </div>
    </nav>
  );
}
