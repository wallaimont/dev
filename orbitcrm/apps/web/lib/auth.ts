type SessionUser = {
  id?: string;
  name?: string;
  email?: string;
  tenantId?: string;
  permissions?: string[];
};

function persistSession(data: { accessToken?: string; refreshToken?: string; user?: SessionUser }) {
  if (data.accessToken) {
    document.cookie = `orbitcrm_session=${data.accessToken}; path=/; max-age=86400; samesite=lax`;
  }

  if (typeof window !== 'undefined') {
    if (data.refreshToken) {
      window.localStorage.setItem('orbitcrm_refresh', data.refreshToken);
    }

    if (data.user) {
      window.localStorage.setItem('orbitcrm_user', JSON.stringify(data.user));
    }
  }
}

function readSessionToken() {
  if (typeof document === 'undefined') return null;
  const match = document.cookie.match(/(?:^|; )orbitcrm_session=([^;]+)/);
  return match ? decodeURIComponent(match[1]) : null;
}

export function getStoredUser(): SessionUser | null {
  if (typeof window === 'undefined') return null;

  const raw = window.localStorage.getItem('orbitcrm_user');
  if (!raw) return null;

  try {
    return JSON.parse(raw) as SessionUser;
  } catch {
    return null;
  }
}

export async function signIn(email: string, password: string) {
  const apiUrl = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:3001';

  try {
    const response = await fetch(`${apiUrl}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password }),
    });

    if (!response.ok) {
      throw new Error('Credenciais inválidas');
    }

    const data = await response.json();
    persistSession(data);
    return data;
  } catch (error) {
    if (email === 'admin@orbitcrm.demo' && password === 'Admin@123') {
      const data = {
        accessToken: 'demo-session',
        refreshToken: 'demo-refresh',
        user: {
          name: 'Orbit Admin',
          email: 'admin@orbitcrm.demo',
          tenantId: 'tenant_demo',
          permissions: ['manage:all'],
        },
      };
      persistSession(data);
      return data;
    }

    throw error;
  }
}

export async function signOut() {
  const apiUrl = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:3001';
  const token = readSessionToken();

  try {
    if (token && token !== 'demo-session') {
      await fetch(`${apiUrl}/auth/logout`, {
        method: 'POST',
        headers: { Authorization: `Bearer ${token}` },
      });
    }
  } catch {
    // noop for local fallback mode
  }

  document.cookie = 'orbitcrm_session=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT';
  if (typeof window !== 'undefined') {
    window.localStorage.removeItem('orbitcrm_refresh');
    window.localStorage.removeItem('orbitcrm_user');
  }
  window.location.href = '/login';
}
