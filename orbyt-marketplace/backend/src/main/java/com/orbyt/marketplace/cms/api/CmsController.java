package com.orbyt.marketplace.cms.api;

import com.orbyt.marketplace.cms.application.CmsService;
import com.orbyt.marketplace.cms.domain.Banner;
import com.orbyt.marketplace.cms.domain.CmsPage;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/cms")
@RequiredArgsConstructor
public class CmsController {

    private final CmsService cmsService;

    @GetMapping("/banners")
    public List<Banner> getBanners(@RequestParam(defaultValue = "HOME_HERO") String position) {
        return cmsService.getBanners(position);
    }

    @PostMapping("/banners")
    @PreAuthorize("hasAuthority('admin.cms.manage')")
    @ResponseStatus(HttpStatus.CREATED)
    public Banner createBanner(@RequestBody Banner banner) {
        return cmsService.createBanner(banner);
    }

    @GetMapping("/pages/{slug}")
    public CmsPage getPage(@PathVariable String slug,
                           @RequestParam(defaultValue = "pt-BR") String lang) {
        return cmsService.getPage(slug, lang);
    }

    @PostMapping("/pages")
    @PreAuthorize("hasAuthority('admin.cms.manage')")
    @ResponseStatus(HttpStatus.CREATED)
    public CmsPage createPage(@RequestBody CmsPage page) {
        return cmsService.createPage(page);
    }

    @PutMapping("/pages/{id}")
    @PreAuthorize("hasAuthority('admin.cms.manage')")
    public CmsPage updatePage(@PathVariable UUID id, @RequestBody CmsPage update) {
        return cmsService.updatePage(id, update);
    }
}
