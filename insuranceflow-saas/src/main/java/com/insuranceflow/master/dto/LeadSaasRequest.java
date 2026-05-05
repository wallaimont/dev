package com.insuranceflow.master.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class LeadSaasRequest {
    @NotBlank private String nome;
    private String empresaNome;
    @NotBlank @Email private String email;
    private String telefone;
    private String origem;
    private String interesse;
    private String observacoes;
}
