package com.orionerp.modules.cadastros.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.cadastros.domain.UnidadeMedida;
import com.orionerp.modules.cadastros.dto.UnidadeMedidaRequest;
import com.orionerp.modules.cadastros.dto.UnidadeMedidaResponse;
import com.orionerp.modules.cadastros.repository.UnidadeMedidaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UnidadeMedidaService {

    private final UnidadeMedidaRepository unidadeMedidaRepository;

    @Transactional(readOnly = true)
    public Page<UnidadeMedidaResponse> list(Pageable pageable) {
        return unidadeMedidaRepository.findAll(pageable).map(UnidadeMedidaResponse::from);
    }

    @Transactional(readOnly = true)
    public UnidadeMedidaResponse getById(Long id) {
        UnidadeMedida entity = unidadeMedidaRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidade de medida não encontrada: " + id));
        return UnidadeMedidaResponse.from(entity);
    }

    @Transactional
    public UnidadeMedidaResponse create(UnidadeMedidaRequest request) {
        if (unidadeMedidaRepository.existsByCodigo(request.codigo())) {
            throw new BusinessException("Já existe uma unidade de medida com o código: " + request.codigo());
        }

        UnidadeMedida entity = new UnidadeMedida();
        entity.setCodigo(request.codigo());
        entity.setNome(request.nome());

        entity = unidadeMedidaRepository.save(entity);
        return UnidadeMedidaResponse.from(entity);
    }

    @Transactional
    public UnidadeMedidaResponse update(Long id, UnidadeMedidaRequest request) {
        UnidadeMedida entity = unidadeMedidaRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidade de medida não encontrada: " + id));

        entity.setNome(request.nome());
        entity.setUpdatedAt(LocalDateTime.now());

        entity = unidadeMedidaRepository.save(entity);
        return UnidadeMedidaResponse.from(entity);
    }

    @Transactional
    public void delete(Long id) {
        UnidadeMedida entity = unidadeMedidaRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidade de medida não encontrada: " + id));
        entity.setAtivo(false);
        entity.setUpdatedAt(LocalDateTime.now());
        unidadeMedidaRepository.save(entity);
    }
}
