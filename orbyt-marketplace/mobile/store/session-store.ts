import { create } from "zustand";
import { fetchCurrentUser, loginDemo } from "../lib/api";

export type SessionState = {
  accessToken: string | null;
  refreshToken: string | null;
  userId: string | null;
  email: string | null;
  isLoading: boolean;
  error: string | null;
  connectDemo: () => Promise<void>;
  logout: () => void;
};

export const useSessionStore = create<SessionState>((set) => ({
  accessToken: null,
  refreshToken: null,
  userId: null,
  email: null,
  isLoading: false,
  error: null,
  connectDemo: async () => {
    set({ isLoading: true, error: null });
    try {
      const session = await loginDemo();
      const me = await fetchCurrentUser(session.accessToken);
      set({
        accessToken: session.accessToken,
        refreshToken: session.refreshToken,
        userId: me.id,
        email: me.email,
        isLoading: false,
        error: null,
      });
    } catch (error) {
      set({
        isLoading: false,
        error: error instanceof Error ? error.message : "Falha ao conectar conta demo",
      });
    }
  },
  logout: () => set({
    accessToken: null,
    refreshToken: null,
    userId: null,
    email: null,
    isLoading: false,
    error: null,
  }),
}));