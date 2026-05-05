package dev.prassistant.repository;

import dev.prassistant.domain.entity.AiAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AiAnalysisRepository extends JpaRepository<AiAnalysis, UUID> {
    List<AiAnalysis> findByPullRequestIdOrderByCreatedAtDesc(UUID pullRequestId);
}
