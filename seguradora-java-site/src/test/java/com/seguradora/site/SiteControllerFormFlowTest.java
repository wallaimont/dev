package com.seguradora.site;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SiteControllerFormFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveAceitarCotacaoValida() throws Exception {
        mockMvc.perform(post("/cotacao")
                        .param("nome", "Cliente Cotacao")
                        .param("email", "cotacao@email.com")
                        .param("telefone", "11999999999")
                        .param("tipoSeguro", "Seguro Auto")
                        .param("cobertura", "Completa")
                        .param("valorBem", "75000")
                        .param("mensagem", "Solicitacao de cotacao")
                        .param("origem", "cotacao")
                        .param("submissionToken", "token-cotacao-ok"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Solicitação recebida com sucesso")))
                .andExpect(model().attributeExists("quoteResult"));
    }

    @Test
    void deveBloquearCotacaoInvalidaComErrosPorCampo() throws Exception {
        mockMvc.perform(post("/cotacao")
                        .param("nome", "")
                        .param("email", "email-invalido")
                        .param("tipoSeguro", "")
                        .param("cobertura", "")
                        .param("origem", "cotacao")
                        .param("submissionToken", "token-cotacao-erro"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("fieldErrors"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Revise os campos destacados")));
    }

    @Test
    void deveAceitarContatoValido() throws Exception {
        mockMvc.perform(post("/contato")
                        .param("nome", "Cliente Contato")
                        .param("email", "contato@email.com")
                        .param("telefone", "11988888888")
                        .param("tipoSeguro", "Seguro de Vida")
                        .param("mensagem", "Mensagem valida")
                        .param("origem", "contato")
                        .param("submissionToken", "token-contato-ok"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Mensagem enviada")));
    }

    @Test
    void deveBloquearContatoInvalidoComErrosPorCampo() throws Exception {
        mockMvc.perform(post("/contato")
                        .param("nome", "")
                        .param("email", "invalido")
                        .param("mensagem", "")
                        .param("origem", "contato")
                        .param("submissionToken", "token-contato-erro"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("fieldErrors"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Revise os campos destacados")));
    }

    @Test
    void deveBloquearSubmissaoDuplicadaNoContato() throws Exception {
        mockMvc.perform(post("/contato")
                        .param("nome", "Cliente Duplicado")
                        .param("email", "duplicado@email.com")
                        .param("mensagem", "Primeiro envio")
                        .param("origem", "contato")
                        .param("submissionToken", "token-duplicado"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/contato")
                        .param("nome", "Cliente Duplicado")
                        .param("email", "duplicado@email.com")
                        .param("mensagem", "Segundo envio")
                        .param("origem", "contato")
                        .param("submissionToken", "token-duplicado"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("envio duplicado")));
    }
}
