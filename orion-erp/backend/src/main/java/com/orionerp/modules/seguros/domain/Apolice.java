package com.orionerp.modules.seguros.domain;

import com.orionerp.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "apolices")
@Getter
@Setter
public class Apolice extends BaseEntity {

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "filial_id")
    private Long filialId;

    @Column(nullable = false, length = 30)
    private String numero;

    @Column(name = "proposta_id")
    private Long propostaId;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "seguradora_id", nullable = false)
    private Long seguradoraId;

    @Column(name = "corretora_id")
    private Long corretoraId;

    @Column(nullable = false, length = 50)
    private String ramo;

    @Column(name = "vigencia_inicio", nullable = false)
    private LocalDate vigenciaInicio;

    @Column(name = "vigencia_fim", nullable = false)
    private LocalDate vigenciaFim;

    @Column(name = "premio_total", nullable = false)
    private BigDecimal premioTotal = BigDecimal.ZERO;

    @Column(name = "importancia_segurada", nullable = false)
    private BigDecimal importanciaSegurada = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal franquia = BigDecimal.ZERO;

    @Column(name = "percentual_comissao", nullable = false)
    private BigDecimal percentualComissao = BigDecimal.ZERO;

    @Column(name = "certificado_inclusao", length = 50)
    private String certificadoInclusao;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @Column(nullable = false, length = 20)
    private String status = "ATIVA";
}
