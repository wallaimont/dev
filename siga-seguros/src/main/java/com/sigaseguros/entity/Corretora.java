package com.sigaseguros.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "corretoras")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Corretora extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(nullable = false, unique = true, length = 18)
    private String cnpj;

    @Column(length = 150)
    private String responsavel;

    @Column(length = 20)
    private String telefone;

    @Column(length = 200)
    private String email;

    @Column(name = "percentual_comissao", precision = 5, scale = 2)
    private BigDecimal percentualComissao;

    @Column(columnDefinition = "TEXT")
    private String observacoes;
}
