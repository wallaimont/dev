package com.sigaseguros.service;

import com.sigaseguros.entity.AuditoriaLog;
import com.sigaseguros.repository.AuditoriaLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private final AuditoriaLogRepository auditoriaLogRepository;

    public void registrar(String entidade, Long entidadeId, String acao, String dadosAnteriores, String dadosNovos) {
        String usuario = getUsuarioLogado();
        AuditoriaLog log = AuditoriaLog.builder()
                .entidade(entidade)
                .entidadeId(entidadeId)
                .acao(acao)
                .usuario(usuario)
                .dataHora(LocalDateTime.now())
                .dadosAnteriores(dadosAnteriores)
                .dadosNovos(dadosNovos)
                .build();
        auditoriaLogRepository.save(log);
    }

    public void registrar(String entidade, Long entidadeId, String acao) {
        registrar(entidade, entidadeId, acao, null, null);
    }

    private String getUsuarioLogado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }
        return "system";
    }
}
