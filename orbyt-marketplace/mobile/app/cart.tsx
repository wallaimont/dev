import { useEffect, useMemo, useState } from "react";
import { Text, View, ScrollView, StyleSheet, TouchableOpacity } from "react-native";
import { fetchCart, removeFromCart, fetchProducts } from "../lib/api";
import { useSessionStore, type SessionState } from "../store/session-store";

export default function CartScreen() {
  const accessToken = useSessionStore((state: SessionState) => state.accessToken);
  const [cart, setCart] = useState<Awaited<ReturnType<typeof fetchCart>> | null>(null);
  const [products, setProducts] = useState<Awaited<ReturnType<typeof fetchProducts>>>([]);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (!accessToken) {
      setCart(null);
      return;
    }

    setIsLoading(true);
    Promise.all([fetchCart(accessToken), fetchProducts()])
      .then(([cartData, productData]) => {
        setCart(cartData);
        setProducts(productData);
      })
      .finally(() => setIsLoading(false));
  }, [accessToken]);

  const productMap = useMemo(
    () => new Map(products.map((product: Awaited<ReturnType<typeof fetchProducts>>[number]) => [product.id, product.name])),
    [products]
  );

  async function handleRemove(itemId: string) {
    if (!accessToken) {
      return;
    }
    const updated = await removeFromCart(accessToken, itemId);
    setCart(updated);
  }

  return (
    <ScrollView style={s.container}>
      <Text style={s.title}>Carrinho</Text>

      {!accessToken ? (
        <View style={s.emptyCard}>
          <Text style={s.emptyTitle}>Faça login na conta demo</Text>
          <Text style={s.emptyText}>O carrinho real depende do token de autenticação do backend.</Text>
        </View>
      ) : isLoading ? (
        <Text style={s.emptyText}>Carregando carrinho...</Text>
      ) : !cart || cart.items.length === 0 ? (
        <View style={s.emptyCard}>
          <Text style={s.emptyTitle}>Carrinho vazio</Text>
          <Text style={s.emptyText}>Adicione produtos no catálogo para avançar ao checkout.</Text>
        </View>
      ) : (
        <>
          {cart.items.map((item: NonNullable<typeof cart>["items"][number]) => (
            <View key={item.id} style={s.card}>
              <View style={s.cardTextWrap}>
                <Text style={s.productName}>{productMap.get(item.productId) ?? `Produto ${item.productId.slice(0, 8)}`}</Text>
                <Text style={s.detail}>{item.quantity}x R$ {item.unitPrice.toFixed(2)}</Text>
              </View>
              <TouchableOpacity onPress={() => handleRemove(item.id)}>
                <Text style={s.removeLink}>Remover</Text>
              </TouchableOpacity>
            </View>
          ))}

          <View style={s.totalBar}>
            <Text style={s.totalLabel}>Total</Text>
            <Text style={s.totalValue}>R$ {cart.total.toFixed(2)}</Text>
          </View>

          <View style={s.checkoutHint}>
            <Text style={s.checkoutHintText}>Abra Checkout pelo atalho da tela inicial para confirmar o pedido.</Text>
          </View>
        </>
      )}
    </ScrollView>
  );
}

const s = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#f8fafc", padding: 16 },
  title: { fontSize: 24, fontWeight: "700", color: "#0f172a", marginBottom: 16 },
  emptyCard: { backgroundColor: "white", borderRadius: 16, padding: 16 },
  emptyTitle: { fontSize: 16, fontWeight: "700", color: "#0f172a" },
  emptyText: { fontSize: 14, color: "#64748b", marginTop: 8 },
  card: {
    backgroundColor: "white",
    borderRadius: 16,
    padding: 16,
    marginBottom: 10,
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center"
  },
  cardTextWrap: { flex: 1, paddingRight: 12 },
  productName: { fontSize: 15, fontWeight: "600", color: "#0f172a" },
  detail: { fontSize: 14, color: "#64748b" },
  removeLink: { fontSize: 13, color: "#ef4444", fontWeight: "600" },
  totalBar: {
    flexDirection: "row",
    justifyContent: "space-between",
    backgroundColor: "#0f172a",
    borderRadius: 16,
    padding: 20,
    marginTop: 16
  },
  totalLabel: { fontSize: 16, color: "#94a3b8" },
  totalValue: { fontSize: 22, fontWeight: "700", color: "white" },
  checkoutBtn: {
    backgroundColor: "#f59e0b",
    borderRadius: 16,
    padding: 16,
    alignItems: "center",
    marginTop: 12
  },
  checkoutText: { fontSize: 16, fontWeight: "700", color: "#0f172a" },
  checkoutHint: { backgroundColor: "#fff7ed", borderRadius: 16, padding: 14, marginTop: 12 },
  checkoutHintText: { fontSize: 13, color: "#9a3412", lineHeight: 18 }
});
