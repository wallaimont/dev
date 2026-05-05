package com.insuranceflow.tenant.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class WhiteLabelRequest {
    private String logoUrl;
    private String faviconUrl;
    private String nomeSistema;
    private String corPrimaria;
    private String corSecundaria;
    private String corAcento;
    private String corFundo;
    private String nomePortalCliente;
    private String rodape;
    @Email private String emailSuporte;
    private String telefoneSuporte;
    private String textoInstitucional;
    private String dominioPersonalizado;
    private String subdominio;
}
