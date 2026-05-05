package com.nexus.shared.tenant;

import java.util.UUID;

/**
 * Thread-local holder for the current tenant context.
 * Set by TenantFilter on every request.
 * Used by Hibernate tenant interceptor and all service layers.
 */
public final class TenantContext {

    private static final ThreadLocal<UUID> CURRENT_TENANT = new InheritableThreadLocal<>();
    private static final ThreadLocal<String> CURRENT_TENANT_SLUG = new InheritableThreadLocal<>();

    private TenantContext() {}

    public static void setTenantId(UUID tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    public static UUID getTenantId() {
        UUID id = CURRENT_TENANT.get();
        if (id == null) throw new TenantNotFoundException("No tenant in current context");
        return id;
    }

    public static UUID getTenantIdOrNull() {
        return CURRENT_TENANT.get();
    }

    public static void setTenantSlug(String slug) {
        CURRENT_TENANT_SLUG.set(slug);
    }

    public static String getTenantSlug() {
        return CURRENT_TENANT_SLUG.get();
    }

    public static void clear() {
        CURRENT_TENANT.remove();
        CURRENT_TENANT_SLUG.remove();
    }

    public static class TenantNotFoundException extends RuntimeException {
        public TenantNotFoundException(String msg) { super(msg); }
    }
}
