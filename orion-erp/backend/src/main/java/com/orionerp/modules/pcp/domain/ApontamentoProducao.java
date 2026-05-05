package com.orionerp.modules.pcp.domain;

import com.orionerp.common.TenantEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "apontamento_producao")
@Getter
@Setter
public class ApontamentoProducao extends TenantEntity {

    @Column(name = "ordem_producao_id", nullable = false)
    private Long ordemProducaoId;

    @Column(name = "funcionario_id")
    private Long funcionarioId;

    @Column(name = "data_apontamento")
    private LocalDate dataApontamento = LocalDate.now();

    @Column(name = "data_inicio")
    private LocalDateTime dataInicio;

    @Column(name = "data_fim")
    private LocalDateTime dataFim;

    @Column(name = "quantidade_produzida", precision = 18, scale = 6)
    private BigDecimal quantidadeProduzida;

    @Column(name = "quantidade_rejeitada", precision = 18, scale = 6)
    private BigDecimal quantidadeRejeitada;

    @Column(length = 50)
    private String maquina;

    @Column(columnDefinition = "TEXT")
    private String observacao;
}
