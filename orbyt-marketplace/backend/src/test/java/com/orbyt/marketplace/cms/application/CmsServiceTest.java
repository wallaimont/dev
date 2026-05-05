package com.orbyt.marketplace.cms.application;

import com.orbyt.marketplace.cms.domain.Banner;
import com.orbyt.marketplace.cms.domain.CmsPage;
import com.orbyt.marketplace.cms.repository.BannerRepository;
import com.orbyt.marketplace.cms.repository.CmsPageRepository;
import com.orbyt.marketplace.shared.tenant.TenantContext;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CmsServiceTest {

    @Mock
    private BannerRepository bannerRepository;

    @Mock
    private CmsPageRepository cmsPageRepository;

    @InjectMocks
    private CmsService cmsService;

    private final UUID tenantId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        TenantContext.set(tenantId);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void shouldCreatePublishedPageWithTenantDefaults() {
        CmsPage page = new CmsPage();
        page.setTitle("Landing page");
        page.setPublished(true);

        when(cmsPageRepository.save(any(CmsPage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CmsPage saved = cmsService.createPage(page);

        assertThat(saved.getTenantId()).isEqualTo(tenantId);
        assertThat(saved.getLanguage()).isEqualTo("pt-BR");
        assertThat(saved.getPublishedAt()).isNotNull();
        verify(cmsPageRepository).save(page);
    }

    @Test
    void shouldReturnActiveBannersByPositionInsideTenantScope() {
        Banner heroBanner = new Banner();
        heroBanner.setTitle("Hero");
        heroBanner.setPosition("HOME_HERO");
        List<Banner> banners = List.of(heroBanner);

        when(bannerRepository.findByTenantIdAndIsActiveTrueAndPositionOrderByDisplayOrderAsc(tenantId, "HOME_HERO"))
                .thenReturn(banners);

        List<Banner> result = cmsService.getBanners("HOME_HERO");

        assertThat(result).containsExactly(heroBanner);
        verify(bannerRepository).findByTenantIdAndIsActiveTrueAndPositionOrderByDisplayOrderAsc(tenantId, "HOME_HERO");
    }

    @Test
    void shouldCreateBannerInsideTenantScope() {
        Banner banner = new Banner();
        banner.setTitle("Campaign");

        when(bannerRepository.save(any(Banner.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Banner saved = cmsService.createBanner(banner);

        assertThat(saved.getTenantId()).isEqualTo(tenantId);
        verify(bannerRepository).save(banner);
    }

    @Test
    void shouldGetPageInsideTenantScope() {
        CmsPage page = new CmsPage();
        page.setSlug("home");
        page.setLanguage("pt-BR");

        when(cmsPageRepository.findByTenantIdAndSlugAndLanguage(tenantId, "home", "pt-BR"))
                .thenReturn(Optional.of(page));

        CmsPage result = cmsService.getPage("home", "pt-BR");

        assertThat(result).isSameAs(page);
        verify(cmsPageRepository).findByTenantIdAndSlugAndLanguage(tenantId, "home", "pt-BR");
    }

    @Test
    void shouldThrowWhenPageIsNotFoundBySlug() {
        when(cmsPageRepository.findByTenantIdAndSlugAndLanguage(tenantId, "missing", "pt-BR"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> cmsService.getPage("missing", "pt-BR"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404 NOT_FOUND")
                .hasMessageContaining("Page not found");
    }

    @Test
    void shouldUpdatePageInsideTenantScope() {
        UUID pageId = UUID.randomUUID();
        CmsPage existing = new CmsPage();
        existing.setTenantId(tenantId);
        existing.setTitle("Old title");
        existing.setPublished(false);

        CmsPage update = new CmsPage();
        update.setSlug("home");
        update.setTitle("New title");
        update.setContent("<h1>Updated</h1>");
        update.setPageType("LANDING");
        update.setLanguage("en-US");
        update.setMetaTitle("Meta title");
        update.setMetaDescription("Meta description");
        update.setPublished(true);

        when(cmsPageRepository.findByIdAndTenantId(pageId, tenantId)).thenReturn(Optional.of(existing));
        when(cmsPageRepository.save(any(CmsPage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CmsPage saved = cmsService.updatePage(pageId, update);

        assertThat(saved.getSlug()).isEqualTo("home");
        assertThat(saved.getTitle()).isEqualTo("New title");
        assertThat(saved.getContent()).isEqualTo("<h1>Updated</h1>");
        assertThat(saved.getPageType()).isEqualTo("LANDING");
        assertThat(saved.getLanguage()).isEqualTo("en-US");
        assertThat(saved.getMetaTitle()).isEqualTo("Meta title");
        assertThat(saved.getMetaDescription()).isEqualTo("Meta description");
        assertThat(saved.isPublished()).isTrue();
        assertThat(saved.getPublishedAt()).isNotNull();
        verify(cmsPageRepository).findByIdAndTenantId(pageId, tenantId);
        verify(cmsPageRepository).save(existing);
    }

    @Test
    void shouldThrowWhenUpdatingPageOutsideTenantScope() {
        UUID pageId = UUID.randomUUID();
        CmsPage update = new CmsPage();

        when(cmsPageRepository.findByIdAndTenantId(pageId, tenantId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cmsService.updatePage(pageId, update))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404 NOT_FOUND")
                .hasMessageContaining("Page not found");
    }
}