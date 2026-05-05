import { useEffect, useState } from "react";
import { Text, View, ScrollView, StyleSheet, TouchableOpacity } from "react-native";
import { fetchMyOrders } from "../lib/api";
import { useSessionStore, type SessionState } from "../store/session-store";

const statusColors: Record<string, string> = {
  PENDING: "#eab308",
  PROCESSING: "#6366f1",
  SHIPPED: "#a855f7",
  DELIVERED: "#10b981",
  CANCELLED: "#ef4444"
};

export default function OrdersScreen() {
  const accessToken = useSessionStore((state: SessionState) => state.accessToken);
  const userId = useSessionStore((state: SessionState) => state.userId);
  const [orders, setOrders] = useState<Awaited<ReturnType<typeof fetchMyOrders>>>([]);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (!accessToken || !userId) {
      setOrders([]);
      return;
    }

    setIsLoading(true);
    fetchMyOrders(accessToken, userId)
      .then(setOrders)
      .finally(() => setIsLoading(false));
  }, [accessToken, userId]);

  return (
    <ScrollView style={s.container}>
      <Text style={s.title}>Meus Pedidos</Text>

      {!accessToken ? (
        <Text style={s.empty}>Entre com a conta demo para carregar seus pedidos.</Text>
      ) : isLoading ? (
        <Text style={s.empty}>Carregando pedidos...</Text>
      ) : orders.length === 0 ? (
        <Text style={s.empty}>Nenhum pedido encontrado para esta conta.</Text>
      ) : orders.map((group: Awaited<ReturnType<typeof fetchMyOrders>>[number]) => (
        <View key={group.id} style={s.card}>
          <View style={s.cardHeader}>
            <Text style={s.orderId}>#{group.id.slice(0, 8)}</Text>
            <View style={[s.badge, { backgroundColor: statusColors[group.status] ?? "#64748b" }]}>
              <Text style={s.badgeText}>{group.status}</Text>
            </View>
          </View>
          <Text style={s.date}>{new Date(group.createdAt).toLocaleDateString("pt-BR")} • {group.orders.length} pedidos</Text>
          <View style={s.cardFooter}>
            <Text style={s.total}>R$ {group.grandTotal.toFixed(2)}</Text>
            <TouchableOpacity>
              <Text style={s.link}>{group.paymentMethod}</Text>
            </TouchableOpacity>
          </View>
        </View>
      ))}
    </ScrollView>
  );
}

const s = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#f8fafc", padding: 16 },
  title: { fontSize: 24, fontWeight: "700", color: "#0f172a", marginBottom: 16 },
  card: {
    backgroundColor: "white",
    borderRadius: 16,
    padding: 16,
    marginBottom: 12,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.05,
    shadowRadius: 4,
    elevation: 1
  },
  cardHeader: { flexDirection: "row", justifyContent: "space-between", alignItems: "center" },
  orderId: { fontSize: 15, fontWeight: "600", color: "#0f172a" },
  badge: { borderRadius: 12, paddingHorizontal: 10, paddingVertical: 4 },
  badgeText: { color: "white", fontSize: 11, fontWeight: "700" },
  date: { fontSize: 13, color: "#64748b", marginTop: 6 },
  cardFooter: { flexDirection: "row", justifyContent: "space-between", alignItems: "center", marginTop: 12 },
  total: { fontSize: 18, fontWeight: "700", color: "#0f172a" },
  link: { fontSize: 13, color: "#f59e0b", fontWeight: "600" },
  empty: { fontSize: 14, color: "#64748b" }
});
