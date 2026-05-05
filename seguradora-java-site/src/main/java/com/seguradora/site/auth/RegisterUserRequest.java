package com.seguradora.site.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @NotBlank(message = "Usuário é obrigatório")
        @Size(min = 3, max = 80, message = "Usuário deve ter entre 3 e 80 caracteres")
        String username,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, max = 120, message = "Senha deve ter entre 6 e 120 caracteres")
        String password,

        @NotBlank(message = "Perfil é obrigatório")
        @Pattern(regexp = "ADMIN|USER", message = "Perfil deve ser ADMIN ou USER")
        String role
) {
}
