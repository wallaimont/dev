package com.orionerp.modules.contabilidade.domain;

import com.orionerp.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "plano_contas")
@Getter
@Setter
public class PlanoContas extends BaseEntity {

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(nullable = false, length = 20)
    private String codigo;

    @Column(nullable = false, length = 200)
    private String descricao;

    @Column(nullable = false, length = 1)
    private String tipo; // S=Sintética, A=Analítica

    @Column(nullable = false, length = 1)
    private String natureza; // D=Devedora, C=Credora

    @Column(length = 30)
    private String classificacao;

    @Column(name = "conta_pai_id")
    private Long contaPaiId;

    @Column(nullable = false)
    private Integer nivel = 1;

    @Column(name = "aceita_lancamento", nullable = false)
    private Boolean aceitaLancamento = true;
}
