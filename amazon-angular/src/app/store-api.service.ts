import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CartResponse, StorefrontResponse } from './store-data';

@Injectable({ providedIn: 'root' })
export class StoreApiService {
  private readonly http = inject(HttpClient);

  getStorefront() {
    return this.http.get<StorefrontResponse>('/api/storefront');
  }

  getCart() {
    return this.http.get<CartResponse>('/api/cart');
  }

  addToCart(productId: number) {
    return this.http.post<CartResponse>('/api/cart/items', { productId });
  }

  clearCart() {
    return this.http.delete<CartResponse>('/api/cart');
  }
}
