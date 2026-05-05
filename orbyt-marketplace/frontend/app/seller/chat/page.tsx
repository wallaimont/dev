"use client";

import { ChatWidget } from "@/components/ui/chat-widget";
import { fetchMyChats, sendChatMessage, markChatAsRead } from "@/lib/api";
import { useNotificationSocket } from "@/lib/hooks/use-notification-socket";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";

export default function SellerChatPage() {
  const queryClient = useQueryClient();
  const [selectedChatId, setSelectedChatId] = useState<string | null>(null);
  const [newMessage, setNewMessage] = useState("");

  const { data: chats = [], isLoading } = useQuery({
    queryKey: ["seller-chats"],
    queryFn: () => fetchMyChats("SELLER"),
  });

  useNotificationSocket(() => {
    queryClient.invalidateQueries({ queryKey: ["seller-chats"] });
  });

  const selectedChat = chats.find((c) => c.id === selectedChatId);

  const sendMutation = useMutation({
    mutationFn: () => sendChatMessage(selectedChatId!, newMessage),
    onSuccess: () => {
      setNewMessage("");
      queryClient.invalidateQueries({ queryKey: ["seller-chats"] });
    },
  });

  const readMutation = useMutation({
    mutationFn: (chatId: string) => markChatAsRead(chatId),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["seller-chats"] }),
  });

  function selectChat(chatId: string) {
    setSelectedChatId(chatId);
    readMutation.mutate(chatId);
  }

  return (
    <main className="mx-auto min-h-screen max-w-5xl px-6 py-10">
      <h1 className="mb-8 text-3xl font-bold text-slate-900">Mensagens de Compradores</h1>

      <ChatWidget
        role="SELLER"
        chats={chats}
        selectedChatId={selectedChatId}
        isLoading={isLoading}
        newMessage={newMessage}
        counterpartLabel="Comprador"
        emptyListText="Nenhuma conversa recebida"
        emptySelectionText="Selecione uma conversa na lista ao lado para responder."
        inputPlaceholder="Responder..."
        onSelectChat={selectChat}
        onNewMessageChange={setNewMessage}
        onSendMessage={() => sendMutation.mutate()}
        isSending={sendMutation.isPending}
      />
    </main>
  );
}
