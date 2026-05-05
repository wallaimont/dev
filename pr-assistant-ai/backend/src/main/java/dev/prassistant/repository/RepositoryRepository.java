package dev.prassistant.repository;

import dev.prassistant.domain.entity.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RepositoryRepository extends JpaRepository<Repository, UUID> {
    Optional<Repository> findByOwnerAndName(String owner, String name);
}
