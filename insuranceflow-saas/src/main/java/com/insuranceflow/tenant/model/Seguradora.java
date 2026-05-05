package com.insuranceflow.tenant.model;

import com.insuranceflow.common.model.TenantBaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seguradora")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Seguradora extends TenantBaseEntity {

    @Column(nullable = false)
    private String nome;
    private String cnpj;
    @Column(name = "codigo_susep") private String codigoSusep;
    private String email;
    private String telefone;
    private String website;
    @Column(name = "logo_url") private String logoUrl;
    @Column(name = "contato_nome") private String contatoNome;
    @Column(name = "contato_email") private String contatoEmail;
    @Column(name = "contato_telefone") private String contatoTelefone;
    private String observacoes;
}
