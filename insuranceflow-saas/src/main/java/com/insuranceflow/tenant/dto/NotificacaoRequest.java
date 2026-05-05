package com.insuranceflow.tenant.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.UUID;

@Data
public class NotificacaoRequest {
    private UUID usuarioId;
    @NotBlank private String titulo;
    @NotBlank private String mensagem;
    private String tipo;
    private String link;
}
