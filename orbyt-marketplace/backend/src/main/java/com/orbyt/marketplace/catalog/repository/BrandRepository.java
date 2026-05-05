package com.orbyt.marketplace.catalog.repository;

import com.orbyt.marketplace.catalog.domain.Brand;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, UUID> {
}
