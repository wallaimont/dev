package com.orionerp.modules.administration.service;

import com.orionerp.common.PageResponse;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.administration.domain.ParametroSistema;
import com.orionerp.modules.administration.dto.ParametroSistemaRequest;
import com.orionerp.modules.administration.dto.ParametroSistemaResponse;
import com.orionerp.modules.administration.repository.ParametroSistemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParametroSistemaService {

    private final ParametroSistemaRepository parametroSistemaRepository;

    @Transactional(readOnly = true)
    public PageResponse<ParametroSistemaResponse> list(Long empresaId,
                                                       Long filialId,
                                                       String modulo,
                                                       String term,
                                                       int page,
                                                       int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "modulo", "chave"));
        var result = parametroSistemaRepository
                .findAll(AdministrationSpecifications.parametroFilter(empresaId, filialId, modulo, term), pageable)
                .map(this::toResponse);
        return PageResponse.from(result);
    }

    @Transactional(readOnly = true)
    public ParametroSistemaResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public ParametroSistemaResponse create(ParametroSistemaRequest request) {
        ParametroSistema entity = new ParametroSistema();
        apply(entity, request);
        return toResponse(parametroSistemaRepository.save(entity));
    }

    @Transactional
    public ParametroSistemaResponse update(Long id, ParametroSistemaRequest request) {
        ParametroSistema entity = findById(id);
        apply(entity, request);
        return toResponse(parametroSistemaRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        ParametroSistema entity = findById(id);
        entity.softDelete();
        parametroSistemaRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public ParametroSistemaResponse resolve(String chave, Long empresaId, Long filialId) {
        return parametroSistemaRepository.findFirstByEmpresaIdAndFilialIdAndChave(empresaId, filialId, chave)
                .or(() -> parametroSistemaRepository.findFirstByEmpresaIdAndFilialIdIsNullAndChave(empresaId, chave))
                .or(() -> parametroSistemaRepository.findFirstByEmpresaIdIsNullAndFilialIdIsNullAndChave(chave))
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Parametro nao encontrado"));
    }

    private ParametroSistema findById(Long id) {
        return parametroSistemaRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parametro nao encontrado"));
    }

    private void apply(ParametroSistema entity, ParametroSistemaRequest request) {
        entity.setEmpresaId(request.empresaId());
        entity.setFilialId(request.filialId());
        entity.setChave(request.chave().trim().toUpperCase());
        entity.setValor(request.valor());
        entity.setTipo(request.tipo().trim().toUpperCase());
        entity.setDescricao(request.descricao());
        entity.setModulo(request.modulo() != null ? request.modulo().trim().toUpperCase() : null);
        if (request.editavel() != null) {
            entity.setEditavel(request.editavel());
        }
        if (request.ativo() != null) {
            entity.setAtivo(request.ativo());
        }
    }

    private ParametroSistemaResponse toResponse(ParametroSistema entity) {
        return new ParametroSistemaResponse(
                entity.getId(),
                entity.getUuid(),
                entity.getEmpresaId(),
                entity.getFilialId(),
                entity.getChave(),
                entity.getValor(),
                entity.getTipo(),
                entity.getDescricao(),
                entity.getModulo(),
                entity.getEditavel(),
                entity.getAtivo()
        );
    }
}
