package com.supportdesk.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class CreateCommentRequest {
    @NotBlank
    private String content;
    private boolean internal = false;
}
