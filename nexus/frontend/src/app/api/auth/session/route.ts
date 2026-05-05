import { NextRequest, NextResponse } from 'next/server'
import { cookies } from 'next/headers'

const COOKIE_OPTIONS = {
  httpOnly: true,
  secure: process.env.NODE_ENV === 'production',
  sameSite: 'lax' as const,
  path: '/',
  maxAge: 60 * 60 * 24 * 30, // 30 days
}

/**
 * POST /api/auth/session — set httpOnly auth cookies after login/register
 * Body: { accessToken, refreshToken, roles }
 */
export async function POST(request: NextRequest) {
  const body = await request.json()
  const { accessToken, refreshToken, roles } = body

  if (!accessToken || !refreshToken || !Array.isArray(roles)) {
    return NextResponse.json({ error: 'Missing fields' }, { status: 400 })
  }

  const cookieStore = await cookies()

  cookieStore.set('nexus_access_token', accessToken, COOKIE_OPTIONS)
  cookieStore.set('nexus_refresh_token', refreshToken, COOKIE_OPTIONS)
  cookieStore.set('nexus_auth', '1', { ...COOKIE_OPTIONS, httpOnly: false }) // middleware reads this
  cookieStore.set('nexus_roles', roles.join(','), { ...COOKIE_OPTIONS, httpOnly: false }) // middleware reads this

  return NextResponse.json({ ok: true })
}

/**
 * DELETE /api/auth/session — clear all auth cookies on logout
 */
export async function DELETE() {
  const cookieStore = await cookies()
  const clearOpts = { ...COOKIE_OPTIONS, maxAge: 0 }

  cookieStore.set('nexus_access_token', '', clearOpts)
  cookieStore.set('nexus_refresh_token', '', clearOpts)
  cookieStore.set('nexus_auth', '', { ...clearOpts, httpOnly: false })
  cookieStore.set('nexus_roles', '', { ...clearOpts, httpOnly: false })

  return NextResponse.json({ ok: true })
}
