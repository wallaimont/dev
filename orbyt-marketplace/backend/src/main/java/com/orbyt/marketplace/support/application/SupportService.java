package com.orbyt.marketplace.support.application;

import com.orbyt.marketplace.support.domain.SupportMessage;
import com.orbyt.marketplace.support.domain.SupportTicket;
import com.orbyt.marketplace.support.repository.SupportTicketRepository;
import com.orbyt.marketplace.shared.tenant.TenantContext;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class SupportService {

    private final SupportTicketRepository ticketRepository;

    @Transactional
    public SupportTicket createTicket(UUID userId, String subject, String description, String category, String priority, UUID orderId) {
        SupportTicket ticket = new SupportTicket();
        ticket.setTenantId(TenantContext.require());
        ticket.setUserId(userId);
        ticket.setSubject(subject);
        ticket.setDescription(description);
        ticket.setCategory(category);
        ticket.setPriority(priority != null ? priority : "MEDIUM");
        ticket.setOrderId(orderId);
        ticket.setStatus("OPEN");
        ticket.setSlaDeadline(OffsetDateTime.now().plusHours(
                "HIGH".equals(priority) ? 4 : "CRITICAL".equals(priority) ? 1 : 24));
        return ticketRepository.save(ticket);
    }

    @Transactional
    public SupportTicket addMessage(UUID ticketId, UUID senderId, String message, boolean internal) {
        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));
        SupportMessage msg = new SupportMessage();
        msg.setTenantId(ticket.getTenantId());
        msg.setTicket(ticket);
        msg.setSenderId(senderId);
        msg.setMessage(message);
        msg.setInternal(internal);
        msg.setStatus("ACTIVE");
        ticket.getMessages().add(msg);

        if ("OPEN".equals(ticket.getStatus())) {
            ticket.setStatus("IN_PROGRESS");
        }
        return ticketRepository.save(ticket);
    }

    @Transactional
    public SupportTicket resolveTicket(UUID ticketId, UUID resolvedBy) {
        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));
        ticket.setStatus("RESOLVED");
        ticket.setResolvedAt(OffsetDateTime.now());
        return ticketRepository.save(ticket);
    }

    public Page<SupportTicket> getUserTickets(UUID userId, Pageable pageable) {
        return ticketRepository.findByTenantIdAndUserIdOrderByCreatedAtDesc(
                TenantContext.require(), userId, pageable);
    }

    public Page<SupportTicket> getAllTickets(Pageable pageable) {
        return ticketRepository.findByTenantIdOrderByCreatedAtDesc(
                TenantContext.require(), pageable);
    }
}
