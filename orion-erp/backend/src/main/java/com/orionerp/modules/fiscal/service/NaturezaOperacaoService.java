package com.orionerp.modules.fiscal.service;

import com.orionerp.common.PageResponse;
import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.fiscal.domain.Cfop;
import com.orionerp.modules.fiscal.domain.NaturezaOperacao;
import com.orionerp.modules.fiscal.dto.NaturezaOperacaoRequest;
import com.orionerp.modules.fiscal.dto.NaturezaOperacaoResponse;
import com.orionerp.modules.fiscal.repository.CfopRepository;
import com.orionerp.modules.fiscal.repository.FiscalSpecifications;
import com.orionerp.modules.fiscal.repository.NaturezaOperacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NaturezaOperacaoService {

    private final NaturezaOperacaoRepository naturezaOperacaoRepository;
    private final CfopRepository cfopRepository;

    @Transactional(readOnly = true)
    public PageResponse<NaturezaOperacaoResponse> list(Long empresaId, String tipo, String term, Pageable pageable) {
        var spec = FiscalSpecifications.naturezaOperacaoFilter(empresaId, tipo, term);
        var page = naturezaOperacaoRepository.findAll(spec, pageable).map(NaturezaOperacaoResponse::from);
        return PageResponse.from(page);
    }

    @Transactional(readOnly = true)
    public NaturezaOperacaoResponse getById(Long id) {
        NaturezaOperacao entity = naturezaOperacaoRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Natureza de operação não encontrada: " + id));
        return NaturezaOperacaoResponse.from(entity);
    }

    @Transactional
    public NaturezaOperacaoResponse create(NaturezaOperacaoRequest request) {
        if (naturezaOperacaoRepository.existsByEmpresaIdAndCodigoAndDeletedFalse(
                request.empresaId(), request.codigo())) {
            throw new BusinessException("Já existe natureza de operação com código '" + request.codigo()
                    + "' para esta empresa");
        }

        NaturezaOperacao entity = new NaturezaOperacao();
        entity.setEmpresaId(request.empresaId());
        entity.setCodigo(request.codigo());
        entity.setNome(request.nome());
        entity.setTipo(request.tipo());
        entity.setGeraFinanceiro(request.geraFinanceiro() != null ? request.geraFinanceiro() : true);
        entity.setMovimentaEstoque(request.movimentaEstoque() != null ? request.movimentaEstoque() : true);

        if (request.cfopId() != null) {
            Cfop cfop = cfopRepository.findById(request.cfopId())
                    .orElseThrow(() -> new ResourceNotFoundException("CFOP não encontrado: " + request.cfopId()));
            entity.setCfop(cfop);
        }

        return NaturezaOperacaoResponse.from(naturezaOperacaoRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        NaturezaOperacao entity = naturezaOperacaoRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Natureza de operação não encontrada: " + id));
        entity.softDelete();
        naturezaOperacaoRepository.save(entity);
    }
}
