package dev.prassistant.dto;

import dev.prassistant.domain.enums.RiskLevel;
import java.time.Instant;

public record FinalAnalysisResponse(
        String id,
        RiskLevel riskLevel,
        int finalScore,
        String decisionReason,
        Instant analyzedAt
) {}
