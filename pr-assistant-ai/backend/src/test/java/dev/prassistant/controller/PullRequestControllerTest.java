package dev.prassistant.controller;

import dev.prassistant.config.SecurityConfig;
import dev.prassistant.domain.entity.FinalAnalysis;
import dev.prassistant.domain.entity.PullRequest;
import dev.prassistant.domain.entity.PullRequestFile;
import dev.prassistant.domain.entity.Repository;
import dev.prassistant.domain.enums.FileStatus;
import dev.prassistant.domain.enums.PrState;
import dev.prassistant.domain.enums.RiskLevel;
import dev.prassistant.repository.FinalAnalysisRepository;
import dev.prassistant.repository.PullRequestRepository;
import dev.prassistant.security.CustomUserDetailsService;
import dev.prassistant.security.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PullRequestController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class PullRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PullRequestRepository pullRequestRepository;

    @MockBean
    private FinalAnalysisRepository finalAnalysisRepository;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private PullRequest buildSamplePr() {
        Repository repo = Repository.builder()
                .id(UUID.randomUUID())
                .name("test-repo")
                .owner("test-org")
                .fullName("test-org/test-repo")
                .provider("github")
                .active(true)
                .build();

        PullRequest pr = PullRequest.builder()
                .id(UUID.randomUUID())
                .externalPrId(42L)
                .repository(repo)
                .title("Fix critical bug")
                .description("Fixes NPE in service layer")
                .author("dev-user")
                .sourceBranch("fix/npe")
                .targetBranch("main")
                .state(PrState.OPEN)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        PullRequestFile file = PullRequestFile.builder()
                .id(UUID.randomUUID())
                .pullRequest(pr)
                .filePath("src/main/java/Service.java")
                .fileStatus(FileStatus.MODIFIED)
                .additions(10)
                .deletions(3)
                .critical(false)
                .build();

        pr.setFiles(List.of(file));
        pr.setRuleAnalyses(List.of());
        pr.setAiAnalyses(List.of());
        pr.setFinalAnalyses(List.of());
        return pr;
    }

    @Test
    @DisplayName("GET /api/pull-requests - list PRs with pagination")
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void listPrs_authenticated() throws Exception {
        PullRequest pr = buildSamplePr();
        var page = new PageImpl<>(List.of(pr));

        when(pullRequestRepository.findAllByOrderByCreatedAtDesc(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/pull-requests").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Fix critical bug"))
                .andExpect(jsonPath("$.content[0].author").value("dev-user"))
                .andExpect(jsonPath("$.content[0].state").value("OPEN"))
                .andExpect(jsonPath("$.content[0].files[0].filePath").value("src/main/java/Service.java"));
    }

    @Test
    @DisplayName("GET /api/pull-requests/{id} - get single PR")
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void getPrById_found() throws Exception {
        PullRequest pr = buildSamplePr();
        UUID prId = pr.getId();

        when(pullRequestRepository.findById(eq(prId))).thenReturn(Optional.of(pr));

        mockMvc.perform(get("/api/pull-requests/{id}", prId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Fix critical bug"))
                .andExpect(jsonPath("$.repositoryName").value("test-repo"));
    }

    @Test
    @DisplayName("GET /api/pull-requests/{id} - not found returns 404")
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void getPrById_notFound() throws Exception {
        UUID prId = UUID.randomUUID();
        when(pullRequestRepository.findById(eq(prId))).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/pull-requests/{id}", prId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/pull-requests/{id}/analyses - get final analyses")
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void getAnalyses_found() throws Exception {
        PullRequest pr = buildSamplePr();
        UUID prId = pr.getId();

        FinalAnalysis analysis = FinalAnalysis.builder()
                .id(UUID.randomUUID())
                .pullRequest(pr)
                .finalRiskLevel(RiskLevel.LOW)
                .finalScore(85)
                .decisionReason("Low risk, high quality")
                .approved(true)
                .analyzedAt(Instant.now())
                .publishedSuccess(true)
                .build();

        when(pullRequestRepository.findById(eq(prId))).thenReturn(Optional.of(pr));
        when(finalAnalysisRepository.findByPullRequestIdOrderByCreatedAtDesc(eq(prId)))
                .thenReturn(List.of(analysis));

        mockMvc.perform(get("/api/pull-requests/{id}/analyses", prId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].riskLevel").value("LOW"))
                .andExpect(jsonPath("$[0].finalScore").value(85));
    }

    @Test
    @DisplayName("GET /api/pull-requests - unauthenticated returns 401")
    void listPrs_unauthenticated() throws Exception {
        mockMvc.perform(get("/api/pull-requests"))
                .andExpect(status().isUnauthorized());
    }
}
