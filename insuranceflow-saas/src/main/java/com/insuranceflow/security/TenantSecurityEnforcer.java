package com.insuranceflow.security;

import com.insuranceflow.common.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TenantSecurityEnforcer {

    public UUID requireTenant() {
        UUID empresaId = TenantContext.getCurrentTenant();
        if (empresaId == null) {
            throw new BusinessException("Contexto de tenant não encontrado. Acesso negado.", HttpStatus.FORBIDDEN);
        }
        return empresaId;
    }

    public void validateTenantAccess(UUID resourceEmpresaId) {
        UUID currentTenant = requireTenant();
        if (!currentTenant.equals(resourceEmpresaId)) {
            throw new BusinessException("Acesso negado: recurso pertence a outra empresa.", HttpStatus.FORBIDDEN);
        }
    }

    public void validateOwnerOrMaster(UUID resourceEmpresaId, UserPrincipal principal) {
        if ("ADMIN_MASTER".equals(principal.getRole())) {
            return;
        }
        if (resourceEmpresaId == null || !resourceEmpresaId.equals(principal.getEmpresaId())) {
            throw new BusinessException("Acesso negado: recurso pertence a outra empresa.", HttpStatus.FORBIDDEN);
        }
    }
}
