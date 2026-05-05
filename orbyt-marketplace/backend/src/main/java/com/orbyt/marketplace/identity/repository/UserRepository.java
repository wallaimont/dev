package com.orbyt.marketplace.identity.repository;

import com.orbyt.marketplace.identity.domain.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByTenantIdAndEmailIgnoreCase(UUID tenantId, String email);

    List<User> findAllByTenantIdAndDeletedAtIsNull(UUID tenantId);

    Optional<User> findByIdAndTenantIdAndDeletedAtIsNull(UUID id, UUID tenantId);
}
