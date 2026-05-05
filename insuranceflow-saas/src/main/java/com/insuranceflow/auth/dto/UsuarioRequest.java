package com.insuranceflow.auth.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UsuarioRequest {
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 200, message = "Nome deve ter entre 2 e 200 caracteres")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Size(max = 150, message = "Email deve ter no máximo 150 caracteres")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 10, max = 128, message = "Senha deve ter entre 10 e 128 caracteres")
    private String senha;

    @Pattern(regexp = "^(ADMIN_EMPRESA|GERENTE|OPERADOR|CLIENTE)?$", message = "Perfil inválido")
    private String perfil;

    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    @Pattern(regexp = "^[0-9()\\-+ ]*$", message = "Telefone contém caracteres inválidos")
    private String telefone;

    @Size(max = 100, message = "Cargo deve ter no máximo 100 caracteres")
    private String cargo;
}
