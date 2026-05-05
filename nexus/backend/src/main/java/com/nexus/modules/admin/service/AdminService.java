package com.nexus.modules.admin.service;

import com.nexus.modules.admin.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    public DashboardKpiResponse getDashboardKpis(String period) {
        return DashboardKpiResponse.builder()
                .totalGmv(BigDecimal.ZERO).totalOrders(0)
                .totalSellers(0).totalBuyers(0).build();
    }

    public GmvResponse getGmv(String from, String to, String granularity) {
        return GmvResponse.builder().totalGmv(BigDecimal.ZERO).dataPoints(List.of()).build();
    }

    public List<?> listFraudAnalysis() {
        return List.of();
    }

    public List<?> getAuditLogs(String resource, String action) {
        return List.of();
    }

    public List<TenantSummary> listTenants() {
        return List.of();
    }
}
