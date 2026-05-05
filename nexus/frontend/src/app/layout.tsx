// ============================================================
// app/layout.tsx — Root layout with providers
// ============================================================

import type { Metadata } from 'next'
import { Syne, DM_Sans, JetBrains_Mono } from 'next/font/google'
import { QueryProvider } from '@/components/providers/QueryProvider'
import { Toaster } from 'react-hot-toast'
import '@/styles/globals.css'

const syne = Syne({
  subsets: ['latin'],
  variable: '--font-syne',
  weight: ['600', '700', '800'],
})

const dmSans = DM_Sans({
  subsets: ['latin'],
  variable: '--font-dm-sans',
  weight: ['300', '400', '500', '600'],
})

const jetbrains = JetBrains_Mono({
  subsets: ['latin'],
  variable: '--font-jetbrains-mono',
  weight: ['400', '500'],
})

export const metadata: Metadata = {
  title: { default: 'Nexus Marketplace', template: '%s | Nexus' },
  description: 'Compre de milhares de vendedores verificados. PIX, cartão e boleto.',
  metadataBase: new URL(process.env.NEXT_PUBLIC_APP_URL ?? 'https://nexus.com.br'),
  openGraph: {
    type: 'website',
    locale: 'pt_BR',
    url: 'https://nexus.com.br',
    siteName: 'Nexus Marketplace',
  },
}

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="pt-BR" suppressHydrationWarning
      className={`${syne.variable} ${dmSans.variable} ${jetbrains.variable}`}>
      <body>
        <QueryProvider>
          {children}
          <Toaster
            position="bottom-right"
            toastOptions={{
              style: {
                background: '#0B0F1A',
                color: '#fff',
                borderRadius: '10px',
                fontSize: '13.5px',
                padding: '12px 16px',
              },
              success: { iconTheme: { primary: '#00D68F', secondary: '#fff' } },
              error:   { iconTheme: { primary: '#FF4B26', secondary: '#fff' } },
            }}
          />
        </QueryProvider>
      </body>
    </html>
  )
}
