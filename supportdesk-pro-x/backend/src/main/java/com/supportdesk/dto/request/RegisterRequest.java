package com.supportdesk.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class RegisterRequest {
    @NotBlank @Size(min = 2, max = 120)
    private String fullName;
    @NotBlank @Email
    private String email;
    @NotBlank @Size(min = 8, max = 64)
    private String password;
}
