import { useEffect, useState } from "react";
import { Text, View, ScrollView, StyleSheet, TextInput, TouchableOpacity } from "react-native";
import { createSupportTicket, fetchSupportTickets } from "../lib/api";
import { useSessionStore, type SessionState } from "../store/session-store";

export default function SupportScreen() {
  const accessToken = useSessionStore((state: SessionState) => state.accessToken);
  const [subject, setSubject] = useState("");
  const [message, setMessage] = useState("");
  const [tickets, setTickets] = useState<Awaited<ReturnType<typeof fetchSupportTickets>>>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [feedback, setFeedback] = useState<string | null>(null);

  useEffect(() => {
    if (!accessToken) {
      setTickets([]);
      return;
    }

    setIsLoading(true);
    fetchSupportTickets(accessToken)
      .then(setTickets)
      .finally(() => setIsLoading(false));
  }, [accessToken]);

  async function handleCreateTicket() {
    if (!accessToken || !subject.trim() || !message.trim()) {
      setFeedback("Preencha assunto e descrição para abrir o chamado.");
      return;
    }

    setIsSubmitting(true);
    try {
      const created = await createSupportTicket(accessToken, subject.trim(), "OTHER", message.trim());
      setTickets((current) => [created, ...current]);
      setSubject("");
      setMessage("");
      setFeedback("Chamado aberto com sucesso.");
    } catch {
      setFeedback("Não foi possível abrir o chamado.");
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <ScrollView style={s.container}>
      <Text style={s.title}>Suporte</Text>

      <View style={s.form}>
        <TextInput style={s.input} placeholder="Assunto" placeholderTextColor="#94a3b8" value={subject} onChangeText={setSubject} />
        <TextInput
          style={[s.input, s.textarea]}
          placeholder="Descreva seu problema..."
          placeholderTextColor="#94a3b8"
          multiline
          numberOfLines={4}
          value={message}
          onChangeText={setMessage}
        />
        <TouchableOpacity style={s.btn} onPress={handleCreateTicket} disabled={isSubmitting || !accessToken}>
          <Text style={s.btnText}>{isSubmitting ? "Enviando..." : "Abrir chamado"}</Text>
        </TouchableOpacity>
        {feedback ? <Text style={s.feedback}>{feedback}</Text> : null}
      </View>

      <Text style={s.sectionTitle}>Chamados anteriores</Text>

      {!accessToken ? (
        <Text style={s.empty}>Entre com a conta demo para gerenciar chamados.</Text>
      ) : isLoading ? (
        <Text style={s.empty}>Carregando chamados...</Text>
      ) : tickets.length === 0 ? (
        <Text style={s.empty}>Nenhum chamado anterior encontrado.</Text>
      ) : (
        tickets.map((ticket) => {
          const isResolved = ticket.status === "RESOLVED";
          return (
            <View key={ticket.id} style={s.ticket}>
              <View style={s.ticketHeader}>
                <Text style={s.ticketSubject}>{ticket.subject}</Text>
                <View style={isResolved ? s.badgeResolved : s.badgeOpen}>
                  <Text style={isResolved ? s.badgeTextResolved : s.badgeText}>{ticket.status}</Text>
                </View>
              </View>
              <Text style={s.ticketMeta}>Categoria: {ticket.category ?? "OTHER"} • {new Date(ticket.createdAt).toLocaleDateString("pt-BR")}</Text>
            </View>
          );
        })
      )}
    </ScrollView>
  );
}

const s = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#f8fafc", padding: 16 },
  title: { fontSize: 24, fontWeight: "700", color: "#0f172a", marginBottom: 16 },
  form: { gap: 12, marginBottom: 24 },
  input: {
    backgroundColor: "white",
    borderRadius: 12,
    padding: 14,
    fontSize: 14,
    borderWidth: 1,
    borderColor: "#e2e8f0",
    color: "#0f172a"
  },
  textarea: { height: 100, textAlignVertical: "top" },
  btn: { backgroundColor: "#0f172a", borderRadius: 12, padding: 14, alignItems: "center" },
  btnText: { color: "white", fontSize: 15, fontWeight: "600" },
  feedback: { fontSize: 13, color: "#475569" },
  sectionTitle: { fontSize: 16, fontWeight: "600", color: "#0f172a", marginBottom: 12 },
  ticket: {
    backgroundColor: "white",
    borderRadius: 12,
    padding: 14,
    marginBottom: 10
  },
  ticketHeader: { flexDirection: "row", justifyContent: "space-between", alignItems: "center" },
  ticketSubject: { fontSize: 14, fontWeight: "600", color: "#0f172a" },
  badgeOpen: { backgroundColor: "#fef3c7", borderRadius: 8, paddingHorizontal: 8, paddingVertical: 3 },
  badgeText: { fontSize: 10, fontWeight: "700", color: "#d97706" },
  badgeResolved: { backgroundColor: "#d1fae5", borderRadius: 8, paddingHorizontal: 8, paddingVertical: 3 },
  badgeTextResolved: { fontSize: 10, fontWeight: "700", color: "#059669" },
  ticketMeta: { fontSize: 12, color: "#64748b", marginTop: 6 },
  empty: { fontSize: 14, color: "#64748b" }
});
