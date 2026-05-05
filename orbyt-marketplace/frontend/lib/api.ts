import { apiClient } from "@/lib/api-client";

/* ─── Types ──────────────────────────────────────────── */

export type Product = {
  id: string;
  storeId: string;
  categoryId: string;
  name: string;
  sku: string;
  price: number;
  promotionalPrice: number | null;
  currencyCode: string;
  approvalStatus: string;
};

export type LoginResponse = {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
};

export type AdminDashboard = {
  tenantId: string;
  products: number;
  stores: number;
  users: number;
  gmv: number;
  riskAlerts: number;
};

export type CartItem = {
  id: string;
  productId: string;
  productName: string;
  quantity: number;
  unitPrice: number;
  subtotal: number;
};

export type Cart = {
  id: string;
  items: CartItem[];
  total: number;
};

export type OrderGroup = {
  id: string;
  buyerId: string;
  grandTotal: number;
  paymentMethod: string;
  status: string;
  createdAt: string;
  orders: OrderSummary[];
};

export type OrderSummary = {
  id: string;
  sellerId: string;
  total: number;
  status: string;
  trackingCode: string | null;
  items: { productName: string; quantity: number; unitPrice: number }[];
};

export type Review = {
  id: string;
  productId: string;
  buyerId: string;
  rating: number;
  title: string;
  comment: string;
  createdAt: string;
};

export type Notification = {
  id: string;
  notificationType: string;
  title: string;
  body: string;
  channel: string;
  isRead: boolean;
  readAt: string | null;
  createdAt: string;
};

export type SupportTicket = {
  id: string;
  subject: string;
  description: string;
  category: string;
  priority: string;
  status: string;
  createdAt: string;
};

export type ChatMessage = {
  id: string;
  senderId: string;
  content: string;
  messageType: string;
  createdAt: string;
  isRead: boolean;
};

export type ChatConversation = {
  id: string;
  buyerId: string;
  sellerId: string;
  lastMessageAt: string | null;
  buyerUnread: number;
  sellerUnread: number;
  messages: ChatMessage[];
};

type PagedResponse<T> = {
  content?: T[];
};

export type Banner = {
  id: string;
  title: string;
  imageUrl: string;
  linkUrl: string;
  position: string;
};

/* ─── Auth ───────────────────────────────────────────── */

export async function login(email: string, password: string) {
  const { data } = await apiClient.post<LoginResponse>("/v1/auth/login", {
    tenantSlug: "orbyt-demo",
    email,
    password
  });
  return data;
}

export const loginSeller = login;

export async function register(name: string, email: string, password: string) {
  const { data } = await apiClient.post("/v1/auth/register", {
    tenantSlug: "orbyt-demo",
    fullName: name,
    email,
    password
  });
  return data;
}

/* ─── Catalog ────────────────────────────────────────── */

export async function fetchProducts(): Promise<Product[]> {
  const { data } = await apiClient.get<Product[]>("/v1/catalog/products");
  return data;
}

export async function createProduct(input: {
  storeId: string;
  categoryId: string;
  sku: string;
  name: string;
  description: string;
  price: number;
  promotionalPrice?: number;
  currencyCode: string;
}) {
  const { data } = await apiClient.post<Product>("/v1/catalog/products", input);
  return data;
}

/* ─── Cart ───────────────────────────────────────────── */

export async function fetchCart(): Promise<Cart> {
  const { data } = await apiClient.get<Cart>("/v1/cart");
  return data;
}

export async function addToCart(productId: string, quantity: number) {
  const { data } = await apiClient.post("/v1/cart/items", { productId, quantity });
  return data;
}

export async function removeFromCart(itemId: string) {
  await apiClient.delete(`/v1/cart/items/${itemId}`);
}

/* ─── Checkout & Orders ──────────────────────────────── */

export async function checkout(paymentMethod: string) {
  const { data } = await apiClient.post("/v1/checkout", { paymentMethod });
  return data;
}

