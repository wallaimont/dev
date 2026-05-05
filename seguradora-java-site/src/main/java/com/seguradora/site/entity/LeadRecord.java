package com.seguradora.site.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

import java.time.LocalDateTime;

@Entity
public class LeadRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, length = 160)
    private String email;

    @Column(length = 30)
    private String telefone;

    @Column(length = 80)
    private String tipoSeguro;

    @Column(length = 40)
    private String cobertura;

    private Double valorBem;

    @Column(length = 1000)
    private String mensagem;

    @Column(nullable = false, length = 30)
    private String origem;

    private Double valorMensalEstimado;

    private Double franquiaEstimada;

    @Column(length = 60)
    private String prazoRetorno;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Double getValorMensalEstimado() {
        return valorMensalEstimado;
    }

    public void setValorMensalEstimado(Double valorMensalEstimado) {
        this.valorMensalEstimado = valorMensalEstimado;
    }

    public Double getFranquiaEstimada() {
        return franquiaEstimada;
    }

    public void setFranquiaEstimada(Double franquiaEstimada) {
        this.franquiaEstimada = franquiaEstimada;
    }

    public String getPrazoRetorno() {
        return prazoRetorno;
    }

    public void setPrazoRetorno(String prazoRetorno) {
        this.prazoRetorno = prazoRetorno;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}