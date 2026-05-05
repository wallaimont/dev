package com.insuranceflow.tenant.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.UUID;

@Data
public class WhatsappRequest {
    private UUID clienteId;
    @NotBlank private String telefoneDestino;
    @NotBlank private String mensagem;
    private String tipo;
}
