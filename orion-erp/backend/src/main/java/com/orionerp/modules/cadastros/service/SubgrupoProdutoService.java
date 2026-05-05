package com.orionerp.modules.cadastros.service;

import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.cadastros.domain.GrupoProduto;
import com.orionerp.modules.cadastros.domain.SubgrupoProduto;
import com.orionerp.modules.cadastros.dto.SubgrupoProdutoRequest;
import com.orionerp.modules.cadastros.dto.SubgrupoProdutoResponse;
import com.orionerp.modules.cadastros.repository.CadastrosSpecifications;
import com.orionerp.modules.cadastros.repository.GrupoProdutoRepository;
import com.orionerp.modules.cadastros.repository.SubgrupoProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubgrupoProdutoService {

    private final SubgrupoProdutoRepository subgrupoProdutoRepository;
    private final GrupoProdutoRepository grupoProdutoRepository;

    @Transactional(readOnly = true)
    public Page<SubgrupoProdutoResponse> list(Long empresaId, Long grupoId, String term, Pageable pageable) {
        return subgrupoProdutoRepository.findAll(
                CadastrosSpecifications.subgrupoProdutoFilter(empresaId, grupoId, term), pageable
        ).map(SubgrupoProdutoResponse::from);
    }

    @Transactional(readOnly = true)
    public SubgrupoProdutoResponse getById(Long id) {
        SubgrupoProduto entity = subgrupoProdutoRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subgrupo de produto não encontrado: " + id));
        return SubgrupoProdutoResponse.from(entity);
    }

    @Transactional
    public SubgrupoProdutoResponse create(SubgrupoProdutoRequest request) {
        GrupoProduto grupo = grupoProdutoRepository.findByIdAndDeletedFalse(request.grupoId())
                .orElseThrow(() -> new ResourceNotFoundException("Grupo de produto não encontrado: " + request.grupoId()));

        SubgrupoProduto entity = new SubgrupoProduto();
        entity.setEmpresaId(request.empresaId());
        entity.setGrupo(grupo);
        entity.setCodigo(request.codigo());
        entity.setNome(request.nome());

        entity = subgrupoProdutoRepository.save(entity);
        return SubgrupoProdutoResponse.from(entity);
    }

    @Transactional
    public SubgrupoProdutoResponse update(Long id, SubgrupoProdutoRequest request) {
        SubgrupoProduto entity = subgrupoProdutoRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subgrupo de produto não encontrado: " + id));

        GrupoProduto grupo = grupoProdutoRepository.findByIdAndDeletedFalse(request.grupoId())
                .orElseThrow(() -> new ResourceNotFoundException("Grupo de produto não encontrado: " + request.grupoId()));

        entity.setGrupo(grupo);
        entity.setCodigo(request.codigo());
        entity.setNome(request.nome());

        entity = subgrupoProdutoRepository.save(entity);
        return SubgrupoProdutoResponse.from(entity);
    }

    @Transactional
    public void delete(Long id) {
        SubgrupoProduto entity = subgrupoProdutoRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subgrupo de produto não encontrado: " + id));
        entity.softDelete();
        subgrupoProdutoRepository.save(entity);
    }
}
