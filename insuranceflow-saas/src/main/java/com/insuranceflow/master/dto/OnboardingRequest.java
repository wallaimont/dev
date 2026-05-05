package com.insuranceflow.master.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class OnboardingRequest {
    @NotBlank(message = "Razão social é obrigatória")
    @Size(max = 200) private String razaoSocial;

    @NotBlank(message = "Nome fantasia é obrigatório")
    @Size(max = 200) private String nomeFantasia;

    @NotBlank(message = "CNPJ é obrigatório")
    @Pattern(regexp = "^\\d{2}\\.?\\d{3}\\.?\\d{3}/?\\d{4}-?\\d{2}$", message = "CNPJ inválido")
    private String cnpj;

    @NotBlank(message = "Email é obrigatório")
    @Email @Size(max = 150) private String email;

    @Size(max = 20)
    @Pattern(regexp = "^[0-9()\\-+ ]*$", message = "Telefone inválido")
    private String telefone;

    @NotBlank(message = "Nome do administrador é obrigatório")
    @Size(max = 200) private String nomeAdmin;

    @NotBlank(message = "Email do administrador é obrigatório")
    @Email @Size(max = 150) private String emailAdmin;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 10, max = 128, message = "Senha deve ter entre 10 e 128 caracteres")
    private String senhaAdmin;

    private String planoEscolhido;
}
