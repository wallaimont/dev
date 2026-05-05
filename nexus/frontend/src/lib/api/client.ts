// ============================================================
// NEXUS — API Client (axios + TanStack Query)
// ============================================================

import axios, {
  AxiosInstance,
  AxiosRequestConfig,
  AxiosResponse,
  InternalAxiosRequestConfig,
} from 'axios'

// ---- Token storage (secure httpOnly via Next.js route) -----
const getAccessToken  = () => typeof window !== 'undefined'
  ? localStorage.getItem('nexus_access_token') : null

const setAccessToken  = (t: string) => localStorage.setItem('nexus_access_token', t)
const getRefreshToken = () => typeof window !== 'undefined'
  ? localStorage.getItem('nexus_refresh_token') : null
const setRefreshToken = (t: string) => localStorage.setItem('nexus_refresh_token', t)
const clearTokens     = () => {
  fetch('/api/auth/session', { method: 'DELETE' }).catch(() => {})
  localStorage.removeItem('nexus_access_token')
  localStorage.removeItem('nexus_refresh_token')
}

// ---- Axios instance ----------------------------------------
const api: AxiosInstance = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
    'X-Tenant-ID': process.env.NEXT_PUBLIC_TENANT_DEFAULT ?? 'nexus',
  },
  timeout: 15_000,
})

// Request interceptor — attach JWT
api.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = getAccessToken()
  if (token) config.headers['Authorization'] = `Bearer ${token}`
  return config
})

// Response interceptor — auto-refresh on 401
let isRefreshing = false
let refreshQueue: Array<(token: string) => void> = []

api.interceptors.response.use(
  (r) => r,
  async (error) => {
    const originalRequest = error.config

    if (error.response?.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        return new Promise((resolve) => {
          refreshQueue.push((token) => {
            originalRequest.headers['Authorization'] = `Bearer ${token}`
            resolve(api(originalRequest))
          })
        })
      }

      originalRequest._retry = true
      isRefreshing = true

      try {
        const refreshToken = getRefreshToken()
        if (!refreshToken) throw new Error('No refresh token')

        const { data } = await axios.post(
          `${process.env.NEXT_PUBLIC_API_URL}/api/v1/auth/refresh`,
          { refreshToken },
          { headers: { 'X-Tenant-ID': process.env.NEXT_PUBLIC_TENANT_DEFAULT } }
        )

        setAccessToken(data.accessToken)
        setRefreshToken(data.refreshToken)

        refreshQueue.forEach((cb) => cb(data.accessToken))
        refreshQueue = []

        originalRequest.headers['Authorization'] = `Bearer ${data.accessToken}`
        return api(originalRequest)
      } catch (err) {
        clearTokens()
        if (typeof window !== 'undefined') window.location.href = '/login'
        return Promise.reject(err)
      } finally {
        isRefreshing = false
      }
    }

    return Promise.reject(error)
  }
)

export default api

// ============================================================
// AUTH API
// ============================================================
export const authApi = {
  register: (data: RegisterRequest) =>
    api.post<AuthResponse>('/api/v1/auth/register', data),

  login: (data: LoginRequest) =>
    api.post<AuthResponse>('/api/v1/auth/login', data),

  refresh: (refreshToken: string) =>
    api.post<AuthResponse>('/api/v1/auth/refresh', { refreshToken }),

  logout: (refreshToken: string) =>
    api.post('/api/v1/auth/logout', { refreshToken }),

  forgotPassword: (email: string) =>
    api.post('/api/v1/auth/forgot-password', { email }),

  resetPassword: (token: string, newPassword: string) =>
    api.post('/api/v1/auth/reset-password', { token, newPassword }),
}

// ============================================================
// PRODUCTS API
// ============================================================
export const productsApi = {
  search: (params: ProductSearchParams) =>
    api.get<PageResponse<ProductSummary>>('/api/v1/products', { params }),

  getBySlug: (slug: string) =>
    api.get<ProductDetail>(`/api/v1/products/${slug}`),

  getRelated: (productId: string) =>
    api.get<ProductSummary[]>(`/api/v1/products/${productId}/related`),

  create: (data: CreateProductRequest) =>
    api.post<ProductDetail>('/api/v1/products', data),

  update: (productId: string, data: UpdateProductRequest) =>
    api.put<ProductDetail>(`/api/v1/products/${productId}`, data),

  delete: (productId: string) =>
    api.delete(`/api/v1/products/${productId}`),

  myProducts: (params?: { status?: string; page?: number }) =>
    api.get<PageResponse<ProductSummary>>('/api/v1/products/my', { params }),

  approve: (productId: string) =>
    api.post(`/api/v1/products/${productId}/approve`),

  reject: (productId: string, reason: string) =>
    api.post(`/api/v1/products/${productId}/reject`, { reason }),
}

