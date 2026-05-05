package com.orbyt.marketplace.identity.application;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RbacProvisioningService {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void provisionTenantDefaults(UUID tenantId, UUID adminUserId) {
        UUID adminRoleId = ensureRole(tenantId, "ADMIN", "Administrador");
        UUID buyerRoleId = ensureRole(tenantId, "BUYER", "Comprador");

        List<UUID> adminPermissionIds = new ArrayList<>();
        adminPermissionIds.add(ensurePermission("admin.dashboard.view", "Visualizar dashboard administrativo"));
        adminPermissionIds.add(ensurePermission("catalog.manage", "Gerenciar catalogo"));
        adminPermissionIds.add(ensurePermission("users.manage", "Gerenciar usuarios"));
        adminPermissionIds.add(ensurePermission("platform.tenants.manage", "Gerenciar tenants da plataforma"));

        for (UUID permissionId : adminPermissionIds) {
            ensureRolePermission(adminRoleId, permissionId);
        }

        ensureUserRole(tenantId, adminUserId, adminRoleId);
        ensureUserRole(tenantId, adminUserId, buyerRoleId);
    }

    @Transactional
    public void assignBuyerRole(UUID tenantId, UUID userId) {
        UUID buyerRoleId = ensureRole(tenantId, "BUYER", "Comprador");
        ensureUserRole(tenantId, userId, buyerRoleId);
    }

    private UUID ensureRole(UUID tenantId, String code, String name) {
        List<UUID> ids = jdbcTemplate.query(
                "select id from roles where tenant_id = ? and code = ? and deleted_at is null",
                (rs, rowNum) -> (UUID) rs.getObject("id"),
                tenantId,
                code
        );
        if (!ids.isEmpty()) {
            return ids.get(0);
        }
        UUID id = UUID.randomUUID();
        jdbcTemplate.update(
                "insert into roles (id, tenant_id, code, name, status, created_at, updated_at) values (?, ?, ?, ?, 'ACTIVE', now(), now())",
                id, tenantId, code, name
        );
        return id;
    }

    private UUID ensurePermission(String code, String name) {
        List<UUID> ids = jdbcTemplate.query(
                "select id from permissions where code = ? and deleted_at is null",
                (rs, rowNum) -> (UUID) rs.getObject("id"),
                code
        );
        if (!ids.isEmpty()) {
            return ids.get(0);
        }
        UUID id = UUID.randomUUID();
        jdbcTemplate.update(
                "insert into permissions (id, code, name, status, created_at, updated_at) values (?, ?, ?, 'ACTIVE', now(), now())",
                id, code, name
        );
        return id;
    }

    private void ensureRolePermission(UUID roleId, UUID permissionId) {
        Integer count = jdbcTemplate.queryForObject(
                "select count(1) from role_permissions where role_id = ? and permission_id = ? and deleted_at is null",
                Integer.class,
                roleId,
                permissionId
        );
        if (count != null && count > 0) {
            return;
        }
        jdbcTemplate.update(
                "insert into role_permissions (id, role_id, permission_id, status, created_at, updated_at) values (?, ?, ?, 'ACTIVE', now(), now())",
                UUID.randomUUID(), roleId, permissionId
        );
    }

    private void ensureUserRole(UUID tenantId, UUID userId, UUID roleId) {
        Integer count = jdbcTemplate.queryForObject(
                "select count(1) from user_roles where tenant_id = ? and user_id = ? and role_id = ? and deleted_at is null",
                Integer.class,
                tenantId,
                userId,
                roleId
        );
        if (count != null && count > 0) {
            return;
        }
        jdbcTemplate.update(
                "insert into user_roles (id, tenant_id, user_id, role_id, status, created_at, updated_at) values (?, ?, ?, ?, 'ACTIVE', now(), now())",
                UUID.randomUUID(), tenantId, userId, roleId
        );
    }
}
