package com.supportdesk.dto.request;

import com.supportdesk.domain.enums.TicketPriority;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor
public class CreateTicketRequest {
    @NotBlank @Size(max = 240)
    private String title;
    @NotBlank
    private String description;
    @NotNull
    private TicketPriority priority;
    @NotNull
    private UUID categoryId;
    private List<String> tags;
}
