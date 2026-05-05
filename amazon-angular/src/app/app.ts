import { isPlatformBrowser } from '@angular/common';
import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { PLATFORM_ID } from '@angular/core';
import { StoreApiService } from './store-api.service';
import {
  createStorefrontResponse,
  featuredMetrics,
  navigation,
  Product,
  products
} from './store-data';

@Component({
  selector: 'app-root',
  imports: [],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App implements OnDestroy, OnInit {
  private readonly platformId = inject(PLATFORM_ID);
  private readonly storeApi = inject(StoreApiService);
  protected navigation = [...navigation];
  protected products: Product[] = [...products];
  protected featuredMetrics = [...featuredMetrics];

  protected cartCount = 0;
  protected cartItems: Product[] = [];
  protected cartOpen = false;
  protected dealDeadline = new Date(createStorefrontResponse().dealDeadline);
  protected timeLeft = this.calculateTimeLeft();

  private countdownInterval: number | null = null;

  constructor() {
    if (isPlatformBrowser(this.platformId)) {
      this.countdownInterval = window.setInterval(() => {
        this.timeLeft = this.calculateTimeLeft();
      }, 1000);
    }
  }

  ngOnInit(): void {
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

    this.storeApi.getStorefront().subscribe((storefront) => {
      this.navigation = storefront.navigation;
      this.featuredMetrics = storefront.featuredMetrics;
      this.products = storefront.products;
      this.dealDeadline = new Date(storefront.dealDeadline);
      this.timeLeft = this.calculateTimeLeft();
    });

    this.storeApi.getCart().subscribe((cart) => {
      this.cartItems = cart.items;
      this.cartCount = cart.count;
    });
  }

  protected get deals(): Product[] {
    return this.products.filter((product) => product.section === 'deals');
  }

  protected get bestSellers(): Product[] {
    return this.products.filter((product) => product.section === 'best-sellers');
  }

  protected get continueShopping(): Product[] {
    return this.products.filter((product) => product.section === 'continue-shopping');
  }

  protected get cartTotal(): number {
    return this.cartItems.reduce((total, product) => total + product.discountPrice, 0);
  }

  protected addToCart(product: Product): void {
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

    this.storeApi.addToCart(product.id).subscribe((cart) => {
      this.cartItems = cart.items;
      this.cartCount = cart.count;
      this.cartOpen = true;
    });
  }

  protected clearCart(): void {
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

    this.storeApi.clearCart().subscribe((cart) => {
      this.cartItems = cart.items;
      this.cartCount = cart.count;
      this.cartOpen = false;
    });
  }

  protected toggleCart(): void {
    this.cartOpen = !this.cartOpen;
  }

  protected closeCart(): void {
    this.cartOpen = false;
  }

  protected formatPrice(value: number): string {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value);
  }

  protected stars(rating: number): string {
    return '★'.repeat(rating) + '☆'.repeat(5 - rating);
  }

  ngOnDestroy(): void {
    if (this.countdownInterval !== null) {
      clearInterval(this.countdownInterval);
    }
  }

  private calculateTimeLeft(): { hours: string; minutes: string; seconds: string } {
    const difference = Math.max(this.dealDeadline.getTime() - Date.now(), 0);
    const hours = Math.floor(difference / (1000 * 60 * 60));
    const minutes = Math.floor((difference % (1000 * 60 * 60)) / (1000 * 60));
    const seconds = Math.floor((difference % (1000 * 60)) / 1000);

    return {
      hours: String(hours).padStart(2, '0'),
      minutes: String(minutes).padStart(2, '0'),
      seconds: String(seconds).padStart(2, '0')
    };
  }
}
