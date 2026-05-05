package com.orionerp.modules.rh.domain;

import com.orionerp.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "departamentos_rh")
@Getter
@Setter
public class DepartamentoRh extends BaseEntity {

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(nullable = false, length = 20)
    private String codigo;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(name = "centro_custo_id")
    private Long centroCustoId;

    @Column(name = "gestor_id")
    private Long gestorId;
}
