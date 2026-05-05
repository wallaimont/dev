package dev.prassistant.service.ai;

import dev.prassistant.domain.entity.PullRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "ai.provider", havingValue = "mock", matchIfMissing = true)
public class MockAiReviewProvider implements AiReviewProvider {

    @Override
    public String name() {
        return "mock";
    }

    @Override
    public AiReviewResult review(PullRequest pr) {
        int filesCount = pr.getFiles().size();
        int score = Math.max(40, 100 - filesCount * 3);
        String risk = filesCount > 15 ? "HIGH" : filesCount > 5 ? "MEDIUM" : "LOW";

        return new AiReviewResult(
                "Mock AI review: PR '" + pr.getTitle() + "' with " + filesCount + " files analyzed.",
                "Technical summary: " + filesCount + " files changed across " +
                        pr.getSourceBranch() + " → " + pr.getTargetBranch() + ". " +
                        "Code quality appears acceptable with minor suggestions for improvement.",
                score,
                risk
        );
    }
}
