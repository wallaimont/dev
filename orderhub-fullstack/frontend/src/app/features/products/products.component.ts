import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ProductService } from '../../core/services/product.service';
import { Product } from '../../models/product.model';

@Component({
  selector: 'app-products',
  templateUrl: './products.component.html'
})
export class ProductsComponent implements OnInit {
  products: Product[] = [];
  private fb = inject(FormBuilder);

  form = this.fb.group({
    name: ['', Validators.required],
    description: [''],
    category: [''],
    price: [0, Validators.required],
    stock: [0, Validators.required]
  });

  constructor(private productService: ProductService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.productService.getAll().subscribe(data => this.products = data);
  }

  submit(): void {
    if (this.form.invalid) return;
    this.productService.create(this.form.getRawValue() as Product).subscribe(() => {
      this.form.reset({ price: 0, stock: 0 });
      this.load();
    });
  }
}
