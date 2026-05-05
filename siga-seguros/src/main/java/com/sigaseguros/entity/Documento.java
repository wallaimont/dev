package com.sigaseguros.entity;

import com.sigaseguros.enums.TipoDocumento;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "documentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Documento extends BaseEntity {

    @Column(name = "nome_arquivo", nullable = false, length = 300)
    private String nomeArquivo;

    @Column(name = "nome_original", nullable = false, length = 300)
    private String nomeOriginal;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "tamanho")
    private Long tamanho;

    @Column(name = "caminho", nullable = false, length = 500)
    private String caminho;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", length = 30)
    private TipoDocumento tipoDocumento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposta_id")
    private Proposta proposta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apolice_id")
    private Apolice apolice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sinistro_id")
    private Sinistro sinistro;
}
