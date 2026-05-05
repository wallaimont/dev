import "./globals.css";
import type { Metadata } from "next";
import { QueryProvider } from "@/components/providers/query-provider";
import { Navbar } from "@/components/ui/navbar";

export const metadata: Metadata = {
  title: "Orbyt Market",
  description: "Marketplace enterprise multi-tenant premium"
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="pt-BR">
      <body>
        <QueryProvider>
          <Navbar />
          {children}
        </QueryProvider>
      </body>
    </html>
  );
}
