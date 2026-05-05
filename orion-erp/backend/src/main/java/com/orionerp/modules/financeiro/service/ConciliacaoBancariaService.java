package com.orionerp.modules.financeiro.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.financeiro.domain.ConciliacaoBancaria;
import com.orionerp.modules.financeiro.dto.ConciliacaoBancariaRequest;
import com.orionerp.modules.financeiro.dto.ConciliacaoBancariaResponse;
import com.orionerp.modules.financeiro.repository.ConciliacaoBancariaRepository;
import com.orionerp.modules.financeiro.repository.FinanceiroSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConciliacaoBancariaService {

    private final ConciliacaoBancariaRepository repository;

    @Transactional(readOnly = true)
    public Page<ConciliacaoBancariaResponse> listar(Long empresaId, Long contaBancariaId, String status, Pageable pageable) {
        return repository.findAll(
                FinanceiroSpecifications.conciliacaoFilter(empresaId, contaBancariaId, status), pageable)
                .map(ConciliacaoBancariaResponse::from);
    }

    @Transactional(readOnly = true)
    public ConciliacaoBancariaResponse buscarPorId(Long id) {
        return ConciliacaoBancariaResponse.from(findOrFail(id));
    }

    @Transactional
    public ConciliacaoBancariaResponse criar(ConciliacaoBancariaRequest request) {
        ConciliacaoBancaria entity = new ConciliacaoBancaria();
        mapFields(entity, request);
        return ConciliacaoBancariaResponse.from(repository.save(entity));
    }

    @Transactional
    public ConciliacaoBancariaResponse atualizar(Long id, ConciliacaoBancariaRequest request) {
        ConciliacaoBancaria entity = findOrFail(id);
        if ("FECHADA".equals(entity.getStatus())) {
            throw new BusinessException("Conciliação fechada não pode ser alterada");
        }
        mapFields(entity, request);
        return ConciliacaoBancariaResponse.from(repository.save(entity));
    }

    @Transactional
    public ConciliacaoBancariaResponse fechar(Long id) {
        ConciliacaoBancaria entity = findOrFail(id);
        if ("FECHADA".equals(entity.getStatus())) {
            throw new BusinessException("Conciliação já está fechada");
        }
        entity.setStatus("FECHADA");
        return ConciliacaoBancariaResponse.from(repository.save(entity));
    }

    @Transactional
    public void excluir(Long id) {
        ConciliacaoBancaria entity = findOrFail(id);
        if ("FECHADA".equals(entity.getStatus())) {
            throw new BusinessException("Conciliação fechada não pode ser excluída");
        }
        entity.softDelete();
        repository.save(entity);
    }

    private ConciliacaoBancaria findOrFail(Long id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conciliação bancária não encontrada"));
    }

    private void mapFields(ConciliacaoBancaria entity, ConciliacaoBancariaRequest r) {
        entity.setEmpresaId(r.empresaId());
        entity.setFilialId(r.filialId());
        entity.setContaBancariaId(r.contaBancariaId());
        entity.setDataInicio(r.dataInicio());
        entity.setDataFim(r.dataFim());
        entity.setSaldoExtrato(r.saldoExtrato() != null ? r.saldoExtrato() : java.math.BigDecimal.ZERO);
        entity.setSaldoSistema(r.saldoSistema() != null ? r.saldoSistema() : java.math.BigDecimal.ZERO);
        entity.setDiferenca(r.diferenca() != null ? r.diferenca() : java.math.BigDecimal.ZERO);
        if (r.status() != null) entity.setStatus(r.status());
        entity.setObservacao(r.observacao());
    }
}
