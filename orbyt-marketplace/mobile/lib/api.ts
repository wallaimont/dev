const API_BASE = "http://10.0.2.2:8080/api";
const TENANT_SLUG = "orbyt-demo";
const TENANT_HEADER = "orbyt-demo";
const DEMO_EMAIL = "admin@orbyt.local";
const DEMO_PASSWORD = "Admin@123";
const DEFAULT_SHIPPING_ADDRESS_ID = "00000000-0000-0000-0000-000000000001";

type RequestOptions = {
  method?: string;
  token?: string | null;
  body?: unknown;
};

export type AuthSession = {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
};

export type MobileProduct = {
  id: string;
  name: string;
  sku: string;
  price: number;
  promotionalPrice: number | null;
  currencyCode: string;
  approvalStatus: string;
};

export type MobileCart = {
  id: string;
  userId: string;
  currencyCode: string;
  subtotal: number;
  discount: number;
  shippingTotal: number;
  total: number;
  items: Array<{
    id: string;
    productId: string;
    variantId: string | null;
    quantity: number;
    unitPrice: number;
    subtotal: number;
  }>;
};

export type MobileOrderGroup = {
  id: string;
  buyerId: string;
  grandTotal: number;
  paymentMethod: string;
  status: string;
  createdAt: string;
  orders: Array<{
    id: string;
    total: number;
    status: string;
    trackingCode: string | null;
    items: Array<{
      productName: string;
      quantity: number;
      unitPrice: number;
    }>;
  }>;
};

export type MobileChat = {
  id: string;
  buyerId: string;
  sellerId: string;
  lastMessageAt: string | null;
  buyerUnread: number;
  sellerUnread: number;
  messages: Array<{
    id: string;
    senderId: string;
    content: string;
    createdAt: string;
  }>;
};

export type MobileNotification = {
  id: string;
  notificationType: string;
  title: string;
  body: string;
  channel: string;
  isRead: boolean;
  readAt: string | null;
  createdAt: string;
};

export type MobileSupportTicket = {
  id: string;
  subject: string;
  description: string | null;
  category: string | null;
  priority: string;
  status: string;
  createdAt: string;
};

export type MobileUserProfile = {
  id: string;
  userId: string;
  displayName: string | null;
  phone: string | null;
  cpf: string | null;
  avatarUrl: string | null;
  preferredLanguage: string | null;
  preferredCurrency: string | null;
};

export type MobileUserAddress = {
  id: string;
  label: string;
  street: string;
  number: string | null;
  neighborhood: string;
  city: string;
  state: string;
  zipCode: string;
  defaultAddress: boolean;
};

export type UpdateUserProfileInput = {
  displayName: string;
  phone: string;
  cpf: string;
  preferredLanguage: string;
  preferredCurrency: string;
};

export type CreateUserAddressInput = {
  label: string;
  street: string;
  number: string;
  neighborhood: string;
  city: string;
  state: string;
  zipCode: string;
  defaultAddress: boolean;
};

type MobilePage<T> = {
  content?: T[];
};

async function apiRequest<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const response = await fetch(`${API_BASE}${path}`, {
    method: options.method ?? "GET",
    headers: {
      "Content-Type": "application/json",
      "X-Tenant-Id": TENANT_HEADER,
      ...(options.token ? { Authorization: `Bearer ${options.token}` } : {}),
    },
    body: options.body ? JSON.stringify(options.body) : undefined,
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || `HTTP ${response.status}`);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return response.json() as Promise<T>;
}

export async function loginDemo(): Promise<AuthSession> {
  return apiRequest<AuthSession>("/v1/auth/login", {
    method: "POST",
    body: {
      tenantSlug: TENANT_SLUG,
      email: DEMO_EMAIL,
      password: DEMO_PASSWORD,
    },
  });
}

export async function fetchCurrentUser(token: string) {
  return apiRequest<{ id: string; email: string; tenantId: string }>("/v1/auth/me", { token });
}

export async function fetchProducts() {
  return apiRequest<MobileProduct[]>("/v1/catalog/products");
}

export async function fetchProduct(id: string) {
  return apiRequest<MobileProduct>(`/v1/catalog/products/${id}`);
}

export async function fetchCart(token: string) {
  return apiRequest<MobileCart>("/v1/cart", { token });
}

