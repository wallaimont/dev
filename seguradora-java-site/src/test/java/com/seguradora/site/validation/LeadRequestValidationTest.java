package com.seguradora.site.validation;

import com.seguradora.site.model.LeadRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class LeadRequestValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void cleanupValidator() {
        validatorFactory.close();
    }

    @Test
    void deveValidarCamposObrigatoriosNomeEEmail() {
        LeadRequest request = new LeadRequest();
        request.setNome("   ");
        request.setEmail(" ");

        Set<String> messages = validator.validate(request)
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());

        assertThat(messages).contains("{lead.nome.required}", "{lead.email.required}");
    }

    @Test
    void deveValidarFormatoDeEmailInvalido() {
        LeadRequest request = new LeadRequest();
        request.setNome("Cliente Teste");
        request.setEmail("invalido");

        Set<String> messages = validator.validate(request)
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());

        assertThat(messages).contains("{lead.email.invalid}");
    }

    @Test
    void deveValidarLimitesDeNomeETelefone() {
        LeadRequest request = new LeadRequest();
        request.setNome("AB");
        request.setEmail("cliente@email.com");
        request.setTelefone("1234567890123456789012345678901");

        Set<String> messages = validator.validate(request)
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());

        assertThat(messages).contains("{lead.nome.size}", "{lead.telefone.size}");
    }
}
