package com.orionerp.modules.rh.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.rh.domain.DepartamentoRh;
import com.orionerp.modules.rh.dto.DepartamentoRhRequest;
import com.orionerp.modules.rh.dto.DepartamentoRhResponse;
import com.orionerp.modules.rh.repository.DepartamentoRhRepository;
import com.orionerp.modules.rh.repository.RhSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DepartamentoRhService {

    private final DepartamentoRhRepository repository;

    @Transactional(readOnly = true)
    public Page<DepartamentoRhResponse> listar(Long empresaId, String term, Pageable pageable) {
        return repository.findAll(RhSpecifications.departamentoRhFilter(empresaId, term), pageable)
                .map(DepartamentoRhResponse::from);
    }

    @Transactional(readOnly = true)
    public DepartamentoRhResponse buscarPorId(Long id) {
        return DepartamentoRhResponse.from(findOrFail(id));
    }

    @Transactional
    public DepartamentoRhResponse criar(DepartamentoRhRequest request) {
        if (repository.existsByEmpresaIdAndCodigoAndDeletedFalse(request.empresaId(), request.codigo())) {
            throw new BusinessException("Já existe um departamento com o código " + request.codigo());
        }
        DepartamentoRh entity = new DepartamentoRh();
        mapFields(entity, request);
        return DepartamentoRhResponse.from(repository.save(entity));
    }

    @Transactional
    public DepartamentoRhResponse atualizar(Long id, DepartamentoRhRequest request) {
        DepartamentoRh entity = findOrFail(id);
        mapFields(entity, request);
        return DepartamentoRhResponse.from(repository.save(entity));
    }

    @Transactional
    public void excluir(Long id) {
        DepartamentoRh entity = findOrFail(id);
        entity.softDelete();
        repository.save(entity);
    }

    private DepartamentoRh findOrFail(Long id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Departamento não encontrado"));
    }

    private void mapFields(DepartamentoRh entity, DepartamentoRhRequest r) {
        entity.setEmpresaId(r.empresaId());
        entity.setCodigo(r.codigo());
        entity.setNome(r.nome());
        entity.setCentroCustoId(r.centroCustoId());
        entity.setGestorId(r.gestorId());
    }
}
