import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { of, throwError } from 'rxjs';
import { TicketListComponent } from './ticket-list.component';
import { TicketService } from '../../../core/services/ticket.service';

describe('TicketListComponent', () => {
  let fixture: ComponentFixture<TicketListComponent>;
  let component: TicketListComponent;
  let ticketService: jasmine.SpyObj<TicketService>;

  beforeEach(async () => {
    ticketService = jasmine.createSpyObj<TicketService>('TicketService', ['list']);

    await TestBed.configureTestingModule({
      imports: [TicketListComponent],
      providers: [provideNoopAnimations(), { provide: TicketService, useValue: ticketService }]
    }).compileComponents();

    fixture = TestBed.createComponent(TicketListComponent);
    component = fixture.componentInstance;
  });

  it('deve carregar chamados com sucesso', () => {
    ticketService.list.and.returnValue(
      of({ content: [], page: 0, size: 20, totalElements: 3, totalPages: 1 })
    );

    fixture.detectChanges();

    expect(ticketService.list).toHaveBeenCalledWith(0, 20);
    expect(component.totalElements).toBe(3);
    expect(component.loading).toBeFalse();
  });

  it('deve encerrar loading em caso de falha', () => {
    ticketService.list.and.returnValue(throwError(() => new Error('boom')));

    fixture.detectChanges();

    expect(component.loading).toBeFalse();
    expect(component.tickets.length).toBe(0);
  });
});
