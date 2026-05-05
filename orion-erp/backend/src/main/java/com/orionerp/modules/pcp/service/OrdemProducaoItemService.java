package com.orionerp.modules.pcp.service;

import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.pcp.domain.OrdemProducaoItem;
import com.orionerp.modules.pcp.dto.OrdemProducaoItemRequest;
import com.orionerp.modules.pcp.dto.OrdemProducaoItemResponse;
import com.orionerp.modules.pcp.repository.OrdemProducaoItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdemProducaoItemService {

    private final OrdemProducaoItemRepository repository;

    @Transactional(readOnly = true)
    public List<OrdemProducaoItemResponse> listarPorOrdem(Long ordemProducaoId) {
        return repository.findByOrdemProducaoIdAndDeletedFalse(ordemProducaoId).stream()
                .map(OrdemProducaoItemResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public OrdemProducaoItemResponse buscarPorId(Long id) {
        return OrdemProducaoItemResponse.from(findOrFail(id));
    }

    @Transactional
    public OrdemProducaoItemResponse criar(OrdemProducaoItemRequest request) {
        OrdemProducaoItem entity = new OrdemProducaoItem();
        mapFields(entity, request);
        return OrdemProducaoItemResponse.from(repository.save(entity));
    }

    @Transactional
    public OrdemProducaoItemResponse atualizar(Long id, OrdemProducaoItemRequest request) {
        OrdemProducaoItem entity = findOrFail(id);
        mapFields(entity, request);
        return OrdemProducaoItemResponse.from(repository.save(entity));
    }

    @Transactional
    public void excluir(Long id) {
        OrdemProducaoItem entity = findOrFail(id);
        entity.softDelete();
        repository.save(entity);
    }

    private OrdemProducaoItem findOrFail(Long id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item da ordem não encontrado"));
    }

    private void mapFields(OrdemProducaoItem entity, OrdemProducaoItemRequest r) {
        entity.setEmpresaId(r.empresaId());
        entity.setFilialId(r.filialId());
        entity.setOrdemProducaoId(r.ordemProducaoId());
        entity.setProdutoId(r.produtoId());
        if (r.quantidadePrevista() != null) if (r.quantidadePrevista() != null) entity.setQuantidadePrevista(r.quantidadePrevista());
        if (r.quantidadeUtilizada() != null) entity.setQuantidadeUtilizada(r.quantidadeUtilizada());
        entity.setUnidadeMedida(r.unidadeMedida());
        if (r.custoUnitario() != null) entity.setCustoUnitario(r.custoUnitario());
    }
}
