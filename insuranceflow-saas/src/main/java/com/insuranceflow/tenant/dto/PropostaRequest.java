package com.insuranceflow.tenant.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class PropostaRequest {
    @NotNull private UUID clienteId;
    private UUID seguradoraId;
    private UUID corretoraId;
    private UUID ramoId;
    private String tipoSeguro;
    private LocalDate dataInicioVigencia;
    private LocalDate dataFimVigencia;
    private BigDecimal valorImportanciaSegurada;
    private BigDecimal valorPremio;
    private BigDecimal valorPremioLiquido;
    private BigDecimal valorIof;
    private String formaPagamento;
    private Integer numeroParcelas;
    private String observacoes;
}
