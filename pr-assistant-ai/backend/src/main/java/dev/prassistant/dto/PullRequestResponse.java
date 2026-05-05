package dev.prassistant.dto;

import dev.prassistant.domain.enums.RiskLevel;
import java.time.Instant;
import java.util.List;

public record PullRequestResponse(
        String id,
        Long externalPrId,
        String repositoryName,
        String repositoryOwner,
        String title,
        String description,
        String author,
        String sourceBranch,
        String targetBranch,
        String state,
        List<FileResponse> files,
        RuleAnalysisResponse latestRuleAnalysis,
        AiAnalysisResponse latestAiAnalysis,
        FinalAnalysisResponse latestFinalAnalysis,
        Instant createdAt,
        Instant updatedAt
) {}
