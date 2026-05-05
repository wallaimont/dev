package dev.prassistant.service.rules;

import dev.prassistant.domain.entity.PullRequest;
import dev.prassistant.domain.entity.PullRequestFile;
import dev.prassistant.domain.entity.RuleAnalysis;
import dev.prassistant.domain.enums.RiskLevel;
import dev.prassistant.repository.RuleAnalysisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RuleEngineService {

    private final CriticalFileDetector criticalFileDetector;
    private final RuleAnalysisRepository ruleAnalysisRepository;

    public RuleAnalysis analyze(PullRequest pr) {
        List<String> criticalFiles = new ArrayList<>();
        List<String> violations = new ArrayList<>();
        int totalAdditions = 0;
        int totalDeletions = 0;

        for (PullRequestFile file : pr.getFiles()) {
            var result = criticalFileDetector.check(file.getFilePath());
            if (result.critical()) {
                criticalFiles.add(file.getFilePath());
                file.setCritical(true);
                file.setCriticalReason(result.reason());
            }
            totalAdditions += file.getAdditions();
            totalDeletions += file.getDeletions();
        }

        if (totalAdditions + totalDeletions > 1000) {
            violations.add("PR too large: " + (totalAdditions + totalDeletions) + " lines changed (limit: 1000)");
        }
        if (pr.getFiles().size() > 30) {
            violations.add("Too many files changed: " + pr.getFiles().size() + " (limit: 30)");
        }
        if (pr.getTitle() == null || pr.getTitle().length() < 10) {
            violations.add("PR title too short (min 10 chars)");
        }
        if (pr.getDescription() == null || pr.getDescription().isBlank()) {
            violations.add("PR description is empty");
        }

        RiskLevel risk = determineRisk(criticalFiles.size(), violations.size(), totalAdditions + totalDeletions);

        RuleAnalysis analysis = new RuleAnalysis();
        analysis.setPullRequest(pr);
        analysis.setRiskLevel(risk);
        analysis.setCriticalFilesCount(criticalFiles.size());
        analysis.setCriticalFiles(criticalFiles);
        analysis.setViolations(violations);
        analysis.setAnalyzedAt(Instant.now());

        return ruleAnalysisRepository.save(analysis);
    }

    private RiskLevel determineRisk(int criticalCount, int violationCount, int linesChanged) {
        if (criticalCount >= 3 || violationCount >= 3) return RiskLevel.CRITICAL;
        if (criticalCount >= 2 || violationCount >= 2 || linesChanged > 500) return RiskLevel.HIGH;
        if (criticalCount >= 1 || violationCount >= 1 || linesChanged > 200) return RiskLevel.MEDIUM;
        return RiskLevel.LOW;
    }
}
