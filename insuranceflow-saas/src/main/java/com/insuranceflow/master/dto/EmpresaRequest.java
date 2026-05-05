package com.insuranceflow.master.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.UUID;

@Data
public class EmpresaRequest {
    @NotBlank @Size(max = 200) private String razaoSocial;
    @NotBlank @Size(max = 200) private String nomeFantasia;
    @NotBlank @Pattern(regexp = "^\\d{2}\\.?\\d{3}\\.?\\d{3}/?\\d{4}-?\\d{2}$", message = "CNPJ inválido")
    private String cnpj;
    @Size(max = 30) private String inscricaoEstadual;
    @NotBlank @Email @Size(max = 150) private String email;
    @Size(max = 20) @Pattern(regexp = "^[0-9()\\-+ ]*$", message = "Telefone inválido") private String telefone;
    @Size(max = 20) @Pattern(regexp = "^[0-9()\\-+ ]*$", message = "Celular inválido") private String celular;
    @Size(max = 200) private String website;
    @Size(max = 10) @Pattern(regexp = "^[0-9\\-]*$", message = "CEP inválido") private String cep;
    @Size(max = 200) private String logradouro;
    @Size(max = 20) private String numero;
    @Size(max = 100) private String complemento;
    @Size(max = 100) private String bairro;
    @Size(max = 100) private String cidade;
    @Size(max = 2) @Pattern(regexp = "^[A-Z]{0,2}$", message = "Estado inválido") private String estado;
    private UUID planoId;
}
