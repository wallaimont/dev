package com.insuranceflow.tenant.model;

import com.insuranceflow.common.model.TenantBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "documento")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Documento extends TenantBaseEntity {
    @Column(name = "entidade_tipo", nullable = false) private String entidadeTipo;
    @Column(name = "entidade_id", nullable = false) private UUID entidadeId;
    @Column(nullable = false) private String nome;
    @Column(name = "nome_arquivo", nullable = false) private String nomeArquivo;
    @Column(name = "tipo_arquivo") private String tipoArquivo;
    @Column(name = "tamanho_bytes") private Long tamanhoBytes;
    private String url;
    private String descricao;
}
