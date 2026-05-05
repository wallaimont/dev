'use client'

import { Sidebar } from '@/components/layout/Sidebar'

export default function AdminLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="min-h-screen bg-[var(--bg-page)]">
      <Sidebar variant="admin" />
      <div className="ml-[220px]">
        {children}
      </div>
    </div>
  )
}
