package com.supportdesk.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class RefreshTokenRequest {
    @NotBlank
    private String refreshToken;
}
