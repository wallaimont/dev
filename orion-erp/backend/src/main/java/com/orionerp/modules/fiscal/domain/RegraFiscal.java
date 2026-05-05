package com.orionerp.modules.fiscal.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "regras_fiscais")
@Getter
@Setter
public class RegraFiscal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid = UUID.randomUUID();

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "uf_origem", length = 2)
    private String ufOrigem;

    @Column(name = "uf_destino", length = 2)
    private String ufDestino;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ncm_id")
    private Ncm ncm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cfop_id")
    private Cfop cfop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cst_icms_id")
    private Cst cstIcms;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cst_pis_id")
    private Cst cstPis;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cst_cofins_id")
    private Cst cstCofins;

    @Column(name = "aliquota_icms", precision = 7, scale = 4)
    private BigDecimal aliquotaIcms;

    @Column(name = "aliquota_pis", precision = 7, scale = 4)
    private BigDecimal aliquotaPis;

    @Column(name = "aliquota_cofins", precision = 7, scale = 4)
    private BigDecimal aliquotaCofins;

    @Column(name = "aliquota_ipi", precision = 7, scale = 4)
    private BigDecimal aliquotaIpi;

    @Column(name = "reducao_base_icms", precision = 7, scale = 4)
    private BigDecimal reducaoBaseIcms;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
