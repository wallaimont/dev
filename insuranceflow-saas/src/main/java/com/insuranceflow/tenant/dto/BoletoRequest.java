package com.insuranceflow.tenant.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class BoletoRequest {
    private UUID lancamentoId;
    private UUID apoliceId;
    private UUID clienteId;
    private Integer numeroParcela;
    @NotNull private BigDecimal valor;
    @NotNull private LocalDate dataVencimento;
    private String linhaDigitavel;
    private String codigoBarras;
    private String urlBoleto;
    private String nossoNumero;
    private String observacoes;
}
