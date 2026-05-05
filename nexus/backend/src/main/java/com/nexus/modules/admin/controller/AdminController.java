package com.nexus.modules.admin.controller;

import com.nexus.modules.admin.dto.*;
import com.nexus.modules.admin.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin", description = "Admin dashboard and platform management")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('TENANT_ADMIN','SUPER_ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get admin dashboard KPIs")
    public ResponseEntity<DashboardKpiResponse> getDashboard(
            @RequestParam(required = false) String period) {
        return ResponseEntity.ok(adminService.getDashboardKpis(period));
    }

    @GetMapping("/gmv")
    @Operation(summary = "Get GMV metrics")
    public ResponseEntity<GmvResponse> getGmv(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam(required = false, defaultValue = "daily") String granularity) {
        return ResponseEntity.ok(adminService.getGmv(from, to, granularity));
    }

    @GetMapping("/fraud-analysis")
    @Operation(summary = "List fraud analysis records")
    public ResponseEntity<?> getFraudAnalysis() {
        return ResponseEntity.ok(adminService.listFraudAnalysis());
    }

    @GetMapping("/audit-logs")
    @Operation(summary = "Get audit logs")
    public ResponseEntity<?> getAuditLogs(
            @RequestParam(required = false) String resource,
            @RequestParam(required = false) String action) {
        return ResponseEntity.ok(adminService.getAuditLogs(resource, action));
    }

    @GetMapping("/tenants")
    @Operation(summary = "List tenants (super admin)")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<java.util.List<TenantSummary>> listTenants() {
        return ResponseEntity.ok(adminService.listTenants());
    }
}
