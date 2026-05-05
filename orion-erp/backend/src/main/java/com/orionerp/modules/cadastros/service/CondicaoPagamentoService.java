package com.orionerp.modules.cadastros.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.cadastros.domain.CondicaoPagamento;
import com.orionerp.modules.cadastros.dto.CondicaoPagamentoRequest;
import com.orionerp.modules.cadastros.dto.CondicaoPagamentoResponse;
import com.orionerp.modules.cadastros.repository.CadastrosSpecifications;
import com.orionerp.modules.cadastros.repository.CondicaoPagamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CondicaoPagamentoService {

    private final CondicaoPagamentoRepository condicaoPagamentoRepository;

    @Transactional(readOnly = true)
    public Page<CondicaoPagamentoResponse> list(Long empresaId, String tipo, String term, Pageable pageable) {
        return condicaoPagamentoRepository.findAll(
                CadastrosSpecifications.condicaoPagamentoFilter(empresaId, tipo, term), pageable
        ).map(CondicaoPagamentoResponse::from);
    }

    @Transactional(readOnly = true)
    public CondicaoPagamentoResponse getById(Long id) {
        CondicaoPagamento entity = condicaoPagamentoRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Condição de pagamento não encontrada: " + id));
        return CondicaoPagamentoResponse.from(entity);
    }

    @Transactional
    public CondicaoPagamentoResponse create(CondicaoPagamentoRequest request) {
        if (condicaoPagamentoRepository.existsByEmpresaIdAndCodigoAndDeletedFalse(request.empresaId(), request.codigo())) {
            throw new BusinessException("Já existe uma condição de pagamento com o código: " + request.codigo());
        }

        CondicaoPagamento entity = new CondicaoPagamento();
        entity.setEmpresaId(request.empresaId());
        entity.setCodigo(request.codigo());
        entity.setNome(request.nome());
        if (request.tipo() != null) entity.setTipo(request.tipo());

        entity = condicaoPagamentoRepository.save(entity);
        return CondicaoPagamentoResponse.from(entity);
    }

    @Transactional
    public CondicaoPagamentoResponse update(Long id, CondicaoPagamentoRequest request) {
        CondicaoPagamento entity = condicaoPagamentoRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Condição de pagamento não encontrada: " + id));

        entity.setCodigo(request.codigo());
        entity.setNome(request.nome());
        if (request.tipo() != null) entity.setTipo(request.tipo());

        entity = condicaoPagamentoRepository.save(entity);
        return CondicaoPagamentoResponse.from(entity);
    }

    @Transactional
    public void delete(Long id) {
        CondicaoPagamento entity = condicaoPagamentoRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Condição de pagamento não encontrada: " + id));
        entity.softDelete();
        condicaoPagamentoRepository.save(entity);
    }
}
