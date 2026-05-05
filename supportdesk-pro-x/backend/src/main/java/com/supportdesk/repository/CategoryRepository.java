package com.supportdesk.repository;

import com.supportdesk.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByActiveTrue();
    boolean existsByNameIgnoreCase(String name);
}
