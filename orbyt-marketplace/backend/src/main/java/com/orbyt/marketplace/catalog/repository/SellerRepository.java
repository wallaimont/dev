package com.orbyt.marketplace.catalog.repository;

import com.orbyt.marketplace.catalog.domain.Seller;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, UUID> {
}