export async function fetchMyOrders(buyerId: string): Promise<OrderGroup[]> {
  const { data } = await apiClient.get<OrderGroup[]>(`/v1/orders/my?buyerId=${buyerId}`);
  return data;
}

export async function fetchSellerOrders(sellerId: string): Promise<OrderSummary[]> {
  const { data } = await apiClient.get<OrderSummary[]>(`/v1/orders/seller?sellerId=${sellerId}`);
  return data;
}

/* ─── Reviews ────────────────────────────────────────── */

export async function fetchReviews(productId: string): Promise<Review[]> {
  const { data } = await apiClient.get<Review[]>(`/v1/reviews/product/${productId}`);
  return data;
}

export async function createReview(productId: string, rating: number, title: string, comment: string) {
  const { data } = await apiClient.post("/v1/reviews", { productId, rating, title, comment });
  return data;
}

/* ─── Notifications ──────────────────────────────────── */

export async function fetchNotifications(): Promise<Notification[]> {
  const { data } = await apiClient.get<PagedResponse<Notification>>(`/v1/notifications?size=50`);
  if (Array.isArray(data)) {
    return data as Notification[];
  }
  return data.content ?? [];
}

export async function markNotificationRead(notificationId: string) {
  await apiClient.put(`/v1/notifications/${notificationId}/read`);
}

/* ─── Support ────────────────────────────────────────── */

export async function fetchTickets(): Promise<SupportTicket[]> {
  const { data } = await apiClient.get<PagedResponse<SupportTicket>>(`/v1/support/tickets/mine?size=20`);
  if (Array.isArray(data)) {
    return data as SupportTicket[];
  }
  return data.content ?? [];
}

export async function createTicket(subject: string, category: string, message: string) {
  const { data } = await apiClient.post("/v1/support/tickets", {
    subject,
    description: message,
    category,
    priority: "MEDIUM"
  });
  return data;
}

/* ─── Chat ───────────────────────────────────────────── */

export async function fetchMyChats(role = "BUYER"): Promise<ChatConversation[]> {
  const { data } = await apiClient.get<ChatConversation[]>(`/v1/chats?role=${role}`);
  return data;
}

export async function startChatWithSeller(sellerId: string): Promise<ChatConversation> {
  const { data } = await apiClient.post<ChatConversation>(`/v1/chats/with/${sellerId}`);
  return data;
}

export async function sendChatMessage(chatId: string, content: string, messageType = "TEXT") {
  const { data } = await apiClient.post<ChatMessage>(`/v1/chats/${chatId}/messages`, {
    content,
    messageType
  });
  return data;
}

export async function markChatAsRead(chatId: string) {
  await apiClient.put(`/v1/chats/${chatId}/read`);
}

/* ─── CMS ────────────────────────────────────────────── */

export async function fetchBanners(position = "HOME_HERO"): Promise<Banner[]> {
  const { data } = await apiClient.get<Banner[]>(`/v1/cms/banners?position=${position}`);
  return data;
}

/* ─── Coupons ────────────────────────────────────────── */

export async function validateCoupon(code: string, orderTotal: number) {
  const { data } = await apiClient.get(`/v1/coupons/validate?code=${encodeURIComponent(code)}&orderTotal=${orderTotal}`);
  return data;
}

/* ─── Admin ──────────────────────────────────────────── */

export async function fetchAdminDashboard(): Promise<AdminDashboard> {
  const { data } = await apiClient.get<AdminDashboard>("/v1/admin/dashboard");
  return data;
}

/* ─── Wishlist ───────────────────────────────────────── */

export async function fetchWishlist(userId: string) {
  const { data } = await apiClient.get(`/v1/wishlist?userId=${userId}`);
  return data;
}

export async function addToWishlist(productId: string) {
  const { data } = await apiClient.post("/v1/wishlist", { productId });
  return data;
}

/* ─── Shipping ───────────────────────────────────────── */

export async function trackShipment(trackingCode: string) {
  const { data } = await apiClient.get(`/v1/shipping/track/${trackingCode}`);
  return data;
}
