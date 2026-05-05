package com.orionerp.modules.financeiro.service;

import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.financeiro.domain.ConciliacaoBancariaItem;
import com.orionerp.modules.financeiro.dto.ConciliacaoBancariaItemRequest;
import com.orionerp.modules.financeiro.dto.ConciliacaoBancariaItemResponse;
import com.orionerp.modules.financeiro.repository.ConciliacaoBancariaItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConciliacaoBancariaItemService {

    private final ConciliacaoBancariaItemRepository repository;

    @Transactional(readOnly = true)
    public List<ConciliacaoBancariaItemResponse> listarPorConciliacao(Long conciliacaoId) {
        return repository.findByConciliacaoIdAndDeletedFalse(conciliacaoId).stream()
                .map(ConciliacaoBancariaItemResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ConciliacaoBancariaItemResponse buscarPorId(Long id) {
        return ConciliacaoBancariaItemResponse.from(findOrFail(id));
    }

    @Transactional
    public ConciliacaoBancariaItemResponse criar(ConciliacaoBancariaItemRequest request) {
        ConciliacaoBancariaItem entity = new ConciliacaoBancariaItem();
        mapFields(entity, request);
        return ConciliacaoBancariaItemResponse.from(repository.save(entity));
    }

    @Transactional
    public ConciliacaoBancariaItemResponse atualizar(Long id, ConciliacaoBancariaItemRequest request) {
        ConciliacaoBancariaItem entity = findOrFail(id);
        mapFields(entity, request);
        return ConciliacaoBancariaItemResponse.from(repository.save(entity));
    }

    @Transactional
    public void excluir(Long id) {
        ConciliacaoBancariaItem entity = findOrFail(id);
        entity.softDelete();
        repository.save(entity);
    }

    private ConciliacaoBancariaItem findOrFail(Long id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item de conciliação não encontrado"));
    }

    private void mapFields(ConciliacaoBancariaItem entity, ConciliacaoBancariaItemRequest r) {
        entity.setEmpresaId(r.empresaId());
        entity.setFilialId(r.filialId());
        entity.setConciliacaoId(r.conciliacaoId());
        entity.setLancamentoFinanceiroId(r.lancamentoFinanceiroId());
        entity.setDataExtrato(r.dataExtrato());
        entity.setDescricaoExtrato(r.descricaoExtrato());
        entity.setValorExtrato(r.valorExtrato());
        if (r.conciliado() != null) entity.setConciliado(r.conciliado());
        entity.setDataConciliacao(r.dataConciliacao());
    }
}
