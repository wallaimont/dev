package com.orbyt.marketplace.support.api;

import com.orbyt.marketplace.identity.application.AuthUserPrincipal;
import com.orbyt.marketplace.support.api.dto.CreateTicketRequest;
import com.orbyt.marketplace.support.api.dto.AddMessageRequest;
import com.orbyt.marketplace.support.application.SupportService;
import com.orbyt.marketplace.support.domain.SupportTicket;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/support/tickets")
@RequiredArgsConstructor
public class SupportController {

    private final SupportService supportService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SupportTicket createTicket(@AuthenticationPrincipal AuthUserPrincipal principal,
                                      @Valid @RequestBody CreateTicketRequest request) {
        return supportService.createTicket(principal.getUserId(), request.subject(),
                request.description(), request.category(), request.priority(), request.orderId());
    }

    @GetMapping("/mine")
    public Page<SupportTicket> getMyTickets(@AuthenticationPrincipal AuthUserPrincipal principal,
                                             Pageable pageable) {
        return supportService.getUserTickets(principal.getUserId(), pageable);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('admin.support.view')")
    public Page<SupportTicket> getAllTickets(Pageable pageable) {
        return supportService.getAllTickets(pageable);
    }

    @PostMapping("/{ticketId}/messages")
    public SupportTicket addMessage(@AuthenticationPrincipal AuthUserPrincipal principal,
                                    @PathVariable UUID ticketId,
                                    @Valid @RequestBody AddMessageRequest request) {
        return supportService.addMessage(ticketId, principal.getUserId(), request.message(), request.internal());
    }

    @PutMapping("/{ticketId}/resolve")
    @PreAuthorize("hasAuthority('admin.support.manage')")
    public SupportTicket resolveTicket(@AuthenticationPrincipal AuthUserPrincipal principal,
                                       @PathVariable UUID ticketId) {
        return supportService.resolveTicket(ticketId, principal.getUserId());
    }
}
