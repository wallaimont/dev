package com.orbyt.marketplace.catalog.api;

import com.orbyt.marketplace.catalog.api.dto.StoreResponse;
import com.orbyt.marketplace.catalog.api.dto.UpsertStoreRequest;
import com.orbyt.marketplace.catalog.application.StoreService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/catalog/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @GetMapping
    @PreAuthorize("hasAuthority('catalog.manage')")
    public List<StoreResponse> findAll() {
        return storeService.findAll();
    }

    @GetMapping("/{storeId}")
    @PreAuthorize("hasAuthority('catalog.manage')")
    public StoreResponse findById(@PathVariable UUID storeId) {
        return storeService.findById(storeId);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('catalog.manage')")
    public StoreResponse create(@Valid @RequestBody UpsertStoreRequest request) {
        return storeService.create(request);
    }

    @PatchMapping("/{storeId}")
    @PreAuthorize("hasAuthority('catalog.manage')")
    public StoreResponse update(@PathVariable UUID storeId, @Valid @RequestBody UpsertStoreRequest request) {
        return storeService.update(storeId, request);
    }

    @DeleteMapping("/{storeId}")
    @PreAuthorize("hasAuthority('catalog.manage')")
    public void delete(@PathVariable UUID storeId) {
        storeService.delete(storeId);
    }
}
