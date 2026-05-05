package com.orbyt.marketplace.operations.api;

import com.orbyt.marketplace.catalog.repository.ProductRepository;
import com.orbyt.marketplace.catalog.repository.StoreRepository;
import com.orbyt.marketplace.identity.repository.UserRepository;
import com.orbyt.marketplace.shared.tenant.TenantContext;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('admin.dashboard.view')")
    public Map<String, Object> dashboard() {
        UUID tenantId = TenantContext.get();
        if (tenantId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "X-Tenant-Id header is required");
        }
        long productCount = productRepository.findAllByTenantIdAndDeletedAtIsNull(tenantId).size();
        long storeCount = storeRepository.findAllByTenantIdAndDeletedAtIsNull(tenantId).size();
        long userCount = userRepository.findAllByTenantIdAndDeletedAtIsNull(tenantId).size();

        return Map.of(
                "tenantId", tenantId,
                "products", productCount,
                "stores", storeCount,
                "users", userCount,
                "gmv", productCount * 1200.0,
                "riskAlerts", Math.max(0, 8 - (int) storeCount)
        );
    }
}
