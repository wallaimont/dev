package com.orionerp.modules.cadastros.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.cadastros.domain.Categoria;
import com.orionerp.modules.cadastros.dto.CategoriaRequest;
import com.orionerp.modules.cadastros.dto.CategoriaResponse;
import com.orionerp.modules.cadastros.repository.CadastrosSpecifications;
import com.orionerp.modules.cadastros.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public Page<CategoriaResponse> list(Long empresaId, String term, Pageable pageable) {
        return categoriaRepository.findAll(
                CadastrosSpecifications.categoriaFilter(empresaId, term), pageable
        ).map(CategoriaResponse::from);
    }

    @Transactional(readOnly = true)
    public CategoriaResponse getById(Long id) {
        Categoria entity = categoriaRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + id));
        return CategoriaResponse.from(entity);
    }

    @Transactional
    public CategoriaResponse create(CategoriaRequest request) {
        if (categoriaRepository.existsByEmpresaIdAndCodigoAndDeletedFalse(request.empresaId(), request.codigo())) {
            throw new BusinessException("Já existe uma categoria com o código: " + request.codigo());
        }

        Categoria entity = new Categoria();
        entity.setEmpresaId(request.empresaId());
        entity.setCodigo(request.codigo());
        entity.setNome(request.nome());
        entity.setParentId(request.parentId());

        entity = categoriaRepository.save(entity);
        return CategoriaResponse.from(entity);
    }

    @Transactional
    public CategoriaResponse update(Long id, CategoriaRequest request) {
        Categoria entity = categoriaRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + id));

        entity.setCodigo(request.codigo());
        entity.setNome(request.nome());
        entity.setParentId(request.parentId());

        entity = categoriaRepository.save(entity);
        return CategoriaResponse.from(entity);
    }

    @Transactional
    public void delete(Long id) {
        Categoria entity = categoriaRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + id));
        entity.softDelete();
        categoriaRepository.save(entity);
    }
}
