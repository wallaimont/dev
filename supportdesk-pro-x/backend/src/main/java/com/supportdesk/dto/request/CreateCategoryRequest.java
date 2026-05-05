package com.supportdesk.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class CreateCategoryRequest {
    @NotBlank @Size(max = 120)
    private String name;
    private String description;
    private String slaHours;
}
