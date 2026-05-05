'use client'

import { Navbar } from '@/components/layout/Navbar'
import { Sidebar } from '@/components/layout/Sidebar'

export default function SellerLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="min-h-screen bg-[var(--bg-page)]">
      <Sidebar variant="seller" />
      <div className="ml-[220px]">
        {children}
      </div>
    </div>
  )
}
