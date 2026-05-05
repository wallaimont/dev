import { useEffect, useMemo, useState } from "react";
import { ScrollView, StyleSheet, Text, TextInput, TouchableOpacity, View } from "react-native";
import { fetchMyChats, markChatAsRead, sendChatMessage } from "../lib/api";
import { useSessionStore } from "../store/session-store";

export default function ChatScreen() {
  const accessToken = useSessionStore((state) => state.accessToken);
  const userId = useSessionStore((state) => state.userId);
  const [conversations, setConversations] = useState<Awaited<ReturnType<typeof fetchMyChats>>>([]);
  const [selectedId, setSelectedId] = useState<string | null>(null);
  const [message, setMessage] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (!accessToken) {
      setConversations([]);
      setSelectedId(null);
      return;
    }

    setIsLoading(true);
    fetchMyChats(accessToken)
      .then((data) => {
        setConversations(data);
        setSelectedId(data[0]?.id ?? null);
      })
      .finally(() => setIsLoading(false));
  }, [accessToken]);

  const selected = useMemo(
    () => conversations.find((conversation) => conversation.id === selectedId) ?? null,
    [conversations, selectedId]
  );

  async function handleSelect(conversationId: string) {
    setSelectedId(conversationId);
    if (accessToken) {
      await markChatAsRead(accessToken, conversationId);
    }
  }

  async function handleSend() {
    if (!selectedId || !message.trim() || !accessToken) {
      return;
    }

    await sendChatMessage(accessToken, selectedId, message.trim());
    const refreshed = await fetchMyChats(accessToken);
    setConversations(refreshed);
    setMessage("");
  }

  return (
    <View style={s.container}>
      <View style={s.sidebar}>
        <Text style={s.sectionTitle}>Conversas</Text>
        {!accessToken ? (
          <Text style={s.emptyText}>Entre com a conta demo para carregar as conversas reais.</Text>
        ) : isLoading ? (
          <Text style={s.emptyText}>Carregando conversas...</Text>
        ) : conversations.length === 0 ? (
          <Text style={s.emptyText}>Nenhuma conversa encontrada para esta conta.</Text>
        ) : conversations.map((conversation) => (
          <TouchableOpacity
            key={conversation.id}
            style={[s.conversationCard, selectedId === conversation.id && s.conversationCardActive]}
            onPress={() => handleSelect(conversation.id)}
          >
            <Text style={s.conversationTitle}>Chat #{conversation.id.slice(0, 8)}</Text>
            <Text style={s.conversationMeta}>{conversation.lastMessageAt ? new Date(conversation.lastMessageAt).toLocaleString("pt-BR") : "Sem mensagens"}</Text>
            {(conversation.buyerUnread + conversation.sellerUnread) > 0 ? (
              <View style={s.unreadBadge}>
                <Text style={s.unreadBadgeText}>{conversation.buyerUnread + conversation.sellerUnread}</Text>
              </View>
            ) : null}
          </TouchableOpacity>
        ))}
      </View>

      <View style={s.chatPane}>
        <Text style={s.sectionTitle}>{selected ? `Conversa #${selected.id.slice(0, 8)}` : "Selecione uma conversa"}</Text>
        <ScrollView style={s.messages} contentContainerStyle={s.messagesContent}>
          {selected?.messages.map((item) => (
            <View key={item.id} style={[s.bubble, item.senderId === userId ? s.bubbleMine : s.bubbleTheirs]}>
              <Text style={[s.bubbleText, item.senderId === userId && s.bubbleTextMine]}>{item.content}</Text>
              <Text style={s.bubbleTime}>{new Date(item.createdAt).toLocaleTimeString("pt-BR", { hour: "2-digit", minute: "2-digit" })}</Text>
            </View>
          ))}
        </ScrollView>

        <View style={s.composer}>
          <TextInput
            style={s.input}
            placeholder="Digite sua mensagem..."
            placeholderTextColor="#94a3b8"
            value={message}
            onChangeText={setMessage}
          />
          <TouchableOpacity style={s.sendButton} onPress={handleSend}>
            <Text style={s.sendButtonText}>Enviar</Text>
          </TouchableOpacity>
        </View>
      </View>
    </View>
  );
}

const s = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#f8fafc" },
  sidebar: { padding: 16, gap: 10 },
  sectionTitle: { fontSize: 22, fontWeight: "700", color: "#0f172a", marginBottom: 12 },
  conversationCard: { backgroundColor: "white", borderRadius: 16, padding: 14, borderWidth: 1, borderColor: "#e2e8f0" },
  conversationCardActive: { borderColor: "#f59e0b", backgroundColor: "#fffbeb" },
  conversationTitle: { fontSize: 15, fontWeight: "600", color: "#0f172a" },
  conversationMeta: { fontSize: 12, color: "#64748b", marginTop: 4 },
  emptyText: { fontSize: 13, color: "#64748b" },
  unreadBadge: { marginTop: 8, alignSelf: "flex-start", borderRadius: 999, backgroundColor: "#f59e0b", paddingHorizontal: 8, paddingVertical: 2 },
  unreadBadgeText: { fontSize: 11, fontWeight: "700", color: "#0f172a" },
  chatPane: { flex: 1, paddingHorizontal: 16, paddingBottom: 16 },
  messages: { flex: 1, maxHeight: 420 },
  messagesContent: { gap: 10, paddingBottom: 16 },
  bubble: { maxWidth: "84%", borderRadius: 18, paddingHorizontal: 14, paddingVertical: 10 },
  bubbleMine: { alignSelf: "flex-end", backgroundColor: "#fef3c7" },
  bubbleTheirs: { alignSelf: "flex-start", backgroundColor: "white" },
  bubbleText: { fontSize: 14, color: "#334155" },
  bubbleTextMine: { color: "#0f172a" },
  bubbleTime: { marginTop: 6, fontSize: 11, color: "#94a3b8" },
  composer: { flexDirection: "row", gap: 10, marginTop: 16 },
  input: { flex: 1, backgroundColor: "white", borderRadius: 14, borderWidth: 1, borderColor: "#e2e8f0", paddingHorizontal: 14, paddingVertical: 12, color: "#0f172a" },
  sendButton: { borderRadius: 14, backgroundColor: "#0f172a", paddingHorizontal: 18, justifyContent: "center" },
  sendButtonText: { color: "white", fontWeight: "700" },
});