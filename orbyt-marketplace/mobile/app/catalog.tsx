import { Text, View, ScrollView, StyleSheet, TouchableOpacity, TextInput } from "react-native";
import { useState, useEffect } from "react";
import { useRouter } from "expo-router";

const API_BASE = "http://10.0.2.2:8080/api";

type Product = {
  id: string;
  name: string;
  sku: string;
  price: number;
  promotionalPrice: number | null;
  currencyCode: string;
};

export default function CatalogScreen() {
  const router = useRouter();
  const [products, setProducts] = useState<Product[]>([]);
  const [search, setSearch] = useState("");

  useEffect(() => {
    fetch(`${API_BASE}/v1/catalog/products`, {
      headers: { "X-Tenant-Id": "orbyt-demo" }
    })
      .then((r) => r.json())
      .then(setProducts)
      .catch(() => {});
  }, []);

  const filtered = products.filter(
    (p) =>
      p.name.toLowerCase().includes(search.toLowerCase()) ||
      p.sku.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <ScrollView style={s.container}>
      <View style={s.header}>
        <Text style={s.title}>Catálogo</Text>
        <TextInput
          style={s.search}
          placeholder="Buscar produtos..."
          placeholderTextColor="#94a3b8"
          value={search}
          onChangeText={setSearch}
        />
      </View>

      {filtered.map((product) => (
        <View key={product.id} style={s.card}>
          <TouchableOpacity onPress={() => router.push(`/product/${product.id}` as any)}>
            <Text style={s.productName}>{product.name}</Text>
            <Text style={s.sku}>{product.sku}</Text>
          </TouchableOpacity>
          <View style={s.priceRow}>
            {product.promotionalPrice ? (
              <>
                <Text style={s.oldPrice}>
                  {product.currencyCode} {product.price.toFixed(2)}
                </Text>
                <Text style={s.promoPrice}>
                  {product.currencyCode} {product.promotionalPrice.toFixed(2)}
                </Text>
              </>
            ) : (
              <Text style={s.price}>
                {product.currencyCode} {product.price.toFixed(2)}
              </Text>
            )}
            <TouchableOpacity style={s.addBtn} onPress={() => router.push("/cart") }>
              <Text style={s.addBtnText}>+ Carrinho</Text>
            </TouchableOpacity>
          </View>
        </View>
      ))}

      {filtered.length === 0 && (
        <Text style={s.empty}>Nenhum produto encontrado</Text>
      )}
    </ScrollView>
  );
}

const s = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#f8fafc" },
  header: { padding: 16, paddingTop: 8 },
  title: { fontSize: 24, fontWeight: "700", color: "#0f172a", marginBottom: 12 },
  search: {
    backgroundColor: "white",
    borderRadius: 12,
    padding: 12,
    fontSize: 14,
    borderWidth: 1,
    borderColor: "#e2e8f0",
    color: "#0f172a"
  },
  card: {
    backgroundColor: "white",
    marginHorizontal: 16,
    marginBottom: 12,
    borderRadius: 16,
    padding: 16,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.05,
    shadowRadius: 4,
    elevation: 1
  },
  productName: { fontSize: 16, fontWeight: "600", color: "#0f172a" },
  sku: { fontSize: 12, color: "#64748b", marginTop: 2 },
  priceRow: { flexDirection: "row", alignItems: "center", marginTop: 12, gap: 8 },
  price: { fontSize: 16, fontWeight: "700", color: "#0f172a", flex: 1 },
  oldPrice: { fontSize: 13, color: "#94a3b8", textDecorationLine: "line-through" },
  promoPrice: { fontSize: 16, fontWeight: "700", color: "#059669", flex: 1 },
  addBtn: { backgroundColor: "#f59e0b", borderRadius: 20, paddingHorizontal: 16, paddingVertical: 8 },
  addBtnText: { fontSize: 13, fontWeight: "600", color: "#0f172a" },
  empty: { textAlign: "center", color: "#64748b", marginTop: 40, fontSize: 14 }
});
