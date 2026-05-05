package com.orionerp.modules.seguros.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NotificacaoSeguroRequest(
        @NotNull Long empresaId,
        @NotBlank @Size(max = 20) String tipo,
        @NotBlank @Size(max = 200) String titulo,
        String descricao,
        @Size(max = 100) String destinatario,
        @Size(max = 50) String tabelaOrigem,
        Long registroId
) {}
