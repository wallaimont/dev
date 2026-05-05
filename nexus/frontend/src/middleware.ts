// ============================================================
// middleware.ts — Auth route protection
// ============================================================
import { NextResponse } from 'next/server'
import type { NextRequest } from 'next/server'

// Routes that require authentication
const PROTECTED_ROUTES = [
  '/dashboard',
  '/orders',
  '/checkout',
  '/profile',
  '/favorites',
  '/messages',
  '/seller',
  '/admin',
]

// Routes that require specific roles
const ROLE_ROUTES: Record<string, string[]> = {
  '/seller': ['SELLER'],
  '/admin':  ['TENANT_ADMIN', 'SUPER_ADMIN'],
}

// Routes only for guests (redirect authenticated users)
const GUEST_ONLY_ROUTES = ['/login', '/register']

export function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl

  // Frontend auth is mirrored to cookies by the client store.
  const authToken = request.cookies.get('nexus_auth')?.value
  const roles = request.cookies.get('nexus_roles')?.value?.split(',').filter(Boolean) ?? []
  const isAuthenticated = !!authToken

  const getDefaultAuthenticatedRoute = () => (
    roles.includes('SELLER') ? '/seller/dashboard' : '/dashboard'
  )

  // Guest-only routes → redirect authenticated users to home
  if (GUEST_ONLY_ROUTES.some(r => pathname.startsWith(r)) && isAuthenticated) {
    return NextResponse.redirect(new URL(getDefaultAuthenticatedRoute(), request.url))
  }

  // Protected routes → redirect unauthenticated users to login
  const isProtected = PROTECTED_ROUTES.some(r => pathname.startsWith(r))
  if (isProtected && !isAuthenticated) {
    const loginUrl = new URL('/login', request.url)
    loginUrl.searchParams.set('redirect', pathname)
    return NextResponse.redirect(loginUrl)
  }

  for (const [routePrefix, requiredRoles] of Object.entries(ROLE_ROUTES)) {
    if (pathname.startsWith(routePrefix) && isAuthenticated && !roles.some(r => requiredRoles.includes(r))) {
      return NextResponse.redirect(new URL('/dashboard', request.url))
    }
  }

  return NextResponse.next()
}

export const config = {
  matcher: [
    '/((?!api|_next/static|_next/image|favicon.ico|.*\\.png$).*)',
  ],
}
