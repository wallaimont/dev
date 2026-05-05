"use client";

import { Panel } from "@/components/ui/panel";
import { fetchNotifications, markNotificationRead } from "@/lib/api";
import { useNotificationSocket } from "@/lib/hooks/use-notification-socket";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";

export default function NotificationsPage() {
  const queryClient = useQueryClient();

  const { data: notifications = [], isLoading } = useQuery({
    queryKey: ["notifications"],
    queryFn: () => fetchNotifications()
  });

  useNotificationSocket(() => {
    queryClient.invalidateQueries({ queryKey: ["notifications"] });
  });

  const markReadMutation = useMutation({
    mutationFn: markNotificationRead,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["notifications"] })
  });

  const unread = notifications.filter((n) => !n.readAt);
  const read = notifications.filter((n) => n.readAt);

  return (
    <main className="mx-auto min-h-screen max-w-3xl px-6 py-10">
      <h1 className="mb-8 text-3xl font-bold text-slate-900">Notificações</h1>

      {isLoading ? (
        <div className="flex justify-center py-20">
          <div className="h-8 w-8 animate-spin rounded-full border-4 border-amber-400 border-t-transparent" />
        </div>
      ) : (
        <>
          {unread.length > 0 && (
            <div className="mb-8">
              <h2 className="mb-4 text-sm font-medium uppercase tracking-wider text-amber-600">
                Não lidas ({unread.length})
              </h2>
              <div className="space-y-3">
                {unread.map((n) => (
                  <div
                    key={n.id}
                    className="flex items-start justify-between rounded-2xl border-l-4 border-l-amber-400 bg-amber-50 p-4"
                  >
                    <div>
                      <p className="font-semibold text-slate-900">{n.title}</p>
                      <p className="text-sm text-slate-600">{n.body}</p>
                      <p className="mt-1 text-xs text-slate-400">
                        {new Date(n.createdAt).toLocaleString("pt-BR")}
                      </p>
                    </div>
                    <button
                      onClick={() => markReadMutation.mutate(n.id)}
                      className="shrink-0 rounded-full bg-white px-3 py-1.5 text-xs font-medium text-slate-600 hover:bg-slate-100"
                    >
                      Marcar lida
                    </button>
                  </div>
                ))}
              </div>
            </div>
          )}

          {read.length > 0 && (
            <div>
              <h2 className="mb-4 text-sm font-medium uppercase tracking-wider text-slate-400">
                Anteriores
              </h2>
              <div className="space-y-3">
                {read.map((n) => (
                  <div key={n.id} className="rounded-2xl bg-white p-4">
                    <p className="font-medium text-slate-700">{n.title}</p>
                    <p className="text-sm text-slate-500">{n.body}</p>
                    <p className="mt-1 text-xs text-slate-400">
                      {new Date(n.createdAt).toLocaleString("pt-BR")}
                    </p>
                  </div>
                ))}
              </div>
            </div>
          )}

          {notifications.length === 0 && (
            <Panel title="Sem notificações" subtitle="Você está em dia!" />
          )}
        </>
      )}
    </main>
  );
}
