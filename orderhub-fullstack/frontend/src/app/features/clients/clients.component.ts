import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ClientService } from '../../core/services/client.service';
import { Client } from '../../models/client.model';

@Component({
  selector: 'app-clients',
  templateUrl: './clients.component.html'
})
export class ClientsComponent implements OnInit {
  clients: Client[] = [];
  private fb = inject(FormBuilder);

  form = this.fb.group({
    name: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    phone: [''],
    document: ['']
  });

  constructor(private clientService: ClientService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.clientService.getAll().subscribe(data => this.clients = data);
  }

  submit(): void {
    if (this.form.invalid) return;
    this.clientService.create({ ...this.form.getRawValue(), active: true } as Client).subscribe(() => {
      this.form.reset();
      this.load();
    });
  }
}
