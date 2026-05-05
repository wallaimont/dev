package com.orbyt.marketplace.identity.repository;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PermissionQueryRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<String> findPermissionsByUserIdAndTenantId(UUID userId, UUID tenantId) {
        String sql = """
                select distinct p.code
                  from permissions p
                  join role_permissions rp on rp.permission_id = p.id and rp.deleted_at is null
                  join roles r on r.id = rp.role_id and r.deleted_at is null
                  join user_roles ur on ur.role_id = r.id and ur.deleted_at is null
                 where ur.user_id = ?
                   and (ur.tenant_id = ? or ur.tenant_id is null)
                   and (r.tenant_id = ? or r.tenant_id is null)
                   and p.status = 'ACTIVE'
                   and r.status = 'ACTIVE'
                   and ur.status = 'ACTIVE'
                """;
        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getString("code"),
                userId,
                tenantId,
                tenantId
        );
    }
}
