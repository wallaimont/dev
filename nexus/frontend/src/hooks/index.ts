// ============================================================
// NEXUS — TanStack Query Hooks
// ============================================================

import {
  useQuery, useMutation, useQueryClient,
  UseQueryOptions, UseMutationOptions,
  keepPreviousData,
} from '@tanstack/react-query'
import { AxiosError } from 'axios'
import {
  productsApi, ordersApi, cartApi, paymentsApi, reviewsApi, sellerApi,
  type ProductSearchParams, type CheckoutRequest, type AddToCartRequest,
  type CreateReviewRequest,
} from '@/lib/api/client'
import { useCartStore, useAuthStore } from '@/store'
import toast from 'react-hot-toast'

// ---- Query Keys ----
export const queryKeys = {
  products: {
    all:    () => ['products'] as const,
    search: (p: ProductSearchParams) => ['products', 'search', p] as const,
    detail: (slug: string) => ['products', 'detail', slug] as const,
    myList: (status?: string) => ['products', 'my', status] as const,
    related:(id: string) => ['products', 'related', id] as const,
  },
  cart: {
    active: () => ['cart'] as const,
  },
  orders: {
    list:   (p?: object) => ['orders', p] as const,
    detail: (id: string) => ['orders', id] as const,
    seller: (p?: object) => ['orders', 'seller', p] as const,
  },
  payments: {
    pix:    (txid: string) => ['payments', 'pix', txid] as const,
    payouts:() => ['payments', 'payouts'] as const,
  },
  reviews: {
    product:(id: string, p?: object) => ['reviews', id, p] as const,
  },
  seller: {
    profile:() => ['seller', 'profile'] as const,
    store:  (slug: string) => ['seller', 'store', slug] as const,
  },
  notifications: () => ['notifications'] as const,
}

// ============================================================
// PRODUCTS
// ============================================================
export function useProductSearch(params: ProductSearchParams) {
  return useQuery({
    queryKey: queryKeys.products.search(params),
    queryFn:  () => productsApi.search(params).then(r => r.data),
    placeholderData: keepPreviousData,
    staleTime: 60_000,
  })
}

export function useProduct(slug: string, options?: UseQueryOptions<any>) {
  return useQuery({
    queryKey: queryKeys.products.detail(slug),
    queryFn:  () => productsApi.getBySlug(slug).then(r => r.data),
    enabled: !!slug,
    staleTime: 30_000,
    ...options,
  })
}

export function useRelatedProducts(productId: string) {
  return useQuery({
    queryKey: queryKeys.products.related(productId),
    queryFn:  () => productsApi.getRelated(productId).then(r => r.data),
    enabled: !!productId,
    staleTime: 120_000,
  })
}

export function useMyProducts(status?: string) {
  return useQuery({
    queryKey: queryKeys.products.myList(status),
    queryFn:  () => productsApi.myProducts({ status }).then(r => r.data),
  })
}

export function useCreateProduct() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: productsApi.create,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: queryKeys.products.myList() })
      toast.success('Produto criado com sucesso!')
    },
    onError: (err: AxiosError<any>) => {
      toast.error(err.response?.data?.detail ?? 'Erro ao criar produto')
    },
  })
}

export function useUpdateProduct(productId: string) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (data: any) => productsApi.update(productId, data),
    onSuccess: (_, __, ctx) => {
      qc.invalidateQueries({ queryKey: queryKeys.products.all() })
      toast.success('Produto atualizado!')
    },
  })
}

// ============================================================
// CART
// ============================================================
export function useCart() {
  const sessionId = useCartStore(s => s.sessionId)
  return useQuery({
    queryKey: queryKeys.cart.active(),
    queryFn:  () => cartApi.get(sessionId).then(r => r.data),
    staleTime: 30_000,
  })
}

export function useAddToCart() {
  const qc = useQueryClient()
  const setCart = useCartStore(s => s.setCart)
  const openCart = useCartStore(s => s.openCart)
  const sessionId = useCartStore(s => s.sessionId)

  return useMutation({
    mutationFn: (req: AddToCartRequest) => cartApi.addItem(req, sessionId).then(r => r.data),
    onSuccess: (cart) => {
      setCart(cart)
      qc.setQueryData(queryKeys.cart.active(), cart)
      openCart()
      toast.success('Item adicionado ao carrinho!')
    },
    onError: (err: AxiosError<any>) => {
      toast.error(err.response?.data?.detail ?? 'Erro ao adicionar item')
    },
  })
}

