package com.insuranceflow.auth.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data @Builder
public class UsuarioResponse {
    private UUID id;
    private String nome;
    private String email;
    private String perfil;
    private String telefone;
    private String cargo;
    private Boolean active;
}
