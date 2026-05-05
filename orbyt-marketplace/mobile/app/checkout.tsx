import { useEffect, useState } from "react";
import { ScrollView, StyleSheet, Text, TouchableOpacity, View } from "react-native";
import { useRouter } from "expo-router";
import { checkoutCart, fetchCart, previewCheckout } from "../lib/api";
import { useSessionStore } from "../store/session-store";

const paymentMethods = ["PIX", "Cartão de crédito", "Boleto"];

export default function CheckoutScreen() {
  const router = useRouter();
  const accessToken = useSessionStore((state) => state.accessToken);
  const [selectedMethod, setSelectedMethod] = useState(paymentMethods[0]);
  const [cart, setCart] = useState<Awaited<ReturnType<typeof fetchCart>> | null>(null);
  const [preview, setPreview] = useState<{ total: number; subtotal: number; shipping: number; discount: number } | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [feedback, setFeedback] = useState<string | null>(null);

  useEffect(() => {
    if (!accessToken) {
      setCart(null);
      setPreview(null);
      return;
    }

    setIsLoading(true);
    fetchCart(accessToken)
      .then((cartData) => {
        setCart(cartData);
        return previewCheckout(accessToken, cartData.id, selectedMethod);
      })
      .then(setPreview)
      .catch(() => setFeedback("Não foi possível carregar o resumo do checkout."))
      .finally(() => setIsLoading(false));
  }, [accessToken, selectedMethod]);

  async function handleCheckout() {
    if (!accessToken || !cart) {
      setFeedback("Conecte a conta demo e adicione itens ao carrinho antes de continuar.");
      return;
    }
    setIsSubmitting(true);
    try {
      await checkoutCart(accessToken, cart.id, selectedMethod === "Cartão de crédito" ? "CREDIT_CARD" : selectedMethod);
      setFeedback("Pedido confirmado com sucesso.");
      router.push("/orders");
    } catch {
      setFeedback("O backend recusou o checkout. Verifique carrinho e pagamento.");
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <ScrollView style={s.container} contentContainerStyle={s.content}>
      <Text style={s.title}>Checkout</Text>

      {!accessToken ? (
        <View style={s.section}>
          <Text style={s.sectionTitle}>Login necessário</Text>
          <Text style={s.sectionBody}>Entre com a conta demo na tela inicial para carregar o checkout real.</Text>
        </View>
      ) : isLoading ? (
        <View style={s.section}>
          <Text style={s.sectionTitle}>Carregando checkout</Text>
          <Text style={s.sectionBody}>Buscando carrinho e prévia do pedido.</Text>
        </View>
      ) : !cart || cart.items.length === 0 ? (
        <View style={s.section}>
          <Text style={s.sectionTitle}>Carrinho vazio</Text>
          <Text style={s.sectionBody}>Adicione itens antes de confirmar o pedido.</Text>
        </View>
      ) : (
        <>

          <View style={s.section}>
            <Text style={s.sectionTitle}>Entrega</Text>
            <Text style={s.sectionBody}>Av. Paulista, 1500, Bela Vista, São Paulo - SP</Text>
            <Text style={s.helper}>Entrega estimada em até 3 dias úteis.</Text>
          </View>

          <View style={s.section}>
            <Text style={s.sectionTitle}>Pagamento</Text>
            {paymentMethods.map((method, index) => (
              <TouchableOpacity
                key={method}
                style={[s.option, selectedMethod === method && s.optionActive]}
                onPress={() => setSelectedMethod(method)}
              >
                <Text style={s.optionTitle}>{method}</Text>
                <Text style={s.optionSubtitle}>{index === 0 ? "Aprovação imediata" : "Disponível na próxima etapa"}</Text>
              </TouchableOpacity>
            ))}
          </View>

          <View style={s.section}>
            <Text style={s.sectionTitle}>Resumo do pedido</Text>
            <View style={s.summaryRow}>
              <Text style={s.summaryLabel}>Subtotal</Text>
              <Text style={s.summaryValue}>R$ {preview?.subtotal.toFixed(2) ?? cart.subtotal.toFixed(2)}</Text>
            </View>
            <View style={s.summaryRow}>
              <Text style={s.summaryLabel}>Frete</Text>
              <Text style={s.summaryValue}>R$ {preview?.shipping.toFixed(2) ?? cart.shippingTotal.toFixed(2)}</Text>
            </View>
            <View style={s.summaryRow}>
              <Text style={s.summaryLabel}>Desconto</Text>
              <Text style={s.summaryValue}>R$ {preview?.discount.toFixed(2) ?? cart.discount.toFixed(2)}</Text>
            </View>
            <View style={[s.summaryRow, s.summaryTotal]}>
              <Text style={s.summaryTotalLabel}>Total</Text>
              <Text style={s.summaryTotalValue}>R$ {preview?.total.toFixed(2) ?? cart.total.toFixed(2)}</Text>
            </View>
          </View>

          <TouchableOpacity style={s.confirmButton} onPress={handleCheckout} disabled={isSubmitting}>
            <Text style={s.confirmButtonText}>{isSubmitting ? "Confirmando..." : "Confirmar pedido"}</Text>
          </TouchableOpacity>
        </>
      )}

      {feedback ? <Text style={s.feedback}>{feedback}</Text> : null}
    </ScrollView>
  );
}

const s = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#f8fafc" },
  content: { padding: 16, paddingBottom: 28 },
  title: { fontSize: 24, fontWeight: "700", color: "#0f172a", marginBottom: 16 },
  section: { backgroundColor: "white", borderRadius: 18, padding: 16, marginBottom: 14 },
  sectionTitle: { fontSize: 16, fontWeight: "700", color: "#0f172a", marginBottom: 10 },
  sectionBody: { fontSize: 14, color: "#334155", lineHeight: 20 },
  helper: { fontSize: 12, color: "#64748b", marginTop: 6 },
  option: { borderRadius: 14, borderWidth: 1, borderColor: "#e2e8f0", padding: 14, marginBottom: 10 },
  optionActive: { borderColor: "#f59e0b", backgroundColor: "#fffbeb" },
  optionTitle: { fontSize: 14, fontWeight: "600", color: "#0f172a" },
  optionSubtitle: { fontSize: 12, color: "#64748b", marginTop: 4 },
  summaryRow: { flexDirection: "row", justifyContent: "space-between", marginBottom: 10 },
  summaryLabel: { fontSize: 14, color: "#64748b" },
  summaryValue: { fontSize: 14, color: "#0f172a", fontWeight: "600" },
  summaryTotal: { borderTopWidth: 1, borderTopColor: "#e2e8f0", paddingTop: 12, marginTop: 4 },
  summaryTotalLabel: { fontSize: 16, color: "#0f172a", fontWeight: "700" },
  summaryTotalValue: { fontSize: 18, color: "#0f172a", fontWeight: "700" },
  confirmButton: { borderRadius: 18, backgroundColor: "#f59e0b", paddingVertical: 16, alignItems: "center", marginTop: 8 },
  confirmButtonText: { color: "#0f172a", fontSize: 16, fontWeight: "700" },
  feedback: { marginTop: 14, fontSize: 13, color: "#475569" },
});