export function useUpdateCartItem() {
  const qc = useQueryClient()
  const setCart = useCartStore(s => s.setCart)
  return useMutation({
    mutationFn: ({ itemId, quantity }: { itemId: string; quantity: number }) =>
      cartApi.updateItem(itemId, quantity).then(r => r.data),
    onSuccess: (cart) => {
      setCart(cart)
      qc.setQueryData(queryKeys.cart.active(), cart)
    },
  })
}

export function useRemoveCartItem() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (itemId: string) => cartApi.removeItem(itemId),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: queryKeys.cart.active() })
    },
  })
}

export function useApplyCoupon() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (code: string) => cartApi.applyCoupon(code).then(r => r.data),
    onSuccess: (cart) => {
      qc.setQueryData(queryKeys.cart.active(), cart)
      toast.success('Cupom aplicado com sucesso!')
    },
    onError: () => toast.error('Cupom inválido ou expirado'),
  })
}

// ============================================================
// ORDERS
// ============================================================
export function useMyOrders(params?: object) {
  return useQuery({
    queryKey: queryKeys.orders.list(params),
    queryFn:  () => ordersApi.list(params).then(r => r.data),
    placeholderData: keepPreviousData,
  })
}

export function useOrderDetail(orderId: string) {
  return useQuery({
    queryKey: queryKeys.orders.detail(orderId),
    queryFn:  () => ordersApi.get(orderId).then(r => r.data),
    enabled: !!orderId,
  })
}

export function useCheckout() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (data: CheckoutRequest) => ordersApi.checkout(data).then(r => r.data),
    onSuccess: (order) => {
      qc.invalidateQueries({ queryKey: queryKeys.cart.active() })
      qc.invalidateQueries({ queryKey: queryKeys.orders.list() })
    },
    onError: (err: AxiosError<any>) => {
      toast.error(err.response?.data?.detail ?? 'Erro no checkout')
    },
  })
}

export function useCancelOrder() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ orderId, reason }: { orderId: string; reason: string }) =>
      ordersApi.cancel(orderId, reason),
    onSuccess: (_, { orderId }) => {
      qc.invalidateQueries({ queryKey: queryKeys.orders.detail(orderId) })
      qc.invalidateQueries({ queryKey: queryKeys.orders.list() })
      toast.success('Pedido cancelado')
    },
  })
}

export function useSellerOrders(params?: object) {
  return useQuery({
    queryKey: queryKeys.orders.seller(params),
    queryFn:  () => ordersApi.sellerList(params).then(r => r.data),
    placeholderData: keepPreviousData,
  })
}

// ============================================================
// PAYMENTS — PIX
// ============================================================
export function useCreatePix() {
  return useMutation({
    mutationFn: ({ orderId, idempotencyKey }: { orderId: string; idempotencyKey: string }) =>
      paymentsApi.createPix(orderId, idempotencyKey).then(r => r.data),
  })
}

export function usePixStatus(txid: string, enabled = true) {
  return useQuery({
    queryKey: queryKeys.payments.pix(txid),
    queryFn:  () => paymentsApi.getPixStatus(txid).then(r => r.data),
    enabled: enabled && !!txid,
    refetchInterval: (query) => {
      // Poll every 3 seconds while pending
      if (query.state.data?.status === 'PAID') return false
      return 3_000
    },
    staleTime: 0,
  })
}

// ============================================================
// REVIEWS
// ============================================================
export function useProductReviews(productId: string, page = 0) {
  return useQuery({
    queryKey: queryKeys.reviews.product(productId, { page }),
    queryFn:  () => reviewsApi.list(productId, { page }).then(r => r.data),
    enabled: !!productId,
    placeholderData: keepPreviousData,
  })
}

export function useSubmitReview(productId: string) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (data: CreateReviewRequest) =>
      reviewsApi.submit(productId, data).then(r => r.data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: queryKeys.reviews.product(productId) })
      qc.invalidateQueries({ queryKey: queryKeys.products.detail(productId) })
      toast.success('Avaliação enviada com sucesso!')
    },
  })
}
