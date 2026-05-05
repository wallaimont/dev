package com.orbyt.marketplace.cms.application;

import com.orbyt.marketplace.cms.domain.Banner;
import com.orbyt.marketplace.cms.domain.CmsPage;
import com.orbyt.marketplace.cms.repository.BannerRepository;
import com.orbyt.marketplace.cms.repository.CmsPageRepository;
import com.orbyt.marketplace.shared.tenant.TenantContext;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CmsService {

    private final BannerRepository bannerRepository;
    private final CmsPageRepository cmsPageRepository;

    @Transactional(readOnly = true)
    public List<Banner> getBanners(String position) {
        return bannerRepository.findByTenantIdAndIsActiveTrueAndPositionOrderByDisplayOrderAsc(
                TenantContext.require(), position);
    }

    @Transactional
    public Banner createBanner(Banner banner) {
        banner.setTenantId(TenantContext.require());
        return bannerRepository.save(banner);
    }

    @Transactional(readOnly = true)
    public CmsPage getPage(String slug, String lang) {
        return cmsPageRepository.findByTenantIdAndSlugAndLanguage(TenantContext.require(), slug, lang)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Page not found"));
    }

    @Transactional
    public CmsPage createPage(CmsPage page) {
        page.setTenantId(TenantContext.require());
        applyPageDefaults(page, page.isPublished());
        return cmsPageRepository.save(page);
    }

    @Transactional
    public CmsPage updatePage(UUID id, CmsPage update) {
        CmsPage page = cmsPageRepository.findByIdAndTenantId(id, TenantContext.require())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Page not found"));

        page.setSlug(update.getSlug());
        page.setTitle(update.getTitle());
        page.setContent(update.getContent());
        page.setPageType(update.getPageType());
        page.setLanguage(update.getLanguage());
        page.setMetaTitle(update.getMetaTitle());
        page.setMetaDescription(update.getMetaDescription());
        page.setPublished(update.isPublished());
        applyPageDefaults(page, update.isPublished());

        return cmsPageRepository.save(page);
    }

    private void applyPageDefaults(CmsPage page, boolean published) {
        if (page.getLanguage() == null || page.getLanguage().isBlank()) {
            page.setLanguage("pt-BR");
        }
        if (published) {
            if (page.getPublishedAt() == null) {
                page.setPublishedAt(OffsetDateTime.now());
            }
        } else {
            page.setPublishedAt(null);
        }
    }
}