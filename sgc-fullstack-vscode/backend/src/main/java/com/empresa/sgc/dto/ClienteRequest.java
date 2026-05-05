package com.empresa.sgc.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClienteRequest {
    @NotBlank
    private String nome;
    private String email;
    private String telefone;
    private String documento;
    private String endereco;
}
