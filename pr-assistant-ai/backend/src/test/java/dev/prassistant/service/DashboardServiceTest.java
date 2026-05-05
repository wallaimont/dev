package dev.prassistant.service;

import dev.prassistant.domain.entity.AiAnalysis;
import dev.prassistant.domain.enums.PrState;
import dev.prassistant.domain.enums.RiskLevel;
import dev.prassistant.dto.DashboardResponse;
import dev.prassistant.repository.AiAnalysisRepository;
import dev.prassistant.repository.FinalAnalysisRepository;
import dev.prassistant.repository.PullRequestRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private PullRequestRepository prRepository;

    @Mock
    private FinalAnalysisRepository finalAnalysisRepository;

    @Mock
    private AiAnalysisRepository aiAnalysisRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    @DisplayName("getDashboard - aggregates data from all repositories")
    void getDashboard_aggregatesCorrectly() {
        when(prRepository.count()).thenReturn(100L);
        when(prRepository.countByState(PrState.OPEN)).thenReturn(25L);
        when(finalAnalysisRepository.count()).thenReturn(80L);
        when(finalAnalysisRepository.countByFinalScoreGreaterThanEqual(60)).thenReturn(60L);
        when(finalAnalysisRepository.countByFinalRiskLevel(RiskLevel.CRITICAL)).thenReturn(5L);

        AiAnalysis ai1 = new AiAnalysis();
        ai1.setQualityScore(80);
        AiAnalysis ai2 = new AiAnalysis();
        ai2.setQualityScore(70);
        when(aiAnalysisRepository.findAll()).thenReturn(List.of(ai1, ai2));

        DashboardResponse response = dashboardService.getDashboard();

        assertThat(response.totalPrs()).isEqualTo(100);
        assertThat(response.openPrs()).isEqualTo(25);
        assertThat(response.analyzedPrs()).isEqualTo(80);
        assertThat(response.approvedPrs()).isEqualTo(60);
        assertThat(response.criticalPrs()).isEqualTo(5);
        assertThat(response.avgQualityScore()).isEqualTo(75.0);
    }

    @Test
    @DisplayName("getDashboard - no AI analyses returns 0.0 avg score")
    void getDashboard_noAiAnalyses() {
        when(prRepository.count()).thenReturn(0L);
        when(prRepository.countByState(PrState.OPEN)).thenReturn(0L);
        when(finalAnalysisRepository.count()).thenReturn(0L);
        when(finalAnalysisRepository.countByFinalScoreGreaterThanEqual(60)).thenReturn(0L);
        when(finalAnalysisRepository.countByFinalRiskLevel(RiskLevel.CRITICAL)).thenReturn(0L);
        when(aiAnalysisRepository.findAll()).thenReturn(List.of());

        DashboardResponse response = dashboardService.getDashboard();

        assertThat(response.totalPrs()).isZero();
        assertThat(response.avgQualityScore()).isEqualTo(0.0);
    }
}
