package dev.prassistant.repository;

import dev.prassistant.domain.entity.PullRequestFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PullRequestFileRepository extends JpaRepository<PullRequestFile, UUID> {
    List<PullRequestFile> findByPullRequestId(UUID pullRequestId);
}
