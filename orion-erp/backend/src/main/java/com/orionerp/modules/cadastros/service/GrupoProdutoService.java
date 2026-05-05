package com.orionerp.modules.cadastros.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.cadastros.domain.GrupoProduto;
import com.orionerp.modules.cadastros.dto.GrupoProdutoRequest;
import com.orionerp.modules.cadastros.dto.GrupoProdutoResponse;
import com.orionerp.modules.cadastros.repository.CadastrosSpecifications;
import com.orionerp.modules.cadastros.repository.GrupoProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GrupoProdutoService {

    private final GrupoProdutoRepository grupoProdutoRepository;

    @Transactional(readOnly = true)
    public Page<GrupoProdutoResponse> list(Long empresaId, String term, Pageable pageable) {
        return grupoProdutoRepository.findAll(
                CadastrosSpecifications.grupoProdutoFilter(empresaId, term), pageable
        ).map(GrupoProdutoResponse::from);
    }

    @Transactional(readOnly = true)
    public GrupoProdutoResponse getById(Long id) {
        GrupoProduto entity = grupoProdutoRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grupo de produto não encontrado: " + id));
        return GrupoProdutoResponse.from(entity);
    }

    @Transactional
    public GrupoProdutoResponse create(GrupoProdutoRequest request) {
        if (grupoProdutoRepository.existsByEmpresaIdAndCodigoAndDeletedFalse(request.empresaId(), request.codigo())) {
            throw new BusinessException("Já existe um grupo de produto com o código: " + request.codigo());
        }

        GrupoProduto entity = new GrupoProduto();
        entity.setEmpresaId(request.empresaId());
        entity.setCodigo(request.codigo());
        entity.setNome(request.nome());

        entity = grupoProdutoRepository.save(entity);
        return GrupoProdutoResponse.from(entity);
    }

    @Transactional
    public GrupoProdutoResponse update(Long id, GrupoProdutoRequest request) {
        GrupoProduto entity = grupoProdutoRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grupo de produto não encontrado: " + id));

        entity.setCodigo(request.codigo());
        entity.setNome(request.nome());

        entity = grupoProdutoRepository.save(entity);
        return GrupoProdutoResponse.from(entity);
    }

    @Transactional
    public void delete(Long id) {
        GrupoProduto entity = grupoProdutoRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grupo de produto não encontrado: " + id));
        entity.softDelete();
        grupoProdutoRepository.save(entity);
    }
}
