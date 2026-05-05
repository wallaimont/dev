package com.orbyt.marketplace.platform.api;

import com.orbyt.marketplace.platform.api.dto.TenantResponse;
import com.orbyt.marketplace.platform.api.dto.TenantUpsertRequest;
import com.orbyt.marketplace.platform.application.TenantService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/platform/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @GetMapping
    @PreAuthorize("hasAuthority('platform.tenants.manage')")
    public List<TenantResponse> findAll() {
        return tenantService.findAll();
    }

    @GetMapping("/{tenantId}")
    @PreAuthorize("hasAuthority('platform.tenants.manage')")
    public TenantResponse findById(@PathVariable UUID tenantId) {
        return tenantService.findById(tenantId);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('platform.tenants.manage')")
    public TenantResponse create(@Valid @RequestBody TenantUpsertRequest request) {
        return tenantService.create(request);
    }

    @PatchMapping("/{tenantId}")
    @PreAuthorize("hasAuthority('platform.tenants.manage')")
    public TenantResponse update(@PathVariable UUID tenantId, @Valid @RequestBody TenantUpsertRequest request) {
        return tenantService.update(tenantId, request);
    }

    @DeleteMapping("/{tenantId}")
    @PreAuthorize("hasAuthority('platform.tenants.manage')")
    public void deactivate(@PathVariable UUID tenantId) {
        tenantService.deactivate(tenantId);
    }
}
