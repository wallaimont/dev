package com.supportdesk.dto.response;

import com.supportdesk.domain.enums.*;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TicketResponse {
    private UUID id;
    private String ticketNumber;
    private String title;
    private String description;
    private TicketStatus status;
    private TicketPriority priority;
    private List<String> tags;
    private UserSummaryResponse requester;
    private UserSummaryResponse assignee;
    private CategoryResponse category;
    private Instant slaDeadline;
    private boolean slaBreached;
    private Instant resolvedAt;
    private Instant closedAt;
    private Instant createdAt;
    private Instant updatedAt;
}
