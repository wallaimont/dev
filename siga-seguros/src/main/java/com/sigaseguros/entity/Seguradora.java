package com.sigaseguros.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "seguradoras")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seguradora extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(nullable = false, unique = true, length = 18)
    private String cnpj;

    @Column(name = "codigo_interno", length = 20)
    private String codigoInterno;

    @Column(length = 20)
    private String telefone;

    @Column(length = 200)
    private String email;

    @Column(name = "contato_comercial", length = 150)
    private String contatoComercial;

    @Column(name = "percentual_comissao_padrao", precision = 5, scale = 2)
    private BigDecimal percentualComissaoPadrao;
}
