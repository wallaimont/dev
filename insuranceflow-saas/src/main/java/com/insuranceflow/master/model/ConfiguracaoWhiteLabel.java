package com.insuranceflow.master.model;

import com.insuranceflow.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "configuracao_white_label")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConfiguracaoWhiteLabel extends BaseEntity {

    @Column(name = "empresa_id", nullable = false, unique = true)
    private UUID empresaId;

    @Column(name = "logo_url") private String logoUrl;
    @Column(name = "favicon_url") private String faviconUrl;
    @Column(name = "nome_sistema") private String nomeSistema;
    @Column(name = "cor_primaria") private String corPrimaria;
    @Column(name = "cor_secundaria") private String corSecundaria;
    @Column(name = "cor_acento") private String corAcento;
    @Column(name = "cor_fundo") private String corFundo;
    @Column(name = "nome_portal_cliente") private String nomePortalCliente;
    private String rodape;
    @Column(name = "email_suporte") private String emailSuporte;
    @Column(name = "telefone_suporte") private String telefoneSuporte;
    @Column(name = "texto_institucional") private String textoInstitucional;
    @Column(name = "dominio_personalizado") private String dominioPersonalizado;
    private String subdominio;
}
