package com.orionerp.audit;

import com.orionerp.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public void register(String acao,
                         String entidade,
                         Long entidadeId,
                         String dadosAntes,
                         String dadosDepois,
                         String ip,
                         String userAgent) {

        AuditLog log = new AuditLog();
        log.setAcao(acao);
        log.setEntidade(entidade);
        log.setEntidadeId(entidadeId);
        log.setDadosAntes(dadosAntes);
        log.setDadosDepois(dadosDepois);
        log.setIp(ip);
        log.setUserAgent(userAgent);
        log.setUsuarioId(SecurityUtils.currentUserId());
        log.setUsuarioNome(SecurityUtils.currentUsername());
        log.setEmpresaId(SecurityUtils.currentEmpresaId());
        log.setFilialId(SecurityUtils.currentFilialId());

        auditLogRepository.save(log);
    }
}
