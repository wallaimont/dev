package dev.prassistant.service;

import dev.prassistant.dto.DashboardResponse;
import dev.prassistant.repository.AiAnalysisRepository;
import dev.prassistant.repository.FinalAnalysisRepository;
import dev.prassistant.repository.PullRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PullRequestRepository prRepository;
    private final FinalAnalysisRepository finalAnalysisRepository;
    private final AiAnalysisRepository aiAnalysisRepository;

    public DashboardResponse getDashboard() {
        long total = prRepository.count();
        long open = prRepository.countByState(dev.prassistant.domain.enums.PrState.OPEN);
        long analyzed = finalAnalysisRepository.count();
        long approved = finalAnalysisRepository.countByFinalScoreGreaterThanEqual(60);
        long rejected = analyzed - approved;
        long critical = finalAnalysisRepository.countByFinalRiskLevel(dev.prassistant.domain.enums.RiskLevel.CRITICAL);
        double avgScore = aiAnalysisRepository.findAll().stream()
                .mapToInt(a -> a.getQualityScore())
                .average().orElse(0.0);

        return new DashboardResponse(total, open, analyzed, approved, rejected, critical, Math.round(avgScore * 10) / 10.0);
    }
}
