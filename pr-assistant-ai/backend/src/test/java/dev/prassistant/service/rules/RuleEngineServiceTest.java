package dev.prassistant.service.rules;

import dev.prassistant.domain.entity.PullRequest;
import dev.prassistant.domain.entity.PullRequestFile;
import dev.prassistant.domain.entity.RuleAnalysis;
import dev.prassistant.domain.enums.FileStatus;
import dev.prassistant.domain.enums.RiskLevel;
import dev.prassistant.repository.RuleAnalysisRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RuleEngineServiceTest {

    @Spy
    private CriticalFileDetector criticalFileDetector;

    @Mock
    private RuleAnalysisRepository ruleAnalysisRepository;

    @InjectMocks
    private RuleEngineService ruleEngineService;

    @Test
    @DisplayName("analyze - clean small PR gets LOW risk")
    void analyze_cleanPr_lowRisk() {
        PullRequest pr = buildPr("Fix small bug", "Fixes NPE", List.of(
                buildFile("Service.java", 10, 5)
        ));
        when(ruleAnalysisRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RuleAnalysis result = ruleEngineService.analyze(pr);

        assertThat(result.getRiskLevel()).isEqualTo(RiskLevel.LOW);
        assertThat(result.getCriticalFilesCount()).isZero();
        assertThat(result.getViolations()).isEmpty();
    }

    @Test
    @DisplayName("analyze - PR with critical Dockerfile gets MEDIUM risk")
    void analyze_criticalFile_mediumRisk() {
        PullRequest pr = buildPr("Update Docker config", "Change base image", List.of(
                buildFile("Dockerfile", 5, 3),
                buildFile("src/App.java", 10, 2)
        ));
        when(ruleAnalysisRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RuleAnalysis result = ruleEngineService.analyze(pr);

        assertThat(result.getRiskLevel()).isEqualTo(RiskLevel.MEDIUM);
        assertThat(result.getCriticalFilesCount()).isEqualTo(1);
        assertThat(result.getCriticalFiles()).contains("Dockerfile");
    }

    @Test
    @DisplayName("analyze - PR too large triggers violation and HIGH risk")
    void analyze_largePr_highRisk() {
        PullRequest pr = buildPr("Big refactor", "Major changes", List.of(
                buildFile("App.java", 800, 400),
                buildFile("Dockerfile", 5, 3)
        ));
        when(ruleAnalysisRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RuleAnalysis result = ruleEngineService.analyze(pr);

        assertThat(result.getRiskLevel()).isEqualTo(RiskLevel.HIGH);
        assertThat(result.getViolations()).anyMatch(v -> v.contains("too large"));
    }

    @Test
    @DisplayName("analyze - short title adds violation")
    void analyze_shortTitle() {
        PullRequest pr = buildPr("Fix", "Good description", List.of(
                buildFile("App.java", 5, 2)
        ));
        when(ruleAnalysisRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RuleAnalysis result = ruleEngineService.analyze(pr);

        assertThat(result.getViolations()).anyMatch(v -> v.contains("title too short"));
    }

    @Test
    @DisplayName("analyze - empty description adds violation")
    void analyze_emptyDescription() {
        PullRequest pr = buildPr("Update service layer", "", List.of(
                buildFile("App.java", 5, 2)
        ));
        when(ruleAnalysisRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RuleAnalysis result = ruleEngineService.analyze(pr);

        assertThat(result.getViolations()).anyMatch(v -> v.contains("description is empty"));
    }

    @Test
    @DisplayName("analyze - 3+ critical files triggers CRITICAL risk")
    void analyze_manyCriticalFiles_criticalRisk() {
        PullRequest pr = buildPr("Infrastructure overhaul", "Full revamp", List.of(
                buildFile("Dockerfile", 10, 5),
                buildFile("docker-compose.yml", 20, 10),
                buildFile(".env", 3, 1),
                buildFile("pom.xml", 5, 2)
        ));
        when(ruleAnalysisRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RuleAnalysis result = ruleEngineService.analyze(pr);

        assertThat(result.getRiskLevel()).isEqualTo(RiskLevel.CRITICAL);
        assertThat(result.getCriticalFilesCount()).isGreaterThanOrEqualTo(3);
    }

    private PullRequest buildPr(String title, String description, List<PullRequestFile> files) {
        PullRequest pr = PullRequest.builder()
                .title(title)
                .description(description)
                .build();
        pr.setFiles(new ArrayList<>(files));
        return pr;
    }

    private PullRequestFile buildFile(String path, int additions, int deletions) {
        return PullRequestFile.builder()
                .filePath(path)
                .fileStatus(FileStatus.MODIFIED)
                .additions(additions)
                .deletions(deletions)
                .build();
    }
}
