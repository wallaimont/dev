package com.orionerp.modules.rh.domain;

import com.orionerp.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "funcionario_beneficio")
@Getter
@Setter
public class FuncionarioBeneficio extends BaseEntity {

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "funcionario_id", nullable = false)
    private Long funcionarioId;

    @Column(name = "beneficio_id", nullable = false)
    private Long beneficioId;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Column(name = "valor_customizado", precision = 18, scale = 2)
    private BigDecimal valorCustomizado;
}
