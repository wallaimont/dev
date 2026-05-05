package com.orionerp.modules.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AtividadeCrmRequest(
    @NotNull Long empresaId,
    @NotBlank String tipo,
    @NotBlank String titulo,
    String descricao,
    Long leadId,
    Long oportunidadeId,
    Long clienteId,
    Long responsavelId,
    @NotNull LocalDateTime dataHora,
    Integer duracaoMinutos,
    Boolean concluida
) {}
