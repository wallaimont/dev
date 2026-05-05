"use client";

import { ChatConversation } from "@/lib/api";
import { Panel } from "@/components/ui/panel";

type ChatWidgetProps = {
  role: "BUYER" | "SELLER";
  chats: ChatConversation[];
  selectedChatId: string | null;
  isLoading: boolean;
  newMessage: string;
  counterpartLabel: string;
  emptyListText: string;
  emptySelectionText: string;
  inputPlaceholder: string;
  onSelectChat: (chatId: string) => void;
  onNewMessageChange: (value: string) => void;
  onSendMessage: () => void;
  isSending: boolean;
};

export function ChatWidget({
  role,
  chats,
  selectedChatId,
  isLoading,
  newMessage,
  counterpartLabel,
  emptyListText,
  emptySelectionText,
  inputPlaceholder,
  onSelectChat,
  onNewMessageChange,
  onSendMessage,
  isSending,
}: ChatWidgetProps) {
  const selectedChat = chats.find((chat) => chat.id === selectedChatId);
  const currentUserId = selectedChat ? (role === "BUYER" ? selectedChat.buyerId : selectedChat.sellerId) : null;

  return (
    <div className="grid gap-6 lg:grid-cols-[300px_1fr]">
      <div className="space-y-2">
        {isLoading ? (
          <div className="flex justify-center py-10">
            <div className="h-6 w-6 animate-spin rounded-full border-4 border-amber-400 border-t-transparent" />
          </div>
        ) : chats.length === 0 ? (
          <p className="text-sm text-slate-500">{emptyListText}</p>
        ) : (
          chats.map((chat) => {
            const unreadCount = role === "BUYER" ? chat.buyerUnread : chat.sellerUnread;
            return (
              <button
                key={chat.id}
                onClick={() => onSelectChat(chat.id)}
                className={`w-full rounded-2xl border p-4 text-left transition ${
                  selectedChatId === chat.id
                    ? "border-amber-400 bg-amber-50"
                    : "border-slate-100 bg-white hover:bg-slate-50"
                }`}
              >
                <p className="text-sm font-medium text-slate-900">{counterpartLabel}</p>
                <p className="text-xs text-slate-500">
                  {chat.lastMessageAt ? new Date(chat.lastMessageAt).toLocaleString("pt-BR") : "Sem mensagens"}
                </p>
                {unreadCount > 0 ? (
                  <span className="mt-1 inline-flex h-5 w-5 items-center justify-center rounded-full bg-amber-400 text-xs font-bold text-slate-900">
                    {unreadCount}
                  </span>
                ) : null}
              </button>
            );
          })
        )}
      </div>

      <Panel title={selectedChat ? "Conversa" : "Selecione uma conversa"}>
        {selectedChat ? (
          <div className="flex flex-col gap-4">
            <div className="max-h-96 space-y-3 overflow-y-auto">
              {selectedChat.messages.map((msg) => (
                <div
                  key={msg.id}
                  className={`max-w-[80%] rounded-2xl px-4 py-2.5 text-sm ${
                    msg.senderId === currentUserId ? "ml-auto bg-amber-100 text-slate-900" : "bg-slate-100 text-slate-700"
                  }`}
                >
                  <p>{msg.content}</p>
                  <p className="mt-1 text-[10px] text-slate-400">
                    {new Date(msg.createdAt).toLocaleTimeString("pt-BR", {
                      hour: "2-digit",
                      minute: "2-digit",
                    })}
                  </p>
                </div>
              ))}
            </div>

            <div className="flex gap-2">
              <input
                type="text"
                value={newMessage}
                onChange={(e) => onNewMessageChange(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === "Enter" && newMessage.trim()) {
                    onSendMessage();
                  }
                }}
                placeholder={inputPlaceholder}
                className="flex-1 rounded-xl border border-slate-200 px-4 py-2.5 text-sm outline-none focus:border-amber-400"
              />
              <button
                onClick={onSendMessage}
                disabled={!newMessage.trim() || isSending}
                className="rounded-xl bg-slate-900 px-5 py-2.5 text-sm font-medium text-white disabled:opacity-50"
              >
                Enviar
              </button>
            </div>
          </div>
        ) : (
          <p className="text-sm text-slate-500">{emptySelectionText}</p>
        )}
      </Panel>
    </div>
  );
}