export async function addToCart(token: string, productId: string, quantity: number) {
  return apiRequest<MobileCart>("/v1/cart/items", {
    method: "POST",
    token,
    body: { productId, quantity },
  });
}

export async function removeFromCart(token: string, itemId: string) {
  return apiRequest<MobileCart>(`/v1/cart/items/${itemId}`, {
    method: "DELETE",
    token,
  });
}

export async function previewCheckout(token: string, cartId: string, paymentMethod: string) {
  return apiRequest<{ total: number; subtotal: number; shipping: number; discount: number }>("/v1/checkout/preview", {
    method: "POST",
    token,
    body: {
      tenantSlug: TENANT_SLUG,
      cartId,
      shippingAddressId: DEFAULT_SHIPPING_ADDRESS_ID,
      paymentMethod,
    },
  });
}

export async function checkoutCart(token: string, cartId: string, paymentMethod: string) {
  return apiRequest<MobileOrderGroup>("/v1/checkout", {
    method: "POST",
    token,
    body: {
      tenantSlug: TENANT_SLUG,
      cartId,
      shippingAddressId: DEFAULT_SHIPPING_ADDRESS_ID,
      paymentMethod,
    },
  });
}

export async function fetchMyOrders(token: string, buyerId: string) {
  return apiRequest<MobileOrderGroup[]>(`/v1/orders/my?buyerId=${buyerId}`, { token });
}

export async function fetchMyChats(token: string, role = "BUYER") {
  return apiRequest<MobileChat[]>(`/v1/chats?role=${role}`, { token });
}

export async function sendChatMessage(token: string, chatId: string, content: string) {
  return apiRequest(`/v1/chats/${chatId}/messages`, {
    method: "POST",
    token,
    body: { content, messageType: "TEXT" },
  });
}

export async function markChatAsRead(token: string, chatId: string) {
  return apiRequest<void>(`/v1/chats/${chatId}/read`, {
    method: "PUT",
    token,
  });
}

export async function fetchNotifications(token: string) {
  const page = await apiRequest<MobilePage<MobileNotification>>(`/v1/notifications?size=50`, { token });
  return page.content ?? [];
}

export async function markNotificationAsRead(token: string, notificationId: string) {
  return apiRequest<void>(`/v1/notifications/${notificationId}/read`, {
    method: "PUT",
    token,
  });
}

export async function markAllNotificationsAsRead(token: string) {
  return apiRequest<void>(`/v1/notifications/read-all`, {
    method: "PUT",
    token,
  });
}

export async function fetchSupportTickets(token: string) {
  const page = await apiRequest<MobilePage<MobileSupportTicket>>(`/v1/support/tickets/mine?size=20`, { token });
  return page.content ?? [];
}

export async function createSupportTicket(token: string, subject: string, category: string, description: string) {
  return apiRequest<MobileSupportTicket>(`/v1/support/tickets`, {
    method: "POST",
    token,
    body: {
      subject,
      description,
      category,
      priority: "MEDIUM",
    },
  });
}

export async function fetchUserProfile(token: string, userId: string) {
  return apiRequest<MobileUserProfile>(`/v1/profile/${userId}`, { token });
}

export async function fetchUserAddresses(token: string, userId: string) {
  return apiRequest<MobileUserAddress[]>(`/v1/profile/${userId}/addresses`, { token });
}

export async function updateUserProfile(token: string, userId: string, input: UpdateUserProfileInput) {
  return apiRequest<MobileUserProfile>(`/v1/profile/${userId}`, {
    method: "PUT",
    token,
    body: input,
  });
}

export async function createUserAddress(token: string, userId: string, input: CreateUserAddressInput) {
  return apiRequest<MobileUserAddress>(`/v1/profile/${userId}/addresses`, {
    method: "POST",
    token,
    body: input,
  });
}

export async function updateUserAddress(token: string, userId: string, addressId: string, input: CreateUserAddressInput) {
  return apiRequest<MobileUserAddress>(`/v1/profile/${userId}/addresses/${addressId}`, {
    method: "PUT",
    token,
    body: input,
  });
}

export async function deleteUserAddress(token: string, userId: string, addressId: string) {
  return apiRequest<void>(`/v1/profile/${userId}/addresses/${addressId}`, {
    method: "DELETE",
    token,
  });
}