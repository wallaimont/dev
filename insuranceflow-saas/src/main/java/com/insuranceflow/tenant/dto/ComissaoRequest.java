package com.insuranceflow.tenant.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class ComissaoRequest {
    private UUID apoliceId;
    private UUID corretoraId;
    private UUID seguradoraId;
    @NotBlank private String tipo;
    private BigDecimal percentual;
    @NotNull private BigDecimal valor;
    private LocalDate dataReferencia;
    private String observacoes;
}
