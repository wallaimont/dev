package com.orbyt.marketplace.support.application;

import com.orbyt.marketplace.support.domain.SupportTicket;
import com.orbyt.marketplace.support.repository.SupportTicketRepository;
import com.orbyt.marketplace.shared.tenant.TenantContext;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupportServiceTest {

    @Mock
    private SupportTicketRepository ticketRepository;

    @InjectMocks
    private SupportService supportService;

    private final UUID tenantId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        TenantContext.set(tenantId);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void shouldCreateHighPriorityTicketWithTenantScope() {
        OffsetDateTime before = OffsetDateTime.now();

        when(ticketRepository.save(any(SupportTicket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SupportTicket ticket = supportService.createTicket(
                UUID.randomUUID(),
                "Pagamento não aprovado",
                "Pedido falhou no checkout",
                "PAYMENT",
                "HIGH",
                UUID.randomUUID()
        );

        assertThat(ticket.getTenantId()).isEqualTo(tenantId);
        assertThat(ticket.getStatus()).isEqualTo("OPEN");
        assertThat(ticket.getPriority()).isEqualTo("HIGH");
        assertThat(ticket.getSlaDeadline()).isAfter(before.plusHours(3));
        assertThat(ticket.getSlaDeadline()).isBefore(before.plusHours(5));
    }
}