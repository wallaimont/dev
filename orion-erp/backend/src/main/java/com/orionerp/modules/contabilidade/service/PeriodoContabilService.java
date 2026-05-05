package com.orionerp.modules.contabilidade.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.contabilidade.domain.PeriodoContabil;
import com.orionerp.modules.contabilidade.dto.PeriodoContabilRequest;
import com.orionerp.modules.contabilidade.dto.PeriodoContabilResponse;
import com.orionerp.modules.contabilidade.repository.PeriodoContabilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PeriodoContabilService {

    private final PeriodoContabilRepository repository;

    @Transactional(readOnly = true)
    public List<PeriodoContabilResponse> listarPorAno(Long empresaId, Integer ano) {
        return repository.findByEmpresaIdAndAnoOrderByMes(empresaId, ano).stream()
                .map(PeriodoContabilResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PeriodoContabilResponse buscarPorId(Long id) {
        return PeriodoContabilResponse.from(findOrFail(id));
    }

    @Transactional
    public PeriodoContabilResponse criar(PeriodoContabilRequest request) {
        repository.findByEmpresaIdAndAnoAndMes(request.empresaId(), request.ano(), request.mes())
                .ifPresent(p -> { throw new BusinessException("Período " + request.mes() + "/" + request.ano() + " já existe"); });
        PeriodoContabil entity = new PeriodoContabil();
        entity.setEmpresaId(request.empresaId());
        entity.setAno(request.ano());
        entity.setMes(request.mes());
        entity.setDataInicio(request.dataInicio());
        entity.setDataFim(request.dataFim());
        entity.setStatus("ABERTO");
        return PeriodoContabilResponse.from(repository.save(entity));
    }

    @Transactional
    public PeriodoContabilResponse fechar(Long id, String usuario) {
        PeriodoContabil entity = findOrFail(id);
        if ("FECHADO".equals(entity.getStatus())) {
            throw new BusinessException("Período já está fechado");
        }
        entity.setStatus("FECHADO");
        entity.setFechadoPor(usuario);
        entity.setFechadoEm(LocalDateTime.now());
        return PeriodoContabilResponse.from(repository.save(entity));
    }

    @Transactional
    public PeriodoContabilResponse reabrir(Long id) {
        PeriodoContabil entity = findOrFail(id);
        if (!"FECHADO".equals(entity.getStatus())) {
            throw new BusinessException("Somente períodos fechados podem ser reabertos");
        }
        entity.setStatus("REABERTO");
        entity.setFechadoPor(null);
        entity.setFechadoEm(null);
        return PeriodoContabilResponse.from(repository.save(entity));
    }

    private PeriodoContabil findOrFail(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Período contábil não encontrado"));
    }
}
