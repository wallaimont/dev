package com.insuranceflow.master.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.UUID;

@Data
public class AssinaturaRequest {
    @NotNull private UUID empresaId;
    @NotNull private UUID planoId;
    @NotBlank private String ciclo;
    private String gateway;
    private String metodoPagamento;
    private String observacoes;
}
