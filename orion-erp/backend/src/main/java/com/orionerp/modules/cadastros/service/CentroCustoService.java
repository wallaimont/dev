package com.orionerp.modules.cadastros.service;

import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.cadastros.domain.CentroCusto;
import com.orionerp.modules.cadastros.dto.CentroCustoRequest;
import com.orionerp.modules.cadastros.dto.CentroCustoResponse;
import com.orionerp.modules.cadastros.repository.CadastrosSpecifications;
import com.orionerp.modules.cadastros.repository.CentroCustoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CentroCustoService {

    private final CentroCustoRepository centroCustoRepository;

    @Transactional(readOnly = true)
    public Page<CentroCustoResponse> list(Long empresaId, String term, Pageable pageable) {
        return centroCustoRepository.findAll(
                CadastrosSpecifications.centroCustoFilter(empresaId, term), pageable
        ).map(CentroCustoResponse::from);
    }

    @Transactional(readOnly = true)
    public CentroCustoResponse getById(Long id) {
        CentroCusto entity = centroCustoRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Centro de custo não encontrado: " + id));
        return CentroCustoResponse.from(entity);
    }

    @Transactional
    public CentroCustoResponse create(CentroCustoRequest request) {
        CentroCusto entity = new CentroCusto();
        entity.setEmpresaId(request.empresaId());
        entity.setCodigo(request.codigo());
        entity.setNome(request.nome());
        entity.setParentId(request.parentId());
        entity.setNivel(request.nivel() != null ? request.nivel() : 1);

        entity = centroCustoRepository.save(entity);
        return CentroCustoResponse.from(entity);
    }

    @Transactional
    public CentroCustoResponse update(Long id, CentroCustoRequest request) {
        CentroCusto entity = centroCustoRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Centro de custo não encontrado: " + id));

        entity.setCodigo(request.codigo());
        entity.setNome(request.nome());
        entity.setParentId(request.parentId());
        if (request.nivel() != null) {
            entity.setNivel(request.nivel());
        }

        entity = centroCustoRepository.save(entity);
        return CentroCustoResponse.from(entity);
    }

    @Transactional
    public void delete(Long id) {
        CentroCusto entity = centroCustoRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Centro de custo não encontrado: " + id));
        entity.softDelete();
        centroCustoRepository.save(entity);
    }
}
