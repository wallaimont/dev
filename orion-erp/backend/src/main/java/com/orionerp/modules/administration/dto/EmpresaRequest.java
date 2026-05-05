package com.orionerp.modules.administration.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmpresaRequest(
        @NotBlank @Size(max = 20) String codigo,
        @NotBlank @Size(max = 200) String razaoSocial,
        @Size(max = 200) String nomeFantasia,
        @NotBlank @Size(max = 18) String cnpj,
        @Email @Size(max = 150) String email,
        @Size(max = 100) String cidade,
        @Size(max = 2) String uf,
        String observacao,
        Boolean ativo
) {
}
