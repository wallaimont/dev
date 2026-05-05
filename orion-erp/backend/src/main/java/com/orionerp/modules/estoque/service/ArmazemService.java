package com.orionerp.modules.estoque.service;

import com.orionerp.common.PageResponse;
import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.estoque.domain.Armazem;
import com.orionerp.modules.estoque.dto.ArmazemRequest;
import com.orionerp.modules.estoque.dto.ArmazemResponse;
import com.orionerp.modules.estoque.repository.ArmazemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArmazemService {

    private final ArmazemRepository armazemRepository;

    @Transactional(readOnly = true)
    public PageResponse<ArmazemResponse> list(Long empresaId, Long filialId, String term, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "nome"));
        var result = armazemRepository.findAll(EstoqueSpecifications.armazemFilter(empresaId, filialId, term), pageable)
                .map(this::toResponse);
        return PageResponse.from(result);
    }

    @Transactional(readOnly = true)
    public ArmazemResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public ArmazemResponse create(ArmazemRequest request) {
        if (armazemRepository.existsByEmpresaIdAndFilialIdAndCodigoIgnoreCaseAndDeletedFalse(
                request.empresaId(), request.filialId(), request.codigo())) {
            throw new BusinessException("Ja existe armazem com este codigo");
        }

        Armazem armazem = new Armazem();
        armazem.setEmpresaId(request.empresaId());
        armazem.setFilialId(request.filialId());
        apply(armazem, request);
        return toResponse(armazemRepository.save(armazem));
    }

    @Transactional
    public ArmazemResponse update(Long id, ArmazemRequest request) {
        Armazem armazem = findById(id);
        apply(armazem, request);
        return toResponse(armazemRepository.save(armazem));
    }

    @Transactional
    public void delete(Long id) {
        Armazem armazem = findById(id);
        armazem.softDelete();
        armazemRepository.save(armazem);
    }

    private void apply(Armazem armazem, ArmazemRequest request) {
        armazem.setCodigo(request.codigo().trim());
        armazem.setNome(request.nome().trim());
        if (request.tipo() != null) armazem.setTipo(request.tipo().trim());
        if (request.ativo() != null) armazem.setAtivo(request.ativo());
    }

    Armazem findById(Long id) {
        return armazemRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Armazem nao encontrado"));
    }

    private ArmazemResponse toResponse(Armazem a) {
        return new ArmazemResponse(
                a.getId(), a.getUuid(), a.getEmpresaId(), a.getFilialId(),
                a.getCodigo(), a.getNome(), a.getTipo(), a.getAtivo());
    }
}
