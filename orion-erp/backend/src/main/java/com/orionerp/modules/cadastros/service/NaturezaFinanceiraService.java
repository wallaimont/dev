package com.orionerp.modules.cadastros.service;

import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.cadastros.domain.NaturezaFinanceira;
import com.orionerp.modules.cadastros.dto.NaturezaFinanceiraRequest;
import com.orionerp.modules.cadastros.dto.NaturezaFinanceiraResponse;
import com.orionerp.modules.cadastros.repository.CadastrosSpecifications;
import com.orionerp.modules.cadastros.repository.NaturezaFinanceiraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NaturezaFinanceiraService {

    private final NaturezaFinanceiraRepository naturezaFinanceiraRepository;

    @Transactional(readOnly = true)
    public Page<NaturezaFinanceiraResponse> list(Long empresaId, String tipo, String term, Pageable pageable) {
        return naturezaFinanceiraRepository.findAll(
                CadastrosSpecifications.naturezaFinanceiraFilter(empresaId, tipo, term), pageable
        ).map(NaturezaFinanceiraResponse::from);
    }

    @Transactional(readOnly = true)
    public NaturezaFinanceiraResponse getById(Long id) {
        NaturezaFinanceira entity = naturezaFinanceiraRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Natureza financeira não encontrada: " + id));
        return NaturezaFinanceiraResponse.from(entity);
    }

    @Transactional
    public NaturezaFinanceiraResponse create(NaturezaFinanceiraRequest request) {
        NaturezaFinanceira entity = new NaturezaFinanceira();
        entity.setEmpresaId(request.empresaId());
        entity.setCodigo(request.codigo());
        entity.setNome(request.nome());
        entity.setTipo(request.tipo());
        entity.setParentId(request.parentId());

        entity = naturezaFinanceiraRepository.save(entity);
        return NaturezaFinanceiraResponse.from(entity);
    }

    @Transactional
    public NaturezaFinanceiraResponse update(Long id, NaturezaFinanceiraRequest request) {
        NaturezaFinanceira entity = naturezaFinanceiraRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Natureza financeira não encontrada: " + id));

        entity.setCodigo(request.codigo());
        entity.setNome(request.nome());
        entity.setTipo(request.tipo());
        entity.setParentId(request.parentId());

        entity = naturezaFinanceiraRepository.save(entity);
        return NaturezaFinanceiraResponse.from(entity);
    }

    @Transactional
    public void delete(Long id) {
        NaturezaFinanceira entity = naturezaFinanceiraRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Natureza financeira não encontrada: " + id));
        entity.softDelete();
        naturezaFinanceiraRepository.save(entity);
    }
}
