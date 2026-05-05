package com.supportdesk.service;

import com.supportdesk.config.AppProperties;
import com.supportdesk.domain.entity.*;
import com.supportdesk.domain.enums.*;
import com.supportdesk.dto.request.*;
import com.supportdesk.dto.response.*;
import com.supportdesk.exception.*;
import com.supportdesk.messaging.OutboxEventPublisher;
import com.supportdesk.repository.*;
import com.supportdesk.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final OutboxEventPublisher outboxPublisher;
    private final AuditService auditService;
    private final AppProperties appProperties;

    @Transactional
    public TicketResponse create(CreateTicketRequest req, UserPrincipal principal) {
        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", req.getCategoryId()));
        User requester = userRepository.findById(principal.getId())
            .orElseThrow(() -> new ResourceNotFoundException("User", principal.getId()));

        Ticket ticket = Ticket.builder()
            .ticketNumber(nextTicketNumber())
                .title(req.getTitle())
                .description(req.getDescription())
                .priority(req.getPriority())
                .status(TicketStatus.OPEN)
                .category(category)
                .reporter(requester)
                .tags(req.getTags() != null ? req.getTags().toArray(new String[0]) : new String[0])
                .slaDeadlineAt(computeSlaDeadline(req.getPriority()))
                .build();
        ticket = ticketRepository.save(ticket);
        log.info("Ticket {} created by {}", ticket.getTicketNumber(), principal.getUsername());

        Map<String, Object> eventPayload = new HashMap<>();
        eventPayload.put("ticketId", ticket.getId());
        eventPayload.put("ticketNumber", ticket.getTicketNumber());
        eventPayload.put("requesterId", requester.getId());

        outboxPublisher.publish("Ticket", ticket.getId(), "TicketCreated", "ticket-events", eventPayload);
        auditService.record("Ticket", ticket.getId(), "CREATE", principal, null, ticket);
        return toResponse(ticket);
    }

    @Transactional(readOnly = true)
    public Page<TicketResponse> findAll(Pageable pageable, UserPrincipal principal) {
        // CUSTOMER sees only own tickets; AGENT+ sees all
        boolean isCustomer = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"));
        Page<Ticket> page = isCustomer
            ? ticketRepository.findAll((root, q, cb) -> cb.equal(root.get("reporter").get("id"), principal.getId()), pageable)
                : ticketRepository.findAll(pageable);
        return page.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public TicketResponse findById(UUID id, UserPrincipal principal) {
        Ticket ticket = getTicketOrThrow(id);
        assertCanView(ticket, principal);
        return toResponse(ticket);
    }

    @Transactional
    public TicketResponse update(UUID id, UpdateTicketRequest req, UserPrincipal principal) {
        Ticket ticket = getTicketOrThrow(id);
        Object before = toResponse(ticket);

        if (req.getTitle() != null)      ticket.setTitle(req.getTitle());
        if (req.getDescription() != null) ticket.setDescription(req.getDescription());
        if (req.getPriority() != null)   ticket.setPriority(req.getPriority());
        if (req.getTags() != null)       ticket.setTags(req.getTags().toArray(new String[0]));
        if (req.getCategoryId() != null) {
            ticket.setCategory(categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", req.getCategoryId())));
        }
        if (req.getStatus() != null && req.getStatus() != ticket.getStatus()) {
            TicketStatus prev = ticket.getStatus();
            ticket.setStatus(req.getStatus());
            if (req.getStatus() == TicketStatus.RESOLVED) ticket.setResolvedAt(Instant.now());
            if (req.getStatus() == TicketStatus.CLOSED)   ticket.setClosedAt(Instant.now());
            outboxPublisher.publish("Ticket", ticket.getId(), "TicketStatusChanged", "ticket-events",
                    Map.of("ticketId", ticket.getId(), "from", prev, "to", req.getStatus()));
        }
        if (req.getAssigneeId() != null) {
            ticket.setAssignee(userRepository.findById(req.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", req.getAssigneeId())));
        }
        ticket = ticketRepository.save(ticket);
        auditService.record("Ticket", ticket.getId(), "UPDATE", principal, before, toResponse(ticket));
        return toResponse(ticket);
    }

    private Ticket getTicketOrThrow(UUID id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", id));
    }

    private void assertCanView(Ticket ticket, UserPrincipal principal) {
        boolean isCustomer = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"));
        if (isCustomer && !ticket.getReporter().getId().equals(principal.getId())) {
            throw new AccessDeniedException("Not your ticket");
        }
    }

    private Instant computeSlaDeadline(TicketPriority priority) {
        AppProperties.Sla sla = appProperties.getSla();
        int hours = switch (priority) {
            case LOW -> sla.getLowHours();
            case MEDIUM -> sla.getMediumHours();
            case HIGH -> sla.getHighHours();
            case CRITICAL -> sla.getCriticalHours();
        };
        return Instant.now().plus(hours, ChronoUnit.HOURS);
    }

    private String nextTicketNumber() {
        long seq = ticketRepository.nextTicketSequence();
        return String.format("TKT-%06d", seq);
    }

    TicketResponse toResponse(Ticket t) {
        return TicketResponse.builder()
                .id(t.getId())
                .ticketNumber(t.getTicketNumber())
                .title(t.getTitle())
                .description(t.getDescription())
                .status(t.getStatus())
                .priority(t.getPriority())
            .tags(Arrays.asList(t.getTags()))
            .requester(toUserSummary(t.getReporter()))
                .assignee(t.getAssignee() != null ? toUserSummary(t.getAssignee()) : null)
                .category(toCategoryResponse(t.getCategory()))
            .slaDeadline(t.getSlaDeadlineAt())
                .slaBreached(t.isSlaBreached())
                .resolvedAt(t.getResolvedAt())
                .closedAt(t.getClosedAt())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build();
    }

    private UserSummaryResponse toUserSummary(User u) {
        if (u == null) return null;
        return UserSummaryResponse.builder()
            .id(u.getId()).fullName(u.getName()).email(u.getEmail()).avatarUrl(u.getAvatarUrl())
                .build();
    }

    private CategoryResponse toCategoryResponse(Category c) {
        if (c == null) return null;
        return CategoryResponse.builder().id(c.getId()).name(c.getName()).build();
    }
}
