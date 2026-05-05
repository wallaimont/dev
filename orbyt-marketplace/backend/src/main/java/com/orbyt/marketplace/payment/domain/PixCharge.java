package com.orbyt.marketplace.payment.domain;

import com.orbyt.marketplace.shared.domain.TenantScopedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "pix_charges")
public class PixCharge extends TenantScopedEntity {

    @Column(nullable = false)
    private UUID paymentId;

    @Column
    private String txid;

    @Column(columnDefinition = "TEXT")
    private String qrCode;

    @Column(columnDefinition = "TEXT")
    private String qrCodeImage;

    @Column(columnDefinition = "TEXT")
    private String copyPasteCode;

    @Column
    private BigDecimal amount;

    @Column(nullable = false)
    private OffsetDateTime expiresAt;

    @Column
    private OffsetDateTime paidAt;

    @Column
    private String payerCpf;

    @Column
    private String payerName;

    @Column
    private String endToEndId;
}
