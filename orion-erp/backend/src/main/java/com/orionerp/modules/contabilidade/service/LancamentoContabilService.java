package com.orionerp.modules.contabilidade.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.contabilidade.domain.LancamentoContabil;
import com.orionerp.modules.contabilidade.dto.LancamentoContabilRequest;
import com.orionerp.modules.contabilidade.dto.LancamentoContabilResponse;
import com.orionerp.modules.contabilidade.repository.ContabilidadeSpecifications;
import com.orionerp.modules.contabilidade.repository.LancamentoContabilRepository;
import com.orionerp.modules.contabilidade.repository.PeriodoContabilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class LancamentoContabilService {

    private final LancamentoContabilRepository repository;
    private final PeriodoContabilRepository periodoRepository;

    @Transactional(readOnly = true)
    public Page<LancamentoContabilResponse> listar(Long empresaId, String lote,
                                                    LocalDate dataInicio, LocalDate dataFim,
                                                    String status, Pageable pageable) {
        return repository.findAll(
                ContabilidadeSpecifications.lancamentoFilter(empresaId, lote, dataInicio, dataFim, status), pageable
        ).map(LancamentoContabilResponse::from);
    }

    @Transactional(readOnly = true)
    public LancamentoContabilResponse buscarPorId(Long id) {
        return LancamentoContabilResponse.from(findOrFail(id));
    }

    @Transactional
    public LancamentoContabilResponse criar(LancamentoContabilRequest request) {
        validarPeriodoAberto(request.empresaId(), request.dataLancamento());
        if (request.contaDebitoId().equals(request.contaCreditoId())) {
            throw new BusinessException("Conta débito e crédito não podem ser iguais");
        }
        LancamentoContabil entity = new LancamentoContabil();
        mapToEntity(request, entity);
        entity.setStatus("DIGITADO");
        return LancamentoContabilResponse.from(repository.save(entity));
    }

    @Transactional
    public LancamentoContabilResponse atualizar(Long id, LancamentoContabilRequest request) {
        LancamentoContabil entity = findOrFail(id);
        if (!"DIGITADO".equals(entity.getStatus())) {
            throw new BusinessException("Somente lançamentos com status DIGITADO podem ser alterados");
        }
        mapToEntity(request, entity);
        return LancamentoContabilResponse.from(repository.save(entity));
    }

    @Transactional
    public LancamentoContabilResponse alterarStatus(Long id, String novoStatus) {
        LancamentoContabil entity = findOrFail(id);
        entity.setStatus(novoStatus);
        return LancamentoContabilResponse.from(repository.save(entity));
    }

    @Transactional
    public void excluir(Long id) {
        LancamentoContabil entity = findOrFail(id);
        if (!"DIGITADO".equals(entity.getStatus())) {
            throw new BusinessException("Somente lançamentos DIGITADOS podem ser excluídos");
        }
        entity.softDelete();
        repository.save(entity);
    }

    private void validarPeriodoAberto(Long empresaId, LocalDate data) {
        periodoRepository.findByEmpresaIdAndAnoAndMes(empresaId, data.getYear(), data.getMonthValue())
                .ifPresent(p -> {
                    if ("FECHADO".equals(p.getStatus())) {
                        throw new BusinessException("Período contábil " + p.getMes() + "/" + p.getAno() + " está fechado");
                    }
                });
    }

    private LancamentoContabil findOrFail(Long id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lançamento contábil não encontrado"));
    }

    private void mapToEntity(LancamentoContabilRequest req, LancamentoContabil entity) {
        entity.setEmpresaId(req.empresaId());
        entity.setFilialId(req.filialId());
        entity.setLote(req.lote());
        entity.setSublote(req.sublote());
        entity.setNumero(req.numero());
        entity.setDataLancamento(req.dataLancamento());
        entity.setContaDebitoId(req.contaDebitoId());
        entity.setContaCreditoId(req.contaCreditoId());
        entity.setValor(req.valor());
        entity.setHistorico(req.historico());
        entity.setDocumento(req.documento());
        entity.setCentroCustoId(req.centroCustoId());
        entity.setCentroResultadoId(req.centroResultadoId());
        entity.setTipo(req.tipo() != null ? req.tipo() : "NORMAL");
        entity.setOrigem(req.origem());
        entity.setOrigemId(req.origemId());
    }
}
