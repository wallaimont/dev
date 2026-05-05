package com.orbyt.marketplace.identity.domain;

import com.orbyt.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "user_profiles")
@Getter @Setter @NoArgsConstructor
public class UserProfile extends BaseEntity {

    @Column(nullable = false, unique = true)
    private UUID userId;

    @Column(nullable = false)
    private UUID tenantId;

    @Column(length = 100)
    private String displayName;

    @Column(length = 20)
    private String phone;

    @Column(length = 14)
    private String cpf;

    private LocalDate birthDate;

    @Column(length = 500)
    private String avatarUrl;

    @Column(length = 10)
    private String preferredLanguage = "pt-BR";

    @Column(length = 3)
    private String preferredCurrency = "BRL";
}
