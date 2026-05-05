import { useEffect, useState } from "react";
import { Text, View, ScrollView, StyleSheet, TouchableOpacity } from "react-native";
import { fetchNotifications, markAllNotificationsAsRead, markNotificationAsRead } from "../lib/api";
import { useSessionStore, type SessionState } from "../store/session-store";

export default function NotificationsScreen() {
  const accessToken = useSessionStore((state: SessionState) => state.accessToken);
  const [notifications, setNotifications] = useState<Awaited<ReturnType<typeof fetchNotifications>>>([]);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (!accessToken) {
      setNotifications([]);
      return;
    }

    setIsLoading(true);
    fetchNotifications(accessToken)
      .then(setNotifications)
      .finally(() => setIsLoading(false));
  }, [accessToken]);

  async function handleRead(notificationId: string) {
    if (!accessToken) {
      return;
    }
    await markNotificationAsRead(accessToken, notificationId);
    setNotifications((current) => current.map((item) => item.id === notificationId ? { ...item, isRead: true } : item));
  }

  async function handleReadAll() {
    if (!accessToken) {
      return;
    }
    await markAllNotificationsAsRead(accessToken);
    setNotifications((current) => current.map((item) => ({ ...item, isRead: true })));
  }

  return (
    <ScrollView style={s.container}>
      <View style={s.header}>
        <Text style={s.title}>Notificações</Text>
        {accessToken && notifications.length > 0 ? (
          <TouchableOpacity onPress={handleReadAll}>
            <Text style={s.readAll}>Marcar todas</Text>
          </TouchableOpacity>
        ) : null}
      </View>

      {!accessToken ? (
        <Text style={s.empty}>Entre com a conta demo para carregar as notificações.</Text>
      ) : isLoading ? (
        <Text style={s.empty}>Carregando notificações...</Text>
      ) : notifications.length === 0 ? (
        <Text style={s.empty}>Nenhuma notificação encontrada.</Text>
      ) : (
        notifications.map((notification) => (
          <TouchableOpacity
            key={notification.id}
            style={[s.card, !notification.isRead && s.unread]}
            onPress={() => !notification.isRead ? handleRead(notification.id) : undefined}
          >
            {!notification.isRead && <View style={s.dot} />}
            <View style={s.content}>
              <Text style={s.notifTitle}>{notification.title}</Text>
              <Text style={s.notifMsg}>{notification.body}</Text>
              <Text style={s.time}>{new Date(notification.createdAt).toLocaleString("pt-BR")}</Text>
            </View>
          </TouchableOpacity>
        ))
      )}
    </ScrollView>
  );
}

const s = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#f8fafc", padding: 16 },
  header: { flexDirection: "row", justifyContent: "space-between", alignItems: "center", marginBottom: 16 },
  title: { fontSize: 24, fontWeight: "700", color: "#0f172a", marginBottom: 16 },
  readAll: { fontSize: 13, color: "#f59e0b", fontWeight: "700" },
  card: {
    backgroundColor: "white",
    borderRadius: 16,
    padding: 16,
    marginBottom: 10,
    flexDirection: "row",
    alignItems: "flex-start"
  },
  unread: { borderLeftWidth: 3, borderLeftColor: "#f59e0b" },
  dot: {
    width: 8,
    height: 8,
    borderRadius: 4,
    backgroundColor: "#f59e0b",
    marginTop: 6,
    marginRight: 12
  },
  content: { flex: 1 },
  notifTitle: { fontSize: 15, fontWeight: "600", color: "#0f172a" },
  notifMsg: { fontSize: 13, color: "#64748b", marginTop: 4 },
  time: { fontSize: 11, color: "#94a3b8", marginTop: 6 },
  empty: { fontSize: 14, color: "#64748b" }
});
