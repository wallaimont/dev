package com.orbyt.marketplace.cms.domain;

import com.orbyt.marketplace.shared.domain.TenantScopedEntity;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "cms_pages")
public class CmsPage extends TenantScopedEntity {

    @Column
    private String slug;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column
    private String pageType;

    @Column(nullable = false)
    private String language = "pt-BR";

    @Column(nullable = false)
    private boolean isPublished = false;

    @Column
    private OffsetDateTime publishedAt;

    @Column
    private String metaTitle;

    @Column
    private String metaDescription;
}
