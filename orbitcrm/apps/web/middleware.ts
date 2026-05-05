import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

const protectedPaths = [
  '/dashboard',
  '/leads',
  '/accounts',
  '/contacts',
  '/opportunities',
  '/pipeline',
  '/activities',
  '/calendar',
  '/tickets',
  '/automations',
  '/integrations',
  '/reports',
  '/users',
  '/settings',
  '/billing',
  '/audit',
  '/notifications',
  '/profile',
];

export function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl;
  const requiresAuth = protectedPaths.some((path) => pathname.startsWith(path));

  if (!requiresAuth) return NextResponse.next();

  const session = request.cookies.get('orbitcrm_session');
  if (!session) {
    const loginUrl = new URL('/login', request.url);
    loginUrl.searchParams.set('next', pathname);
    return NextResponse.redirect(loginUrl);
  }

  return NextResponse.next();
}

export const config = {
  matcher: ['/dashboard/:path*', '/leads/:path*', '/accounts/:path*', '/contacts/:path*', '/opportunities/:path*', '/pipeline/:path*', '/activities/:path*', '/calendar/:path*', '/tickets/:path*', '/automations/:path*', '/reports/:path*', '/users/:path*', '/settings/:path*', '/billing/:path*', '/audit/:path*', '/integrations/:path*', '/notifications/:path*', '/profile/:path*'],
};
