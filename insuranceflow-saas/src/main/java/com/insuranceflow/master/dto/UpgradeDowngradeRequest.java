package com.insuranceflow.master.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.UUID;

@Data
public class UpgradeDowngradeRequest {
    @NotNull private UUID empresaId;
    @NotNull private UUID novoPlanoId;
    @NotBlank private String ciclo;
    private String observacoes;
}
