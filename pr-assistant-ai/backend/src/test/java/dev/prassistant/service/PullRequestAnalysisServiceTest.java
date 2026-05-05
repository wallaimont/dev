package dev.prassistant.service;

import dev.prassistant.domain.entity.*;
import dev.prassistant.domain.enums.FileStatus;
import dev.prassistant.domain.enums.PrState;
import dev.prassistant.domain.enums.RiskLevel;
import dev.prassistant.repository.*;
import dev.prassistant.service.ai.AiReviewProvider;
import dev.prassistant.service.rules.RuleEngineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PullRequestAnalysisServiceTest {

    @Mock private PullRequestRepository prRepository;
    @Mock private PullRequestFileRepository fileRepository;
    @Mock private AiAnalysisRepository aiAnalysisRepository;
    @Mock private FinalAnalysisRepository finalAnalysisRepository;
    @Mock private RepositoryRepository repoRepository;
    @Mock private GitHubClient gitHubClient;
    @Mock private RuleEngineService ruleEngine;
    @Mock private AiReviewProvider aiProvider;
    @Mock private AuditService auditService;

    @InjectMocks
    private PullRequestAnalysisService service;

    private Repository repository;
    private PullRequest pullRequest;

    @BeforeEach
    void setUp() {
        repository = Repository.builder()
                .id(UUID.randomUUID())
                .owner("org")
                .name("repo")
                .fullName("org/repo")
                .active(true)
                .build();

        pullRequest = PullRequest.builder()
                .id(UUID.randomUUID())
                .repository(repository)
                .externalPrId(42L)
                .title("Test PR")
                .state(PrState.OPEN)
                .build();
    }

    @Test
    @DisplayName("processWebhook - creates new repository if not found")
    void processWebhook_createsNewRepo() {
        when(repoRepository.findByOwnerAndName("org", "repo")).thenReturn(Optional.empty());
        when(repoRepository.save(any(Repository.class))).thenAnswer(inv -> {
            Repository r = inv.getArgument(0);
            r.setId(UUID.randomUUID());
            return r;
        });
        when(prRepository.findByExternalPrIdAndRepositoryId(anyLong(), any())).thenReturn(Optional.empty());
        when(prRepository.save(any(PullRequest.class))).thenAnswer(inv -> {
            PullRequest p = inv.getArgument(0);
            if (p.getId() == null) p.setId(UUID.randomUUID());
            return p;
        });

        PullRequestFile file = PullRequestFile.builder()
                .filePath("App.java").fileStatus(FileStatus.MODIFIED)
                .additions(10).deletions(2)
                .build();
        when(gitHubClient.fetchPrFiles("org", "repo", 42L)).thenReturn(List.of(file));

        RuleAnalysis ruleResult = new RuleAnalysis();
        ruleResult.setRiskLevel(RiskLevel.LOW);
        ruleResult.setViolations(List.of());
        ruleResult.setCriticalFiles(List.of());
        ruleResult.setCriticalFilesCount(0);
        when(ruleEngine.analyze(any())).thenReturn(ruleResult);

        when(aiProvider.name()).thenReturn("mock");
        when(aiProvider.review(any())).thenReturn(
                new AiReviewProvider.AiReviewResult("Good PR", "No issues", 90, "LOW")
        );

        service.processWebhook("org", "repo", 42L, "Test PR", "desc", "dev", "feature", "main");

        verify(repoRepository).save(argThat(r -> "org".equals(r.getOwner()) && "repo".equals(r.getName())));
    }

    @Test
    @DisplayName("processWebhook - saves AI analysis with correct provider and score")
    void processWebhook_savesAiAnalysis() {
        when(repoRepository.findByOwnerAndName("org", "repo")).thenReturn(Optional.of(repository));
        when(prRepository.findByExternalPrIdAndRepositoryId(42L, repository.getId())).thenReturn(Optional.of(pullRequest));
        when(prRepository.save(any())).thenReturn(pullRequest);
        when(gitHubClient.fetchPrFiles(any(), any(), anyLong())).thenReturn(List.of());

        RuleAnalysis ruleResult = new RuleAnalysis();
        ruleResult.setRiskLevel(RiskLevel.MEDIUM);
        ruleResult.setViolations(List.of("Large diff"));
        ruleResult.setCriticalFiles(List.of());
        ruleResult.setCriticalFilesCount(0);
        when(ruleEngine.analyze(any())).thenReturn(ruleResult);

        when(aiProvider.name()).thenReturn("mock");
        when(aiProvider.review(any())).thenReturn(
                new AiReviewProvider.AiReviewResult("Summary", "Technical", 75, "MEDIUM")
        );

        service.processWebhook("org", "repo", 42L, "Test", "desc", "dev", "feat", "main");

        ArgumentCaptor<AiAnalysis> captor = ArgumentCaptor.forClass(AiAnalysis.class);
        verify(aiAnalysisRepository).save(captor.capture());

        AiAnalysis saved = captor.getValue();
        assertThat(saved.getProvider()).isEqualTo("mock");
        assertThat(saved.getQualityScore()).isEqualTo(75);
        assertThat(saved.getRiskLevel()).isEqualTo(RiskLevel.MEDIUM);
    }

    @Test
    @DisplayName("processWebhook - low risk + high score results in APPROVED")
    void processWebhook_lowRiskApproved() {
        when(repoRepository.findByOwnerAndName("org", "repo")).thenReturn(Optional.of(repository));
        when(prRepository.findByExternalPrIdAndRepositoryId(42L, repository.getId())).thenReturn(Optional.of(pullRequest));
        when(prRepository.save(any())).thenReturn(pullRequest);
        when(gitHubClient.fetchPrFiles(any(), any(), anyLong())).thenReturn(List.of());

        RuleAnalysis ruleResult = new RuleAnalysis();
        ruleResult.setRiskLevel(RiskLevel.LOW);
        ruleResult.setViolations(List.of());
        ruleResult.setCriticalFiles(List.of());
        ruleResult.setCriticalFilesCount(0);
        when(ruleEngine.analyze(any())).thenReturn(ruleResult);
        when(aiProvider.name()).thenReturn("mock");
        when(aiProvider.review(any())).thenReturn(
                new AiReviewProvider.AiReviewResult("Good", "Clean", 85, "LOW")
        );

        service.processWebhook("org", "repo", 42L, "Test", "desc", "dev", "feat", "main");

        ArgumentCaptor<FinalAnalysis> captor = ArgumentCaptor.forClass(FinalAnalysis.class);
        verify(finalAnalysisRepository).save(captor.capture());

        FinalAnalysis saved = captor.getValue();
        assertThat(saved.isApproved()).isTrue();
        assertThat(saved.getFinalRiskLevel()).isEqualTo(RiskLevel.LOW);
        assertThat(saved.getFinalScore()).isEqualTo(85);
    }

    @Test
    @DisplayName("processWebhook - high risk results in NOT approved")
    void processWebhook_highRiskNotApproved() {
        when(repoRepository.findByOwnerAndName("org", "repo")).thenReturn(Optional.of(repository));
        when(prRepository.findByExternalPrIdAndRepositoryId(42L, repository.getId())).thenReturn(Optional.of(pullRequest));
        when(prRepository.save(any())).thenReturn(pullRequest);
        when(gitHubClient.fetchPrFiles(any(), any(), anyLong())).thenReturn(List.of());

        RuleAnalysis ruleResult = new RuleAnalysis();
        ruleResult.setRiskLevel(RiskLevel.HIGH);
        ruleResult.setViolations(List.of("Critical file changed", "Too many files"));
        ruleResult.setCriticalFiles(List.of("Dockerfile"));
        ruleResult.setCriticalFilesCount(1);
        when(ruleEngine.analyze(any())).thenReturn(ruleResult);
        when(aiProvider.name()).thenReturn("mock");
        when(aiProvider.review(any())).thenReturn(
                new AiReviewProvider.AiReviewResult("Risky", "Many issues", 45, "HIGH")
        );

        service.processWebhook("org", "repo", 42L, "Big Change", "desc", "dev", "feat", "main");

        ArgumentCaptor<FinalAnalysis> captor = ArgumentCaptor.forClass(FinalAnalysis.class);
        verify(finalAnalysisRepository).save(captor.capture());

        FinalAnalysis saved = captor.getValue();
        assertThat(saved.isApproved()).isFalse();
        assertThat(saved.getFinalRiskLevel()).isEqualTo(RiskLevel.HIGH);

        verify(gitHubClient).postComment(eq("org"), eq("repo"), eq(42L), contains("NEEDS REVIEW"));
        verify(auditService).log(eq("PR_ANALYZED"), eq("PullRequest"), any(), eq("system"), contains("Approved: false"));
    }
}
