package com.orionerp.modules.vendas.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.vendas.domain.TabelaPrecoItem;
import com.orionerp.modules.vendas.dto.TabelaPrecoItemRequest;
import com.orionerp.modules.vendas.dto.TabelaPrecoItemResponse;
import com.orionerp.modules.vendas.repository.TabelaPrecoItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TabelaPrecoItemService {

    private final TabelaPrecoItemRepository repository;

    @Transactional(readOnly = true)
    public List<TabelaPrecoItemResponse> listarPorTabela(Long tabelaPrecoId) {
        return repository.findByTabelaPrecoIdAndDeletedFalse(tabelaPrecoId).stream()
                .map(TabelaPrecoItemResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public TabelaPrecoItemResponse buscarPorId(Long id) {
        return TabelaPrecoItemResponse.from(findOrFail(id));
    }

    @Transactional
    public TabelaPrecoItemResponse criar(TabelaPrecoItemRequest request) {
        if (repository.existsByTabelaPrecoIdAndProdutoIdAndDeletedFalse(request.tabelaPrecoId(), request.produtoId())) {
            throw new BusinessException("Produto já cadastrado nesta tabela de preço");
        }
        TabelaPrecoItem entity = new TabelaPrecoItem();
        mapFields(entity, request);
        return TabelaPrecoItemResponse.from(repository.save(entity));
    }

    @Transactional
    public TabelaPrecoItemResponse atualizar(Long id, TabelaPrecoItemRequest request) {
        TabelaPrecoItem entity = findOrFail(id);
        mapFields(entity, request);
        return TabelaPrecoItemResponse.from(repository.save(entity));
    }

    @Transactional
    public void excluir(Long id) {
        TabelaPrecoItem entity = findOrFail(id);
        entity.softDelete();
        repository.save(entity);
    }

    private TabelaPrecoItem findOrFail(Long id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item de tabela de preço não encontrado"));
    }

    private void mapFields(TabelaPrecoItem entity, TabelaPrecoItemRequest r) {
        entity.setEmpresaId(r.empresaId());
        entity.setFilialId(r.filialId());
        entity.setTabelaPrecoId(r.tabelaPrecoId());
        entity.setProdutoId(r.produtoId());
        entity.setPreco(r.preco());
        entity.setPrecoPromocional(r.precoPromocional());
        entity.setQuantidadeMinima(r.quantidadeMinima());
    }
}
