package dev.prassistant.repository;

import dev.prassistant.domain.entity.RuleAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RuleAnalysisRepository extends JpaRepository<RuleAnalysis, UUID> {
    List<RuleAnalysis> findByPullRequestIdOrderByCreatedAtDesc(UUID pullRequestId);
}
