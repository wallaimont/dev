package com.insuranceflow.tenant.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class LancamentoFinanceiroRequest {
    @NotBlank private String tipo;
    private String categoria;
    @NotBlank private String descricao;
    @NotNull private BigDecimal valor;
    @NotNull private LocalDate dataVencimento;
    private LocalDate dataPagamento;
    private String formaPagamento;
    private UUID clienteId;
    private UUID apoliceId;
    private UUID propostaId;
    private String observacoes;
}
