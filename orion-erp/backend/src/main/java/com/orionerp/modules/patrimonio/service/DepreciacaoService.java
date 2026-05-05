package com.orionerp.modules.patrimonio.service;

import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.patrimonio.domain.Depreciacao;
import com.orionerp.modules.patrimonio.dto.DepreciacaoRequest;
import com.orionerp.modules.patrimonio.dto.DepreciacaoResponse;
import com.orionerp.modules.patrimonio.repository.DepreciacaoRepository;
import com.orionerp.modules.patrimonio.repository.PatrimonioSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DepreciacaoService {

    private final DepreciacaoRepository repository;

    @Transactional(readOnly = true)
    public Page<DepreciacaoResponse> listar(Long empresaId, Long bemPatrimonialId, Integer ano, Integer mes, Pageable pageable) {
        return repository.findAll(
                PatrimonioSpecifications.depreciacaoFilter(empresaId, bemPatrimonialId, ano, mes), pageable)
                .map(DepreciacaoResponse::from);
    }

    @Transactional(readOnly = true)
    public DepreciacaoResponse buscarPorId(Long id) {
        return DepreciacaoResponse.from(findOrFail(id));
    }

    @Transactional
    public DepreciacaoResponse criar(DepreciacaoRequest request) {
        Depreciacao entity = new Depreciacao();
        mapFields(entity, request);
        return DepreciacaoResponse.from(repository.save(entity));
    }

    @Transactional
    public DepreciacaoResponse atualizar(Long id, DepreciacaoRequest request) {
        Depreciacao entity = findOrFail(id);
        mapFields(entity, request);
        return DepreciacaoResponse.from(repository.save(entity));
    }

    @Transactional
    public void excluir(Long id) {
        Depreciacao entity = findOrFail(id);
        entity.softDelete();
        repository.save(entity);
    }

    private Depreciacao findOrFail(Long id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Depreciação não encontrada"));
    }

    private void mapFields(Depreciacao entity, DepreciacaoRequest r) {
        entity.setEmpresaId(r.empresaId());
        entity.setFilialId(r.filialId());
        entity.setBemPatrimonialId(r.bemPatrimonialId());
        if (r.dataDepreciacao() != null) entity.setDataDepreciacao(r.dataDepreciacao());
        if (r.valorDepreciacao() != null) entity.setValorDepreciacao(r.valorDepreciacao());
        if (r.valorAcumulado() != null) entity.setValorAcumulado(r.valorAcumulado());
        if (r.valorLiquido() != null) entity.setValorLiquido(r.valorLiquido());
        if (r.mesReferencia() != null) entity.setMesReferencia(r.mesReferencia());
        if (r.anoReferencia() != null) entity.setAnoReferencia(r.anoReferencia());
    }
}
