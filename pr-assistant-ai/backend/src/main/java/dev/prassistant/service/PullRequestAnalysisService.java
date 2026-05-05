package dev.prassistant.service;

import dev.prassistant.domain.entity.*;
import dev.prassistant.domain.enums.PrState;
import dev.prassistant.domain.enums.RiskLevel;
import dev.prassistant.repository.*;
import dev.prassistant.service.ai.AiReviewProvider;
import dev.prassistant.service.rules.RuleEngineService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PullRequestAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(PullRequestAnalysisService.class);

    private final PullRequestRepository prRepository;
    private final PullRequestFileRepository fileRepository;
    private final AiAnalysisRepository aiAnalysisRepository;
    private final FinalAnalysisRepository finalAnalysisRepository;
    private final RepositoryRepository repoRepository;
    private final GitHubClient gitHubClient;
    private final RuleEngineService ruleEngine;
    private final AiReviewProvider aiProvider;
    private final AuditService auditService;

    @Async
    @Transactional
    public void processWebhook(String owner, String repoName, long prNumber,
                                String title, String description, String author,
                                String sourceBranch, String targetBranch) {
        log.info("Processing PR #{} from {}/{}", prNumber, owner, repoName);

        Repository repository = repoRepository.findByOwnerAndName(owner, repoName)
                .orElseGet(() -> {
                    Repository r = new Repository();
                    r.setOwner(owner);
                    r.setName(repoName);
                    r.setFullName(owner + "/" + repoName);
                    r.setActive(true);
                    return repoRepository.save(r);
                });

        PullRequest pr = prRepository.findByExternalPrIdAndRepositoryId(prNumber, repository.getId())
                .orElseGet(() -> {
                    PullRequest p = new PullRequest();
                    p.setRepository(repository);
                    p.setExternalPrId(prNumber);
                    return p;
                });

        pr.setTitle(title);
        pr.setDescription(description);
        pr.setAuthor(author);
        pr.setSourceBranch(sourceBranch);
        pr.setTargetBranch(targetBranch);
        pr.setState(PrState.OPEN);
        pr = prRepository.save(pr);

        // Fetch files from GitHub
        List<PullRequestFile> files = gitHubClient.fetchPrFiles(owner, repoName, prNumber);
        for (PullRequestFile file : files) {
            file.setPullRequest(pr);
        }
        fileRepository.saveAll(files);
        pr.setFiles(files);

        // Rule-based analysis
        RuleAnalysis ruleAnalysis = ruleEngine.analyze(pr);
        log.info("Rule analysis: risk={}, violations={}", ruleAnalysis.getRiskLevel(), ruleAnalysis.getViolations().size());

        // AI analysis
        AiReviewProvider.AiReviewResult aiResult = aiProvider.review(pr);
        AiAnalysis aiAnalysis = new AiAnalysis();
        aiAnalysis.setPullRequest(pr);
        aiAnalysis.setProvider(aiProvider.name());
        aiAnalysis.setExecutiveSummary(aiResult.executiveSummary());
        aiAnalysis.setTechnicalSummary(aiResult.technicalSummary());
        aiAnalysis.setQualityScore(aiResult.qualityScore());
        aiAnalysis.setRiskLevel(RiskLevel.valueOf(aiResult.riskLevel()));
        aiAnalysis.setAnalyzedAt(Instant.now());
        aiAnalysisRepository.save(aiAnalysis);

        // Final consolidated analysis
        RiskLevel finalRisk = ruleAnalysis.getRiskLevel().ordinal() > RiskLevel.valueOf(aiResult.riskLevel()).ordinal()
                ? ruleAnalysis.getRiskLevel() : RiskLevel.valueOf(aiResult.riskLevel());
        boolean approved = finalRisk.ordinal() < RiskLevel.HIGH.ordinal() && aiResult.qualityScore() >= 60;

        FinalAnalysis finalAnalysis = new FinalAnalysis();
        finalAnalysis.setPullRequest(pr);
        finalAnalysis.setFinalRiskLevel(finalRisk);
        finalAnalysis.setFinalScore(aiResult.qualityScore());
        finalAnalysis.setDecisionReason(
                "Rule risk: " + ruleAnalysis.getRiskLevel() +
                " | AI risk: " + aiResult.riskLevel() +
                " | Score: " + aiResult.qualityScore() +
                " | " + (approved ? "APPROVED" : "NEEDS REVIEW")
        );
        finalAnalysis.setApproved(approved);
        finalAnalysis.setAnalyzedAt(Instant.now());
        finalAnalysisRepository.save(finalAnalysis);

        // Post comment on GitHub
        String comment = buildComment(ruleAnalysis, aiResult, finalAnalysis);
        gitHubClient.postComment(owner, repoName, prNumber, comment);

        auditService.log("PR_ANALYZED", "PullRequest", pr.getId().toString(), "system",
                "PR #" + prNumber + " analyzed. Risk: " + finalRisk + ", Approved: " + approved);

        log.info("PR #{} analysis complete. Risk={}, Approved={}", prNumber, finalRisk, approved);
    }

    private String buildComment(RuleAnalysis rule, AiReviewProvider.AiReviewResult ai, FinalAnalysis finalA) {
        return """
                ## 🤖 PR Assistant AI - Analysis Report
                
                ### Final Verdict: %s %s
                
                | Metric | Value |
                |--------|-------|
                | Risk Level | **%s** |
                | Quality Score | **%d/100** |
                | Critical Files | **%d** |
                | Violations | **%d** |
                
                ### Executive Summary
                %s
                
                ### Technical Summary
                %s
                
                %s
                
                ---
                *Analyzed by PR Assistant AI (%s provider)*
                """.formatted(
                finalA.isApproved() ? "✅" : "⚠️",
                finalA.isApproved() ? "APPROVED" : "NEEDS REVIEW",
                finalA.getFinalRiskLevel(),
                ai.qualityScore(),
                rule.getCriticalFilesCount(),
                rule.getViolations().size(),
                ai.executiveSummary(),
                ai.technicalSummary(),
                rule.getViolations().isEmpty() ? "" : "### Violations\n" +
                        rule.getViolations().stream().map(v -> "- " + v).reduce("", (a, b) -> a + "\n" + b),
                ai.riskLevel()
        );
    }
}
