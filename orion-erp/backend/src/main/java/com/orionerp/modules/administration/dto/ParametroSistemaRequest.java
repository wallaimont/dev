package com.orionerp.modules.administration.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ParametroSistemaRequest(
        Long empresaId,
        Long filialId,
        @NotBlank @Size(max = 100) String chave,
        String valor,
        @NotBlank @Size(max = 30) String tipo,
        @Size(max = 300) String descricao,
        @Size(max = 50) String modulo,
        Boolean editavel,
        Boolean ativo
) {
}
