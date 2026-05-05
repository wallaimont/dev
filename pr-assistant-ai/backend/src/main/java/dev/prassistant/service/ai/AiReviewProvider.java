package dev.prassistant.service.ai;

import dev.prassistant.domain.entity.PullRequest;

public interface AiReviewProvider {
    String name();
    AiReviewResult review(PullRequest pr);

    record AiReviewResult(
            String executiveSummary,
            String technicalSummary,
            int qualityScore,
            String riskLevel
    ) {}
}