// ============================================================
// CART API
// ============================================================
export const cartApi = {
  get: (sessionId?: string) =>
    api.get<CartResponse>('/api/v1/cart', {
      headers: sessionId ? { 'X-Session-ID': sessionId } : {},
    }),

  addItem: (data: AddToCartRequest, sessionId?: string) =>
    api.post<CartResponse>('/api/v1/cart/items', data, {
      headers: sessionId ? { 'X-Session-ID': sessionId } : {},
    }),

  updateItem: (itemId: string, quantity: number) =>
    api.put<CartResponse>(`/api/v1/cart/items/${itemId}`, { quantity }),

  removeItem: (itemId: string) =>
    api.delete<void>(`/api/v1/cart/items/${itemId}`),

  applyCoupon: (code: string) =>
    api.post<CartResponse>('/api/v1/cart/coupon', { code }),

  removeCoupon: () =>
    api.delete<CartResponse>('/api/v1/cart/coupon'),

  merge: (sessionId: string) =>
    api.post<CartResponse>('/api/v1/cart/merge', null, {
      headers: { 'X-Session-ID': sessionId },
    }),
}

// ============================================================
// ORDERS API
// ============================================================
export const ordersApi = {
  checkout: (data: CheckoutRequest) =>
    api.post<OrderResponse>('/api/v1/orders/checkout', data),

  list: (params?: OrderListParams) =>
    api.get<PageResponse<OrderSummary>>('/api/v1/orders', { params }),

  get: (orderId: string) =>
    api.get<OrderDetail>(`/api/v1/orders/${orderId}`),

  cancel: (orderId: string, reason: string) =>
    api.post(`/api/v1/orders/${orderId}/cancel`, { reason }),

  sellerList: (params?: { status?: string }) =>
    api.get<PageResponse<OrderSummary>>('/api/v1/orders/seller', { params }),

  ship: (groupId: string, data: ShipRequest) =>
    api.post(`/api/v1/orders/groups/${groupId}/ship`, data),
}

// ============================================================
// PAYMENTS API
// ============================================================
export const paymentsApi = {
  createPix: (orderId: string, idempotencyKey: string) =>
    api.post<PixChargeResponse>(`/api/v1/orders/${orderId}/payments/pix`, null, {
      headers: { 'Idempotency-Key': idempotencyKey },
    }),

  getPixStatus: (txid: string) =>
    api.get<PixStatusResponse>(`/api/v1/payments/pix/${txid}`),

  payWithCard: (orderId: string, data: CardPaymentRequest, idempotencyKey: string) =>
    api.post(`/api/v1/orders/${orderId}/payments/card`, data, {
      headers: { 'Idempotency-Key': idempotencyKey },
    }),

  requestRefund: (paymentId: string, data: RefundRequest) =>
    api.post<RefundResponse>(`/api/v1/payments/${paymentId}/refund`, data),

  myPayouts: () =>
    api.get('/api/v1/seller/payouts'),
}

// ============================================================
// REVIEWS API
// ============================================================
export const reviewsApi = {
  list: (productId: string, params?: { page?: number }) =>
    api.get<PageResponse<Review>>(`/api/v1/products/${productId}/reviews`, { params }),

  submit: (productId: string, data: CreateReviewRequest) =>
    api.post<Review>(`/api/v1/products/${productId}/reviews`, data),

  markHelpful: (reviewId: string) =>
    api.post(`/api/v1/reviews/${reviewId}/helpful`),
}

// ============================================================
// SELLER API
// ============================================================
export const sellerApi = {
  register: (data: RegisterSellerRequest) =>
    api.post('/api/v1/sellers/register', data),

  getProfile: () =>
    api.get('/api/v1/sellers/me'),

  updateProfile: (data: UpdateSellerRequest) =>
    api.put('/api/v1/sellers/me', data),

  updateStore: (data: UpdateStoreRequest) =>
    api.put('/api/v1/sellers/me/store', data),

  getStorePage: (storeSlug: string) =>
    api.get(`/api/v1/sellers/stores/${storeSlug}`),
}

// ============================================================
// NOTIFICATIONS API
// ============================================================
export const notificationsApi = {
  list: () =>
    api.get('/api/v1/notifications'),

  markRead: (notificationId: string) =>
    api.patch(`/api/v1/notifications/${notificationId}/read`),

  markAllRead: () =>
    api.patch('/api/v1/notifications/read-all'),
}

