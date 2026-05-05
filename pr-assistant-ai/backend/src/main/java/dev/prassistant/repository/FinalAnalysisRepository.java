package dev.prassistant.repository;

import dev.prassistant.domain.entity.FinalAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FinalAnalysisRepository extends JpaRepository<FinalAnalysis, UUID> {
    List<FinalAnalysis> findByPullRequestIdOrderByCreatedAtDesc(UUID pullRequestId);
    long countByFinalScoreGreaterThanEqual(int score);
    long countByFinalRiskLevel(dev.prassistant.domain.enums.RiskLevel finalRiskLevel);
}
