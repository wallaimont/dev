package com.seguradora.site.auth;

import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
        @NotBlank(message = "Usuário é obrigatório")
        String username,
        @NotBlank(message = "Senha é obrigatória")
        String password
) {
}
