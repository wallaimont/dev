package com.supportdesk.dto.request;

import com.supportdesk.domain.enums.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor
public class UpdateTicketRequest {
    private String title;
    private String description;
    private TicketPriority priority;
    private TicketStatus status;
    private UUID categoryId;
    private UUID assigneeId;
    private List<String> tags;
}
