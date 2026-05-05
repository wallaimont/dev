package com.orionerp.modules.cadastros.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.cadastros.domain.TabelaPreco;
import com.orionerp.modules.cadastros.dto.TabelaPrecoRequest;
import com.orionerp.modules.cadastros.dto.TabelaPrecoResponse;
import com.orionerp.modules.cadastros.repository.CadastrosSpecifications;
import com.orionerp.modules.cadastros.repository.TabelaPrecoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TabelaPrecoService {

    private final TabelaPrecoRepository tabelaPrecoRepository;

    @Transactional(readOnly = true)
    public Page<TabelaPrecoResponse> list(Long empresaId, String term, Pageable pageable) {
        return tabelaPrecoRepository.findAll(
                CadastrosSpecifications.tabelaPrecoFilter(empresaId, term), pageable
        ).map(TabelaPrecoResponse::from);
    }

    @Transactional(readOnly = true)
    public TabelaPrecoResponse getById(Long id) {
        TabelaPreco entity = tabelaPrecoRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tabela de preço não encontrada: " + id));
        return TabelaPrecoResponse.from(entity);
    }

    @Transactional
    public TabelaPrecoResponse create(TabelaPrecoRequest request) {
        if (tabelaPrecoRepository.existsByEmpresaIdAndCodigoAndDeletedFalse(request.empresaId(), request.codigo())) {
            throw new BusinessException("Já existe uma tabela de preço com o código: " + request.codigo());
        }

        TabelaPreco entity = new TabelaPreco();
        entity.setEmpresaId(request.empresaId());
        entity.setCodigo(request.codigo());
        entity.setNome(request.nome());
        entity.setVigenciaInicio(request.vigenciaInicio());
        entity.setVigenciaFim(request.vigenciaFim());

        entity = tabelaPrecoRepository.save(entity);
        return TabelaPrecoResponse.from(entity);
    }

    @Transactional
    public TabelaPrecoResponse update(Long id, TabelaPrecoRequest request) {
        TabelaPreco entity = tabelaPrecoRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tabela de preço não encontrada: " + id));

        entity.setCodigo(request.codigo());
        entity.setNome(request.nome());
        entity.setVigenciaInicio(request.vigenciaInicio());
        entity.setVigenciaFim(request.vigenciaFim());

        entity = tabelaPrecoRepository.save(entity);
        return TabelaPrecoResponse.from(entity);
    }

    @Transactional
    public void delete(Long id) {
        TabelaPreco entity = tabelaPrecoRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tabela de preço não encontrada: " + id));
        entity.softDelete();
        tabelaPrecoRepository.save(entity);
    }
}
