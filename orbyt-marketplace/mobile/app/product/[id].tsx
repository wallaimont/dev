import { useEffect, useState } from "react";
import { ScrollView, StyleSheet, Text, TouchableOpacity, View } from "react-native";
import { useLocalSearchParams, useRouter } from "expo-router";
import { addToCart, fetchProduct } from "../../lib/api";
import { useSessionStore } from "../../store/session-store";

type Product = {
  id: string;
  name: string;
  sku: string;
  price: number;
  promotionalPrice: number | null;
  currencyCode: string;
  approvalStatus: string;
};

export default function ProductDetailScreen() {
  const router = useRouter();
  const { id } = useLocalSearchParams<{ id: string }>();
  const accessToken = useSessionStore((state) => state.accessToken);
  const [product, setProduct] = useState<Product | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isAdding, setIsAdding] = useState(false);
  const [feedback, setFeedback] = useState<string | null>(null);

  useEffect(() => {
    if (!id) {
      setIsLoading(false);
      return;
    }

    setIsLoading(true);
    fetchProduct(id)
      .then(setProduct)
      .catch(() => setProduct(null))
      .finally(() => setIsLoading(false));
  }, [id]);

  async function handleAddToCart() {
    if (!product || !accessToken) {
      setFeedback("Conecte a conta demo antes de adicionar itens.");
      return;
    }

    setIsAdding(true);
    try {
      await addToCart(accessToken, product.id, 1);
      setFeedback("Produto adicionado ao carrinho.");
      router.push("/cart");
    } catch {
      setFeedback("Não foi possível adicionar o item ao carrinho.");
    } finally {
      setIsAdding(false);
    }
  }

  return (
    <ScrollView style={s.container} contentContainerStyle={s.content}>
      {isLoading ? (
        <View style={s.section}>
          <Text style={s.sectionTitle}>Carregando produto</Text>
          <Text style={s.sectionBody}>Buscando detalhes atualizados do catálogo.</Text>
        </View>
      ) : product ? (
        <>
          <View style={s.hero}>
            <Text style={s.heroIcon}>📦</Text>
          </View>

          <Text style={s.title}>{product.name}</Text>
          <Text style={s.sku}>SKU: {product.sku}</Text>
          <Text style={s.badge}>{product.approvalStatus}</Text>

          {product.promotionalPrice ? (
            <View style={s.priceBlock}>
              <Text style={s.oldPrice}>{product.currencyCode} {product.price.toFixed(2)}</Text>
              <Text style={s.pricePromo}>{product.currencyCode} {product.promotionalPrice.toFixed(2)}</Text>
            </View>
          ) : (
            <Text style={s.price}>{product.currencyCode} {product.price.toFixed(2)}</Text>
          )}

          <View style={s.section}>
            <Text style={s.sectionTitle}>Descrição</Text>
            <Text style={s.sectionBody}>Produto pronto para venda no fluxo mobile, com integração aos mesmos endpoints do catálogo web.</Text>
          </View>

          <View style={s.actionsRow}>
            <TouchableOpacity style={s.secondaryButton} onPress={() => router.push("/catalog") }>
              <Text style={s.secondaryButtonText}>Voltar ao catálogo</Text>
            </TouchableOpacity>
            <TouchableOpacity style={s.primaryButton} onPress={handleAddToCart} disabled={isAdding}>
              <Text style={s.primaryButtonText}>{isAdding ? "Adicionando..." : "Adicionar ao carrinho"}</Text>
            </TouchableOpacity>
          </View>
          {feedback ? <Text style={s.feedback}>{feedback}</Text> : null}
        </>
      ) : (
        <View style={s.section}>
          <Text style={s.sectionTitle}>Produto não encontrado</Text>
          <Text style={s.sectionBody}>Abra o catálogo novamente para selecionar um item válido.</Text>
        </View>
      )}
    </ScrollView>
  );
}

const s = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#f8fafc" },
  content: { padding: 16, paddingBottom: 28 },
  hero: { height: 260, borderRadius: 22, backgroundColor: "#e2e8f0", alignItems: "center", justifyContent: "center", marginBottom: 20 },
  heroIcon: { fontSize: 64 },
  title: { fontSize: 26, fontWeight: "700", color: "#0f172a" },
  sku: { fontSize: 13, color: "#64748b", marginTop: 6 },
  badge: { alignSelf: "flex-start", marginTop: 10, borderRadius: 999, backgroundColor: "#ecfdf5", color: "#059669", paddingHorizontal: 12, paddingVertical: 6, overflow: "hidden", fontSize: 12, fontWeight: "700" },
  priceBlock: { marginTop: 18 },
  oldPrice: { fontSize: 14, color: "#94a3b8", textDecorationLine: "line-through" },
  pricePromo: { fontSize: 30, color: "#059669", fontWeight: "700", marginTop: 4 },
  price: { fontSize: 30, color: "#0f172a", fontWeight: "700", marginTop: 18 },
  section: { backgroundColor: "white", borderRadius: 18, padding: 16, marginTop: 20 },
  sectionTitle: { fontSize: 16, fontWeight: "700", color: "#0f172a", marginBottom: 8 },
  sectionBody: { fontSize: 14, lineHeight: 21, color: "#334155" },
  actionsRow: { flexDirection: "row", gap: 12, marginTop: 20 },
  primaryButton: { flex: 1, borderRadius: 18, backgroundColor: "#f59e0b", paddingVertical: 16, alignItems: "center" },
  primaryButtonText: { color: "#0f172a", fontWeight: "700", fontSize: 16 },
  secondaryButton: { flex: 1, borderRadius: 18, backgroundColor: "#0f172a", paddingVertical: 16, alignItems: "center" },
  secondaryButtonText: { color: "white", fontWeight: "700", fontSize: 16 },
  feedback: { marginTop: 12, fontSize: 13, color: "#475569" },
});