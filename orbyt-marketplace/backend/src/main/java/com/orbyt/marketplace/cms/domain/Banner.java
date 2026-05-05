package com.orbyt.marketplace.cms.domain;

import com.orbyt.marketplace.shared.domain.TenantScopedEntity;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "banners")
public class Banner extends TenantScopedEntity {

    @Column
    private String title;

    @Column
    private String subtitle;

    @Column
    private String imageUrl;

    @Column
    private String linkUrl;

    @Column
    private String position;

    @Column(nullable = false)
    private int displayOrder = 0;

    @Column
    private OffsetDateTime startsAt;

    @Column
    private OffsetDateTime endsAt;

    @Column(nullable = false)
    private boolean isActive = true;

    @Column(nullable = false)
    private String language = "pt-BR";
}
