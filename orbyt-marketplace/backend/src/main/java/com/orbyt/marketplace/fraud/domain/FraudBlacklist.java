package com.orbyt.marketplace.fraud.domain;

import com.orbyt.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "fraud_blacklist")
@Getter @Setter @NoArgsConstructor
public class FraudBlacklist extends BaseEntity {

    @Column(nullable = false)
    private UUID tenantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "blacklist_type", nullable = false, length = 30)
    private BlacklistType type;

    @Column(nullable = false, length = 500)
    private String value;

    @Column(length = 500)
    private String reason;

    @Column(nullable = false)
    private boolean active = true;

    public enum BlacklistType {
        EMAIL, CPF, IP_ADDRESS, DEVICE_FINGERPRINT, PHONE
    }
}
