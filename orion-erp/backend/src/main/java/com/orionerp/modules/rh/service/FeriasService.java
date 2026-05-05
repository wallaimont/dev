package com.orionerp.modules.rh.service;

import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.rh.domain.Ferias;
import com.orionerp.modules.rh.dto.FeriasRequest;
import com.orionerp.modules.rh.dto.FeriasResponse;
import com.orionerp.modules.rh.repository.FeriasRepository;
import com.orionerp.modules.rh.repository.RhSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeriasService {

    private final FeriasRepository repository;

    @Transactional(readOnly = true)
    public Page<FeriasResponse> listar(Long empresaId, Long funcionarioId, String status, Pageable pageable) {
        return repository.findAll(
                RhSpecifications.feriasFilter(empresaId, funcionarioId, status), pageable)
                .map(FeriasResponse::from);
    }

    @Transactional(readOnly = true)
    public FeriasResponse buscarPorId(Long id) {
        return FeriasResponse.from(findOrFail(id));
    }

    @Transactional
    public FeriasResponse criar(FeriasRequest request) {
        Ferias entity = new Ferias();
        mapFields(entity, request);
        entity.setStatus("PROGRAMADA");
        return FeriasResponse.from(repository.save(entity));
    }

    @Transactional
    public FeriasResponse atualizar(Long id, FeriasRequest request) {
        Ferias entity = findOrFail(id);
        mapFields(entity, request);
        return FeriasResponse.from(repository.save(entity));
    }

    @Transactional
    public FeriasResponse aprovar(Long id) {
        Ferias entity = findOrFail(id);
        entity.setStatus("APROVADA");
        return FeriasResponse.from(repository.save(entity));
    }

    @Transactional
    public void excluir(Long id) {
        Ferias entity = findOrFail(id);
        entity.softDelete();
        repository.save(entity);
    }

    private Ferias findOrFail(Long id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Férias não encontradas"));
    }

    private void mapFields(Ferias entity, FeriasRequest r) {
        entity.setEmpresaId(r.empresaId());
        entity.setFilialId(r.filialId());
        entity.setFuncionarioId(r.funcionarioId());
        entity.setPeriodoAquisitivoInicio(r.periodoAquisitivoInicio());
        entity.setPeriodoAquisitivoFim(r.periodoAquisitivoFim());
        entity.setDataInicio(r.dataInicio());
        entity.setDataFim(r.dataFim());
        entity.setDiasGozo(r.diasGozo());
        entity.setDiasAbono(r.diasAbono() != null ? r.diasAbono() : 0);
        entity.setValorFerias(r.valorFerias() != null ? r.valorFerias() : java.math.BigDecimal.ZERO);
        entity.setValorAbono(r.valorAbono() != null ? r.valorAbono() : java.math.BigDecimal.ZERO);
        entity.setValorAdiantamento13(r.valorAdiantamento13() != null ? r.valorAdiantamento13() : java.math.BigDecimal.ZERO);
    }
}
