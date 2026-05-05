package com.orionerp.modules.rh.service;

import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.rh.domain.PontoEletronico;
import com.orionerp.modules.rh.dto.PontoEletronicoRequest;
import com.orionerp.modules.rh.dto.PontoEletronicoResponse;
import com.orionerp.modules.rh.repository.PontoEletronicoRepository;
import com.orionerp.modules.rh.repository.RhSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PontoEletronicoService {

    private final PontoEletronicoRepository repository;

    @Transactional(readOnly = true)
    public Page<PontoEletronicoResponse> listar(Long empresaId, Long funcionarioId,
                                                 LocalDate dataInicio, LocalDate dataFim,
                                                 String tipo, Pageable pageable) {
        return repository.findAll(
                RhSpecifications.pontoEletronicoFilter(empresaId, funcionarioId, dataInicio, dataFim, tipo), pageable)
                .map(PontoEletronicoResponse::from);
    }

    @Transactional(readOnly = true)
    public PontoEletronicoResponse buscarPorId(Long id) {
        return PontoEletronicoResponse.from(findOrFail(id));
    }

    @Transactional
    public PontoEletronicoResponse criar(PontoEletronicoRequest request) {
        PontoEletronico entity = new PontoEletronico();
        mapFields(entity, request);
        return PontoEletronicoResponse.from(repository.save(entity));
    }

    @Transactional
    public PontoEletronicoResponse atualizar(Long id, PontoEletronicoRequest request) {
        PontoEletronico entity = findOrFail(id);
        mapFields(entity, request);
        return PontoEletronicoResponse.from(repository.save(entity));
    }

    @Transactional
    public PontoEletronicoResponse aprovar(Long id) {
        PontoEletronico entity = findOrFail(id);
        entity.setAprovado(true);
        return PontoEletronicoResponse.from(repository.save(entity));
    }

    @Transactional
    public void excluir(Long id) {
        PontoEletronico entity = findOrFail(id);
        entity.softDelete();
        repository.save(entity);
    }

    private PontoEletronico findOrFail(Long id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de ponto não encontrado"));
    }

    private void mapFields(PontoEletronico entity, PontoEletronicoRequest r) {
        entity.setEmpresaId(r.empresaId());
        entity.setFilialId(r.filialId());
        entity.setFuncionarioId(r.funcionarioId());
        entity.setData(r.data());
        entity.setEntrada1(r.entrada1());
        entity.setSaida1(r.saida1());
        entity.setEntrada2(r.entrada2());
        entity.setSaida2(r.saida2());
        entity.setEntrada3(r.entrada3());
        entity.setSaida3(r.saida3());
        entity.setHorasTrabalhadas(r.horasTrabalhadas() != null ? r.horasTrabalhadas() : java.math.BigDecimal.ZERO);
        entity.setHorasExtras(r.horasExtras() != null ? r.horasExtras() : java.math.BigDecimal.ZERO);
        entity.setHorasFalta(r.horasFalta() != null ? r.horasFalta() : java.math.BigDecimal.ZERO);
        entity.setTipo(r.tipo() != null ? r.tipo() : "NORMAL");
        entity.setAprovado(r.aprovado() != null ? r.aprovado() : false);
    }
}
