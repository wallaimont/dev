package com.insuranceflow.tenant.model;

import com.insuranceflow.common.model.TenantBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "renovacao")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Renovacao extends TenantBaseEntity {

    @Column(name = "apolice_id", nullable = false) private UUID apoliceId;
    @Column(name = "cliente_id", nullable = false) private UUID clienteId;
    @Column(nullable = false) private String status;
    @Column(name = "data_vencimento_original", nullable = false) private LocalDate dataVencimentoOriginal;
    @Column(name = "data_nova_vigencia_inicio") private LocalDate dataNovaVigenciaInicio;
    @Column(name = "data_nova_vigencia_fim") private LocalDate dataNovaVigenciaFim;
    @Column(name = "valor_premio_anterior") private BigDecimal valorPremioAnterior;
    @Column(name = "valor_premio_novo") private BigDecimal valorPremioNovo;
    @Column(name = "numero_nova_apolice") private String numeroNovaApolice;
    private String observacoes;
}
