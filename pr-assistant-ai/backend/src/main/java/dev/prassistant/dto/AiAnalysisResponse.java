package dev.prassistant.dto;

import dev.prassistant.domain.enums.RiskLevel;
import java.time.Instant;

public record AiAnalysisResponse(
        String id,
        String provider,
        String executiveSummary,
        String technicalSummary,
        RiskLevel riskLevel,
        int qualityScore,
        Instant analyzedAt
) {}
