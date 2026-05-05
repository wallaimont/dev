package com.insuranceflow.tenant.model;

import com.insuranceflow.common.model.TenantBaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ramo_seguro")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RamoSeguro extends TenantBaseEntity {
    private String codigo;
    @Column(nullable = false) private String nome;
    private String descricao;
}
