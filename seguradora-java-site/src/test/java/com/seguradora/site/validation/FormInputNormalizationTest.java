package com.seguradora.site.validation;

import com.seguradora.site.model.LeadRequest;
import com.seguradora.site.service.FormInputNormalizationService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FormInputNormalizationTest {

    private final FormInputNormalizationService service = new FormInputNormalizationService();

    @Test
    void deveAplicarTrimECollapseNosCamposTextuais() {
        LeadRequest request = new LeadRequest();
        request.setNome("   Maria    Souza   ");
        request.setTipoSeguro("   Seguro   Auto   ");
        request.setMensagem("   Linha   com   espacos   ");

        service.normalize(request);

        assertThat(request.getNome()).isEqualTo("Maria Souza");
        assertThat(request.getTipoSeguro()).isEqualTo("Seguro Auto");
        assertThat(request.getMensagem()).isEqualTo("Linha com espacos");
    }

    @Test
    void deveNormalizarEmailParaLowerCaseERemoverVazios() {
        LeadRequest request = new LeadRequest();
        request.setEmail("  CLIENTE@EMAIL.COM  ");
        request.setTelefone("   ");

        service.normalize(request);

        assertThat(request.getEmail()).isEqualTo("cliente@email.com");
        assertThat(request.getTelefone()).isNull();
    }
}
