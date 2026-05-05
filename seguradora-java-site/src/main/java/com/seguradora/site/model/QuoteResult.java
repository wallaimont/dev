package com.seguradora.site.model;

public class QuoteResult {

    private final String tipoSeguro;
    private final String cobertura;
    private final double valorMensal;
    private final double franquia;
    private final String prazoRetorno;

    public QuoteResult(String tipoSeguro, String cobertura, double valorMensal, double franquia, String prazoRetorno) {
        this.tipoSeguro = tipoSeguro;
        this.cobertura = cobertura;
        this.valorMensal = valorMensal;
        this.franquia = franquia;
        this.prazoRetorno = prazoRetorno;
    }

    public String getTipoSeguro() {
        return tipoSeguro;
    }

    public String getCobertura() {
        return cobertura;
    }

    public double getValorMensal() {
        return valorMensal;
    }

    public double getFranquia() {
        return franquia;
    }

    public String getPrazoRetorno() {
        return prazoRetorno;
    }
}
