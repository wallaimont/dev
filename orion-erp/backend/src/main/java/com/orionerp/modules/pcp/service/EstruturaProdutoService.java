package com.orionerp.modules.pcp.service;

import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.pcp.domain.EstruturaProduto;
import com.orionerp.modules.pcp.dto.EstruturaProdutoRequest;
import com.orionerp.modules.pcp.dto.EstruturaProdutoResponse;
import com.orionerp.modules.pcp.repository.EstruturaProdutoRepository;
import com.orionerp.modules.pcp.repository.PcpSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EstruturaProdutoService {

    private final EstruturaProdutoRepository repository;

    @Transactional(readOnly = true)
    public Page<EstruturaProdutoResponse> listar(Long empresaId, Long produtoPaiId, Pageable pageable) {
        return repository.findAll(
                PcpSpecifications.estruturaProdutoFilter(empresaId, produtoPaiId), pageable)
                .map(EstruturaProdutoResponse::from);
    }

    @Transactional(readOnly = true)
    public EstruturaProdutoResponse buscarPorId(Long id) {
        return EstruturaProdutoResponse.from(findOrFail(id));
    }

    @Transactional
    public EstruturaProdutoResponse criar(EstruturaProdutoRequest request) {
        EstruturaProduto entity = new EstruturaProduto();
        mapFields(entity, request);
        return EstruturaProdutoResponse.from(repository.save(entity));
    }

    @Transactional
    public EstruturaProdutoResponse atualizar(Long id, EstruturaProdutoRequest request) {
        EstruturaProduto entity = findOrFail(id);
        mapFields(entity, request);
        return EstruturaProdutoResponse.from(repository.save(entity));
    }

    @Transactional
    public void excluir(Long id) {
        EstruturaProduto entity = findOrFail(id);
        entity.softDelete();
        repository.save(entity);
    }

    private EstruturaProduto findOrFail(Long id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estrutura de produto não encontrada"));
    }

    private void mapFields(EstruturaProduto entity, EstruturaProdutoRequest r) {
        entity.setEmpresaId(r.empresaId());
        entity.setFilialId(r.filialId());
        entity.setProdutoPaiId(r.produtoPaiId());
        entity.setProdutoFilhoId(r.produtoFilhoId());
        entity.setQuantidade(r.quantidade());
        entity.setUnidadeMedida(r.unidadeMedida());
        entity.setPerdaPercentual(r.perdaPercentual() != null ? r.perdaPercentual() : java.math.BigDecimal.ZERO);
        entity.setObservacao(r.observacao());
    }
}
