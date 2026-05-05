package com.supportdesk.service;

import com.supportdesk.config.AppProperties;
import com.supportdesk.domain.entity.*;
import com.supportdesk.domain.enums.*;
import com.supportdesk.dto.request.CreateTicketRequest;
import com.supportdesk.dto.response.TicketResponse;
import com.supportdesk.messaging.OutboxEventPublisher;
import com.supportdesk.repository.*;
import com.supportdesk.security.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock TicketRepository ticketRepository;
    @Mock CategoryRepository categoryRepository;
    @Mock UserRepository userRepository;
    @Mock OutboxEventPublisher outboxPublisher;
    @Mock AuditService auditService;
    @Mock AppProperties appProperties;
    @Mock AppProperties.Sla slaProps;

    @InjectMocks TicketService ticketService;

    @BeforeEach
    void setup() {
        lenient().when(appProperties.getSla()).thenReturn(slaProps);
        lenient().when(slaProps.getMediumHours()).thenReturn(48);
        lenient().when(slaProps.getLowHours()).thenReturn(72);
        lenient().when(slaProps.getHighHours()).thenReturn(24);
        lenient().when(slaProps.getCriticalHours()).thenReturn(4);
    }

    @Test
    void create_setsStatusOpenAndSlaDeadline() {
        UUID catId = UUID.randomUUID();
        Category cat = Category.builder().id(catId).name("Infra").active(true).build();
        User user = buildUser();
        when(categoryRepository.findById(catId)).thenReturn(Optional.of(cat));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(ticketRepository.save(any())).thenAnswer(i -> {
            Ticket t = i.getArgument(0);
            t.setId(UUID.randomUUID());
            t.setTicketNumber("TKT-0001");
            return t;
        });

        CreateTicketRequest req = new CreateTicketRequest("My issue", "Desc", TicketPriority.MEDIUM, catId, List.of("tag1"));
        UserPrincipal principal = UserPrincipal.from(user);

        TicketResponse result = ticketService.create(req, principal);

        assertThat(result.getStatus()).isEqualTo(TicketStatus.OPEN);
        assertThat(result.getSlaDeadline()).isNotNull();
        assertThat(result.getTicketNumber()).isEqualTo("TKT-0001");
        verify(outboxPublisher).publish(eq("Ticket"), any(), eq("TicketCreated"), any(), any());
    }

    private User buildUser() {
        Role r = Role.builder().id(UUID.randomUUID()).name("CUSTOMER").build();
        return User.builder()
            .id(UUID.randomUUID()).name("Alice").email("alice@test.com")
            .passwordHash("h").roles(Set.of(r)).enabled(true).build();
    }
}
