package com.seguradora.site;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SiteControllerTest {

    @Autowired
    private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

    @Test
    void deveCarregarPaginasPrincipais() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Nova Vida Seguros")));

        mockMvc.perform(get("/seguros"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Portfólio completo")));

        mockMvc.perform(get("/cotacao"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Simulador online")));

        mockMvc.perform(get("/contato"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Atendimento consultivo")));
    }

    @Test
    void deveProcessarFormularioDeCotacao() throws Exception {
        mockMvc.perform(post("/cotacao")
                        .param("nome", "Teste Cliente")
                        .param("email", "teste@email.com")
                        .param("telefone", "11999999999")
                        .param("tipoSeguro", "Seguro Auto")
                        .param("cobertura", "Completa")
                        .param("valorBem", "75000")
                        .param("mensagem", "Teste de cotacao")
                        .param("origem", "cotacao"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Solicitação recebida com sucesso")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Resumo inicial")));
    }

    @Test
    void deveProcessarFormularioDeContato() throws Exception {
        mockMvc.perform(post("/contato")
                        .param("nome", "Teste Contato")
                        .param("email", "contato@email.com")
                        .param("telefone", "11988888888")
                        .param("tipoSeguro", "Seguro de Vida")
                        .param("mensagem", "Mensagem de teste")
                        .param("origem", "contato"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Mensagem enviada")));
    }

    @Test
    void deveRetornarErroDeValidacaoNaCotacaoComNomeVazio() throws Exception {
        mockMvc.perform(post("/cotacao")
                        .param("nome", "")
                        .param("email", "teste@email.com")
                        .param("telefone", "11999999999")
                        .param("tipoSeguro", "Seguro Auto")
                        .param("cobertura", "Completa")
                        .param("valorBem", "75000")
                        .param("origem", "cotacao"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Nome")))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("Solicitação recebida com sucesso"))));
    }

    @Test
    void deveRetornarErroDeValidacaoNaCotacaoComEmailInvalido() throws Exception {
        mockMvc.perform(post("/cotacao")
                        .param("nome", "Teste Cliente")
                        .param("email", "nao-eh-email")
                        .param("telefone", "11999999999")
                        .param("tipoSeguro", "Seguro Auto")
                        .param("cobertura", "Completa")
                        .param("valorBem", "75000")
                        .param("origem", "cotacao"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("E-mail")))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("Solicitação recebida com sucesso"))));
    }

    @Test
    void deveRetornarErroDeValidacaoNoContatoComNomeVazio() throws Exception {
        mockMvc.perform(post("/contato")
                        .param("nome", "")
                        .param("email", "contato@email.com")
                        .param("mensagem", "Mensagem de teste")
                        .param("origem", "contato"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Nome")))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("Mensagem enviada"))));
    }

    @Test
    void deveRetornarErroDeValidacaoNoContatoComEmailInvalido() throws Exception {
        mockMvc.perform(post("/contato")
                        .param("nome", "Teste Contato")
                        .param("email", "invalido")
                        .param("mensagem", "Mensagem de teste")
                        .param("origem", "contato"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("E-mail")))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("Mensagem enviada"))));
    }

        @Test
        void deveCriarCotacaoViaApi() throws Exception {
                String token = obterToken();

                mockMvc.perform(post("/api/cotacoes")
                                                .header("Authorization", "Bearer " + token)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("""
                                                                {
                                                                        "nome": "Cliente API",
                                                                        "email": "cliente.api@email.com",
                                                                        "telefone": "11911112222",
                                                                        "tipoSeguro": "Seguro Auto",
                                                                        "cobertura": "Premium",
                                                                        "valorBem": 120000,
                                                                        "mensagem": "Solicitação via API"
                                                                }
                                                                """))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.tipoSeguro").value("Seguro Auto"))
                                .andExpect(jsonPath("$.cobertura").value("Premium"))
                                .andExpect(jsonPath("$.valorMensal").exists());
        }

        @Test
        void deveCriarContatoViaApiEListarLeads() throws Exception {
                String token = obterToken();

                mockMvc.perform(post("/api/contatos")
                                                .header("Authorization", "Bearer " + token)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("""
                                                                {
                                                                        "nome": "Contato API",
                                                                        "email": "contato.api@email.com",
                                                                        "telefone": "11933334444",
                                                                        "tipoSeguro": "Seguro de Vida",
                                                                        "mensagem": "Mensagem enviada pela API"
                                                                }
                                                                """))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.nome").value("Contato API"))
                                .andExpect(jsonPath("$.origem").value("contato"));

                mockMvc.perform(get("/api/leads")
                                                .header("Authorization", "Bearer " + token))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").exists())
                                .andExpect(jsonPath("$[0].email").exists());
        }

        @Test
        void deveCadastrarNovoUsuarioViaApiComTokenAdmin() throws Exception {
                String adminToken = obterToken();

                mockMvc.perform(post("/api/auth/register")
                                                .header("Authorization", "Bearer " + adminToken)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("""
                                                                {
                                                                        "username": "consultor",
                                                                        "password": "consultor123",
                                                                        "role": "USER"
                                                                }
                                                                """))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.username").value("consultor"))
                                .andExpect(jsonPath("$.role").value("USER"));

                String userToken = obterToken("consultor", "consultor123");

                mockMvc.perform(get("/api/leads")
                                                .header("Authorization", "Bearer " + userToken))
                                .andExpect(status().isOk());
        }

        @Test
        void deveOperarMockDoProtheus() throws Exception {
                String token = obterToken();

                mockMvc.perform(delete("/api/mock/protheus/leads")
                                                .header("Authorization", "Bearer " + token))
                                .andExpect(status().isNoContent());

                mockMvc.perform(get("/api/mock/protheus/health")
                                                .header("Authorization", "Bearer " + token))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.sistema").value("PROTHEUS-MOCK"))
                                .andExpect(jsonPath("$.totalLeadsRecebidos").value(0));

                mockMvc.perform(post("/api/mock/protheus/leads")
                                                .header("Authorization", "Bearer " + token)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("""
                                                                {
                                                                        "nome": "Lead Protheus",
                                                                        "email": "lead.protheus@email.com",
                                                                        "telefone": "11977778888",
                                                                        "tipoSeguro": "Seguro Empresarial",
                                                                        "mensagem": "Lead enviado ao mock"
                                                                }
                                                                """))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.protocolo").value("PTM-000001"))
                                .andExpect(jsonPath("$.status").value("RECEBIDO"))
                                .andExpect(jsonPath("$.origem").value("integracao-teste"));

                mockMvc.perform(get("/api/mock/protheus/leads")
                                                .header("Authorization", "Bearer " + token))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].sistema").value("PROTHEUS-MOCK"))
                                .andExpect(jsonPath("$[0].nome").value("Lead Protheus"));
        }

        private String obterToken() throws Exception {
                return obterToken("admin", "admin123");
        }

        private String obterToken(String username, String password) throws Exception {
                String response = mockMvc.perform(post("/api/auth/login")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("""
                                                                {
                                                                        "username": "%s",
                                                                        "password": "%s"
                                                                }
                                                                """.formatted(username, password)))
                                .andExpect(status().isOk())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                JsonNode json = objectMapper.readTree(response);
                return json.get("token").asText();
        }
}
