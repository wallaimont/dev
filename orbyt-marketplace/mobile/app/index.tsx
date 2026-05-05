import { Text, View, ScrollView, TouchableOpacity, StyleSheet } from "react-native";
import { useRouter } from "expo-router";
import { useSessionStore } from "../store/session-store";

const menuItems = [
  { label: "Catálogo", route: "/catalog", icon: "🛍️" },
  { label: "Carrinho", route: "/cart", icon: "🛒" },
  { label: "Checkout", route: "/checkout", icon: "💳" },
  { label: "Meus Pedidos", route: "/orders", icon: "📦" },
  { label: "Chat", route: "/chat", icon: "💬" },
  { label: "Notificações", route: "/notifications", icon: "🔔" },
  { label: "Suporte", route: "/support", icon: "💬" },
  { label: "Meu Perfil", route: "/profile", icon: "👤" }
];

export default function App() {
  const router = useRouter();
  const { accessToken, email, isLoading, error, connectDemo, logout } = useSessionStore();

  return (
    <ScrollView style={s.container}>
      <View style={s.hero}>
        <Text style={s.brand}>Orbyt Market</Text>
        <Text style={s.title}>Marketplace enterprise no seu bolso</Text>
        <Text style={s.subtitle}>
          Catálogo, carrinho, pedidos, chat, favoritos e rastreio — tudo via a mesma API multi-tenant.
        </Text>
        <View style={s.sessionCard}>
          <Text style={s.sessionTitle}>{accessToken ? "Sessão demo ativa" : "Sessão desconectada"}</Text>
          <Text style={s.sessionBody}>
            {accessToken ? email ?? "Conta autenticada" : "Conecte a conta demo para usar carrinho, chat e checkout reais."}
          </Text>
          <TouchableOpacity
            style={s.sessionButton}
            onPress={accessToken ? logout : connectDemo}
            disabled={isLoading}
          >
            <Text style={s.sessionButtonText}>{isLoading ? "Conectando..." : accessToken ? "Sair" : "Entrar com demo"}</Text>
          </TouchableOpacity>
          {error ? <Text style={s.sessionError}>{error}</Text> : null}
        </View>
      </View>

      <View style={s.grid}>
        {menuItems.map((item) => (
          <TouchableOpacity
            key={item.route}
            style={s.card}
            onPress={() => router.push(item.route as any)}
          >
            <Text style={s.cardIcon}>{item.icon}</Text>
            <Text style={s.cardLabel}>{item.label}</Text>
          </TouchableOpacity>
        ))}
      </View>

      <View style={s.statsRow}>
        <View style={s.stat}>
          <Text style={s.statValue}>12</Text>
          <Text style={s.statLabel}>Pedidos</Text>
        </View>
        <View style={s.stat}>
          <Text style={s.statValue}>48</Text>
          <Text style={s.statLabel}>Favoritos</Text>
        </View>
        <View style={s.stat}>
          <Text style={s.statValue}>4</Text>
          <Text style={s.statLabel}>Cupons</Text>
        </View>
      </View>
    </ScrollView>
  );
}

const s = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#f8fafc" },
  hero: { backgroundColor: "#0f172a", padding: 24, paddingTop: 16, paddingBottom: 32 },
  brand: { color: "#f59e0b", fontSize: 12, textTransform: "uppercase", letterSpacing: 4 },
  title: { color: "white", fontSize: 28, fontWeight: "700", marginTop: 12 },
  subtitle: { color: "#94a3b8", fontSize: 14, marginTop: 8, lineHeight: 20 },
  sessionCard: { marginTop: 18, borderRadius: 18, backgroundColor: "rgba(255,255,255,0.08)", padding: 16 },
  sessionTitle: { color: "white", fontSize: 16, fontWeight: "700" },
  sessionBody: { color: "#cbd5e1", fontSize: 13, lineHeight: 19, marginTop: 6 },
  sessionButton: { marginTop: 12, alignSelf: "flex-start", borderRadius: 999, backgroundColor: "#f59e0b", paddingHorizontal: 14, paddingVertical: 10 },
  sessionButtonText: { color: "#0f172a", fontSize: 13, fontWeight: "700" },
  sessionError: { color: "#fecaca", fontSize: 12, marginTop: 8 },
  grid: { flexDirection: "row", flexWrap: "wrap", padding: 16, gap: 12 },
  card: {
    width: "47%",
    backgroundColor: "white",
    borderRadius: 16,
    padding: 20,
    alignItems: "center",
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.05,
    shadowRadius: 8,
    elevation: 2
  },
  cardIcon: { fontSize: 32, marginBottom: 8 },
  cardLabel: { fontSize: 14, fontWeight: "600", color: "#1e293b" },
  statsRow: {
    flexDirection: "row",
    justifyContent: "space-around",
    backgroundColor: "white",
    marginHorizontal: 16,
    borderRadius: 16,
    padding: 20,
    marginTop: 4,
    marginBottom: 24
  },
  stat: { alignItems: "center" },
  statValue: { fontSize: 24, fontWeight: "700", color: "#0f172a" },
  statLabel: { fontSize: 12, color: "#64748b", marginTop: 4 }
});
