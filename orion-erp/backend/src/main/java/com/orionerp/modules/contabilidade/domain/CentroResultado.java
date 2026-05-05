package com.orionerp.modules.contabilidade.domain;

import com.orionerp.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "centro_resultado")
@Getter
@Setter
public class CentroResultado extends BaseEntity {

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(nullable = false, length = 20)
    private String codigo;

    @Column(nullable = false, length = 200)
    private String descricao;

    @Column(nullable = false, length = 20)
    private String tipo = "RESULTADO"; // RESULTADO, INVESTIMENTO, FINANCEIRO

    @Column(length = 100)
    private String responsavel;
}
