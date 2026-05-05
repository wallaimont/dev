package dev.prassistant.dto;

public record DashboardResponse(
        long totalPrs,
        long openPrs,
        long analyzedPrs,
        long approvedPrs,
        long rejectedPrs,
        long criticalPrs,
        double avgQualityScore
) {}
