import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ClientService } from '../../core/services/client.service';
import { ProductService } from '../../core/services/product.service';
import { OrderService } from '../../core/services/order.service';
import { Client } from '../../models/client.model';
import { Product } from '../../models/product.model';
import { Order } from '../../models/order.model';

@Component({
  selector: 'app-orders',
  templateUrl: './orders.component.html'
})
export class OrdersComponent implements OnInit {
  clients: Client[] = [];
  products: Product[] = [];
  orders: Order[] = [];
  integrationMessage = '';
  private fb = inject(FormBuilder);

  form = this.fb.group({
    clientId: [null, Validators.required],
    productId: [null, Validators.required],
    quantity: [1, Validators.required]
  });

  constructor(
    private clientService: ClientService,
    private productService: ProductService,
    private orderService: OrderService
  ) {}

  ngOnInit(): void {
    this.loadAll();
  }

  loadAll(): void {
    this.clientService.getAll().subscribe(data => this.clients = data);
    this.productService.getAll().subscribe(data => this.products = data);
    this.orderService.getAll().subscribe(data => this.orders = data);
  }

  submit(): void {
    if (this.form.invalid) return;
    const value = this.form.getRawValue();
    this.orderService.create({
      clientId: Number(value.clientId),
      items: [{ productId: Number(value.productId), quantity: Number(value.quantity) }]
    }).subscribe(() => {
      this.form.patchValue({ quantity: 1 });
      this.loadAll();
    });
  }

  sendToBilling(orderId: number): void {
    this.orderService.sendToBilling(orderId).subscribe(response => {
      this.integrationMessage = response.message;
      this.loadAll();
    });
  }
}
