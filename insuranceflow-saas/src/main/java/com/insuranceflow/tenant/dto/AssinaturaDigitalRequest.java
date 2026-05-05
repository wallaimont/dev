package com.insuranceflow.tenant.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.UUID;

@Data
public class AssinaturaDigitalRequest {
    private UUID documentoId;
    private String entidadeTipo;
    private UUID entidadeId;
    @NotBlank private String signatarioNome;
    @NotBlank @Email private String signatarioEmail;
    private String signatarioCpf;
}
