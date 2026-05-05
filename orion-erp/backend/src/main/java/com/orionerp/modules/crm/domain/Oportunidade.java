package com.orionerp.modules.crm.domain;

import com.orionerp.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "oportunidades")
@Getter
@Setter
public class Oportunidade extends BaseEntity {

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(nullable = false, length = 200)
    private String titulo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id")
    private Lead lead;

    @Column(name = "cliente_id")
    private Long clienteId;

    @Column(name = "responsavel_id")
    private Long responsavelId;

    @Column(name = "valor_estimado", precision = 18, scale = 2)
    private BigDecimal valorEstimado;

    private Integer probabilidade;

    @Column(name = "etapa_funil", nullable = false, length = 30)
    private String etapaFunil = "PROSPECCAO";

    @Column(name = "data_previsao_fechamento")
    private LocalDate dataPrevisaoFechamento;

    @Column(name = "motivo_perda", length = 300)
    private String motivoPerda;

    @Column(columnDefinition = "TEXT")
    private String observacao;
}