// ============================================================
// ADMIN API
// ============================================================
export const adminApi = {
  dashboard: (period?: string) =>
    api.get('/api/v1/admin/dashboard', { params: { period } }),

  gmv: (from: string, to: string, granularity = 'daily') =>
    api.get('/api/v1/admin/gmv', { params: { from, to, granularity } }),

  listSellers: (params?: { status?: string }) =>
    api.get('/api/v1/sellers/admin', { params }),

  approveSeller: (sellerId: string) =>
    api.post(`/api/v1/sellers/${sellerId}/approve`),

  suspendSeller: (sellerId: string, reason: string) =>
    api.post(`/api/v1/sellers/${sellerId}/suspend`, { reason }),
}

// ============================================================
// TYPE DEFINITIONS
// ============================================================
export interface RegisterRequest {
  email: string; password: string; firstName: string; lastName: string; phone?: string
}
export interface LoginRequest { email: string; password: string; deviceInfo?: string }
export interface AuthResponse {
  accessToken: string; refreshToken: string; tokenType: string
  expiresIn: number; userId: string; tenantId: string; email: string; roles: string[]
}
export interface RefreshRequest { refreshToken: string }

export interface PageResponse<T> {
  content: T[]; totalElements: number; totalPages: number; page: number; size: number
}
export interface ProductSummary {
  id: string; name: string; slug: string; basePrice: number; promotionalPrice?: number
  avgRating: number; totalReviews: number; mainImageUrl?: string; storeName: string
}
export interface ProductDetail extends ProductSummary {
  description: string; variants: ProductVariant[]
  images: ProductImage[]; category: string; brand?: string
}
export interface ProductVariant {
  id: string; sku: string; price: number; attributes?: Record<string, string>; stock: number
}
export interface ProductImage { id: string; url: string; isMain: boolean }
export interface ProductSearchParams {
  q?: string; categoryId?: string; brandId?: string; minPrice?: number
  maxPrice?: number; minRating?: number; sortBy?: string; page?: number; size?: number
}
export interface CreateProductRequest {
  name: string; description: string; categoryId: string; basePrice: number
  currencyCode?: string; variants: ProductVariant[]
}
export interface UpdateProductRequest extends Partial<CreateProductRequest> {}

export interface CartResponse {
  id: string; itemCount: number; subtotal: number; couponCode?: string
  couponDiscount: number; items: CartItem[]
}
export interface CartItem {
  id: string; variantId: string; productId: string; sku: string
  quantity: number; unitPrice: number; lineTotal: number
}
export interface AddToCartRequest { variantId: string; sellerId: string; quantity: number }

export interface CheckoutRequest { shippingAddressId: string; couponCode?: string; notes?: string }
export interface OrderResponse { id: string; orderNumber: string; status: string; total: number }
export interface OrderSummary extends OrderResponse { itemCount: number; createdAt: string }
export interface OrderDetail extends OrderSummary {
  subtotal: number; shippingTotal: number; discountTotal: number
  groups: OrderGroup[]; shippingAddress: Address; history: StatusHistory[]
}
export interface OrderGroup { id: string; storeName: string; status: string; items: OrderItem[] }
export interface OrderItem { productName: string; sku: string; quantity: number; unitPrice: number }
export interface Address { street: string; number: string; city: string; state: string; zipCode: string }
export interface StatusHistory { status: string; comment?: string; createdAt: string }
export interface OrderListParams { page?: number; size?: number }
export interface ShipRequest { trackingCode: string; carrier?: string }

export interface PixChargeResponse {
  paymentId: string; txid: string; qrCode: string; qrCodeUrl?: string
  pixCopyPaste: string; amount: number; status: string; expiresAt: string
}
export interface PixStatusResponse { txid: string; status: string; paidAt?: string }
export interface CardPaymentRequest { cardToken: string; installments: number; holderName: string }
export interface RefundRequest { amount: number; reason: string }
export interface RefundResponse { refundId: string; status: string; amount: number }

export interface Review {
  id: string; rating: number; title?: string; comment?: string
  reviewerName: string; createdAt: string; helpfulCount: number
}
export interface CreateReviewRequest { rating: number; title?: string; comment?: string }

export interface RegisterSellerRequest { sellerType: 'PF' | 'PJ'; document: string; companyName?: string }
export interface UpdateSellerRequest { companyName?: string; tradeName?: string }
export interface UpdateStoreRequest { name: string; description?: string; logoUrl?: string; bannerUrl?: string }
