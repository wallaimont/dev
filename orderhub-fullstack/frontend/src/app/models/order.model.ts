export interface OrderItemRequest {
  productId: number;
  quantity: number;
}

export interface OrderRequest {
  clientId: number;
  items: OrderItemRequest[];
}

export interface Order {
  id: number;
  status: string;
  totalAmount: number;
  integrationStatus: string;
}
