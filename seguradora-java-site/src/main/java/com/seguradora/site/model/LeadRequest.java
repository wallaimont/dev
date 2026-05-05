package com.seguradora.site.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LeadRequest {

    @NotBlank(message = "{lead.nome.required}")
    @Size(min = 3, max = 120, message = "{lead.nome.size}")
    private String nome;

    @NotBlank(message = "{lead.email.required}")
    @Email(message = "{lead.email.invalid}")
    private String email;

    @Size(max = 30, message = "{lead.telefone.size}")
    private String telefone;

    private String tipoSeguro;
    private String cobertura;

    @DecimalMin(value = "0.0", inclusive = false, message = "{lead.valorBem.positive}")
    private Double valorBem;

    @Size(max = 1000, message = "{lead.mensagem.size}")
    private String mensagem;
    private String origem;
    private String submissionToken;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getTipoSeguro() {
        return tipoSeguro;
    }

    public void setTipoSeguro(String tipoSeguro) {
        this.tipoSeguro = tipoSeguro;
    }

    public String getCobertura() {
        return cobertura;
    }

    public void setCobertura(String cobertura) {
        this.cobertura = cobertura;
    }

    public Double getValorBem() {
        return valorBem;
    }

    public void setValorBem(Double valorBem) {
        this.valorBem = valorBem;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public String getSubmissionToken() {
        return submissionToken;
    }

    public void setSubmissionToken(String submissionToken) {
        this.submissionToken = submissionToken;
    }
}
