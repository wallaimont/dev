package com.example.orderhub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClientRequest(
        @NotBlank String name,
        @Email @NotBlank String email,
        String phone,
        String document,
        Boolean active
) {}
