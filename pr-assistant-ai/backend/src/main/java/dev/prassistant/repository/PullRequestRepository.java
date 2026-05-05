package dev.prassistant.repository;

import dev.prassistant.domain.entity.PullRequest;
import dev.prassistant.domain.enums.RiskLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface PullRequestRepository extends JpaRepository<PullRequest, UUID> {

    Optional<PullRequest> findByExternalPrIdAndRepositoryId(Long externalPrId, UUID repositoryId);

    Page<PullRequest> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT COUNT(p) FROM PullRequest p")
    long countAll();

    long countByState(dev.prassistant.domain.enums.PrState state);

    @Query("SELECT COUNT(fa) FROM FinalAnalysis fa WHERE fa.finalRiskLevel = :risk")
    long countByFinalRisk(RiskLevel risk);

    @Query("SELECT r.name, COUNT(p) FROM PullRequest p JOIN p.repository r GROUP BY r.name ORDER BY COUNT(p) DESC")
    java.util.List<Object[]> countByRepository();
}
