package com.supportdesk.controller;

import com.supportdesk.dto.request.UpdateTicketRequest;
import com.supportdesk.dto.response.ApiResponse;
import com.supportdesk.dto.response.TicketResponse;
import com.supportdesk.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.isNull;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TicketControllerPermissionTest.MethodSecurityTestConfig.class)
@Import(TicketControllerPermissionTest.MethodSecurityTestConfig.class)
class TicketControllerPermissionTest {

    @Autowired TicketController ticketController;

    @Autowired TicketService ticketService;

    private UpdateTicketRequest updateRequest;

    @BeforeEach
    void setUp() {
        updateRequest = new UpdateTicketRequest();
    }

    @Test
    @WithMockUser(roles = {"AGENT"})
    void update_shouldAllowAgent() throws Exception {
        UUID ticketId = UUID.randomUUID();
        when(ticketService.update(eq(ticketId), any(), any())).thenReturn(TicketResponse.builder()
                .id(ticketId)
                .ticketNumber("TKT-101")
                .title("Title")
                .build());

            var response = ticketController.update(ticketId, updateRequest, null);

            assertThat(response.getStatusCode()).isEqualTo(OK);
            assertThat(response.getBody()).isNotNull();
            verify(ticketService).update(eq(ticketId), eq(updateRequest), isNull());
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void update_shouldDenyCustomer() throws Exception {
        UUID ticketId = UUID.randomUUID();

            assertThatThrownBy(() -> ticketController.update(ticketId, updateRequest, null))
                .isInstanceOf(AccessDeniedException.class);

            verifyNoInteractions(ticketService);
    }

    @Configuration
    @EnableMethodSecurity
    static class MethodSecurityTestConfig {
            @Bean
            TicketService ticketService() {
                return mock(TicketService.class);
            }

            @Bean
            TicketController ticketController(TicketService ticketService) {
                return new TicketController(ticketService);
            }
    }
}
