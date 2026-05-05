package com.orionerp.modules.cadastros.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.cadastros.domain.Marca;
import com.orionerp.modules.cadastros.dto.MarcaRequest;
import com.orionerp.modules.cadastros.dto.MarcaResponse;
import com.orionerp.modules.cadastros.repository.CadastrosSpecifications;
import com.orionerp.modules.cadastros.repository.MarcaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarcaService {

    private final MarcaRepository marcaRepository;

    @Transactional(readOnly = true)
    public Page<MarcaResponse> list(Long empresaId, String term, Pageable pageable) {
        return marcaRepository.findAll(
                CadastrosSpecifications.marcaFilter(empresaId, term), pageable
        ).map(MarcaResponse::from);
    }

    @Transactional(readOnly = true)
    public MarcaResponse getById(Long id) {
        Marca entity = marcaRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marca não encontrada: " + id));
        return MarcaResponse.from(entity);
    }

    @Transactional
    public MarcaResponse create(MarcaRequest request) {
        if (marcaRepository.existsByEmpresaIdAndCodigoAndDeletedFalse(request.empresaId(), request.codigo())) {
            throw new BusinessException("Já existe uma marca com o código: " + request.codigo());
        }

        Marca entity = new Marca();
        entity.setEmpresaId(request.empresaId());
        entity.setCodigo(request.codigo());
        entity.setNome(request.nome());

        entity = marcaRepository.save(entity);
        return MarcaResponse.from(entity);
    }

    @Transactional
    public MarcaResponse update(Long id, MarcaRequest request) {
        Marca entity = marcaRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marca não encontrada: " + id));

        entity.setCodigo(request.codigo());
        entity.setNome(request.nome());

        entity = marcaRepository.save(entity);
        return MarcaResponse.from(entity);
    }

    @Transactional
    public void delete(Long id) {
        Marca entity = marcaRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marca não encontrada: " + id));
        entity.softDelete();
        marcaRepository.save(entity);
    }
}
