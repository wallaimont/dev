package com.insuranceflow.tenant.model;

import com.insuranceflow.common.model.TenantBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "corretora")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Corretora extends TenantBaseEntity {

    @Column(nullable = false)
    private String nome;
    private String cnpj;
    private String susep;
    private String email;
    private String telefone;
    private String responsavel;
    @Column(name = "comissao_padrao") private BigDecimal comissaoPadrao;
    private String observacoes;
}
