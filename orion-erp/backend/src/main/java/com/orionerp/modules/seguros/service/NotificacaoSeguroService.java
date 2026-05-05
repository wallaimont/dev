package com.orionerp.modules.seguros.service;

import com.orionerp.common.PageResponse;
import com.orionerp.modules.seguros.domain.NotificacaoSeguro;
import com.orionerp.modules.seguros.dto.NotificacaoSeguroResponse;
import com.orionerp.modules.seguros.repository.NotificacaoSeguroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.orionerp.modules.seguros.service.SegurosSpecifications.*;

@Service
@RequiredArgsConstructor
public class NotificacaoSeguroService {

    private final NotificacaoSeguroRepository repository;

    @Transactional(readOnly = true)
    public PageResponse<NotificacaoSeguroResponse> list(Long empresaId, Boolean lida, int page, int size) {
        Specification<NotificacaoSeguro> spec = Specification.where(notificacaoEmpresa(empresaId))
                .and(notificacaoLida(lida));
        Page<NotificacaoSeguroResponse> result = repository.findAll(spec, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")))
                .map(this::toResponse);
        return PageResponse.from(result);
    }

    @Transactional
    public void marcarComoLida(Long id) {
        repository.findById(id).ifPresent(n -> {
            n.setLida(true);
            n.setDataLeitura(LocalDateTime.now());
            n.setUpdatedAt(LocalDateTime.now());
            repository.save(n);
        });
    }

    private NotificacaoSeguroResponse toResponse(NotificacaoSeguro e) {
        return new NotificacaoSeguroResponse(
                e.getId(), e.getEmpresaId(), e.getTipo(), e.getTitulo(), e.getDescricao(),
                e.getDestinatario(), e.getTabelaOrigem(), e.getRegistroId(),
                e.getLida(), e.getDataLeitura(), e.getCreatedAt()
        );
    }
}
