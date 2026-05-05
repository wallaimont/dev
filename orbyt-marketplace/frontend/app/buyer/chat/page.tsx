"use client";

import { ChatWidget } from "@/components/ui/chat-widget";
import { fetchMyChats, sendChatMessage, markChatAsRead } from "@/lib/api";
import { useNotificationSocket } from "@/lib/hooks/use-notification-socket";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";

export default function BuyerChatPage() {
  const queryClient = useQueryClient();
  const [selectedChatId, setSelectedChatId] = useState<string | null>(null);
  const [newMessage, setNewMessage] = useState("");

  const { data: chats = [], isLoading } = useQuery({
    queryKey: ["buyer-chats"],
    queryFn: () => fetchMyChats("BUYER"),
  });

  useNotificationSocket(() => {
    queryClient.invalidateQueries({ queryKey: ["buyer-chats"] });
  });

  const selectedChat = chats.find((c) => c.id === selectedChatId);

  const sendMutation = useMutation({
    mutationFn: () => sendChatMessage(selectedChatId!, newMessage),
    onSuccess: () => {
      setNewMessage("");
      queryClient.invalidateQueries({ queryKey: ["buyer-chats"] });
    },
  });

  const readMutation = useMutation({
    mutationFn: (chatId: string) => markChatAsRead(chatId),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["buyer-chats"] }),
  });

  function selectChat(chatId: string) {
    setSelectedChatId(chatId);
    readMutation.mutate(chatId);
  }

  return (
    <main className="mx-auto min-h-screen max-w-5xl px-6 py-10">
      <h1 className="mb-8 text-3xl font-bold text-slate-900">Chat</h1>

      <ChatWidget
        role="BUYER"
        chats={chats}
        selectedChatId={selectedChatId}
        isLoading={isLoading}
        newMessage={newMessage}
        counterpartLabel="Vendedor"
        emptyListText="Nenhuma conversa iniciada"
        emptySelectionText="Selecione uma conversa na lista ao lado para ver as mensagens."
        inputPlaceholder="Digite sua mensagem..."
        onSelectChat={selectChat}
        onNewMessageChange={setNewMessage}
        onSendMessage={() => sendMutation.mutate()}
        isSending={sendMutation.isPending}
      />
    </main>
  );
}
