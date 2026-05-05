package dev.prassistant.dto;

import dev.prassistant.domain.enums.RiskLevel;
import java.time.Instant;
import java.util.List;

public record RuleAnalysisResponse(
        String id,
        RiskLevel riskLevel,
        int criticalFilesCount,
        List<String> criticalFiles,
        List<String> violations,
        Instant analyzedAt
) {}
