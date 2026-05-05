package com.insuranceflow.auth.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data @Builder
public class LoginResponse {
    private String token;
    private String refreshToken;
    private String tipo;
    private String nome;
    private String email;
    private String perfil;
    private UUID empresaId;
    private String empresaNome;
}
