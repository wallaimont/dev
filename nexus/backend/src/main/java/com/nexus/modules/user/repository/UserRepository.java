package com.nexus.modules.user.repository;

import com.nexus.modules.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByTenantIdAndEmail(UUID tenantId, String email);
    boolean existsByTenantIdAndEmail(UUID tenantId, String email);

       @Query(value = "SELECT r.name FROM roles r " +
                               "JOIN user_roles ur ON r.id = ur.role_id " +
                               "WHERE ur.user_id = :userId",
                 nativeQuery = true)
    List<String> findRolesByUserId(UUID userId);

    @Modifying
    @Query(value = "INSERT INTO user_roles (user_id, role_id) " +
                   "SELECT :userId, id FROM roles WHERE name = :roleName AND is_system = true",
           nativeQuery = true)
    void assignRole(UUID userId, String roleName);
}
