package com.orionerp.modules.administration.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(
        @NotNull Long empresaId,
        Long filialId,
        @NotNull Long perfilId,
        @NotBlank @Size(max = 200) String nome,
        @NotBlank @Email @Size(max = 150) String email,
        @Size(min = 8, max = 100) String senha,
        @Size(max = 20) String telefone,
        Boolean trocarSenha,
        Boolean ativo
) {
}
