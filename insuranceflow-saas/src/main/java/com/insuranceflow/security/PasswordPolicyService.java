package com.insuranceflow.security;

import com.insuranceflow.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PasswordPolicyService {

    @Value("${app.security.password.min-length:10}")
    private int minLength;

    public void validate(String password) {
        List<String> violations = new ArrayList<>();

        if (password == null || password.length() < minLength) {
            violations.add("Senha deve ter no mínimo " + minLength + " caracteres");
        }
        if (password != null) {
            if (!password.matches(".*[A-Z].*")) {
                violations.add("Senha deve conter pelo menos uma letra maiúscula");
            }
            if (!password.matches(".*[a-z].*")) {
                violations.add("Senha deve conter pelo menos uma letra minúscula");
            }
            if (!password.matches(".*\\d.*")) {
                violations.add("Senha deve conter pelo menos um número");
            }
            if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
                violations.add("Senha deve conter pelo menos um caractere especial");
            }
        }

        if (!violations.isEmpty()) {
            throw new BusinessException("Política de senha não atendida: " + String.join("; ", violations));
        }
    }
}
