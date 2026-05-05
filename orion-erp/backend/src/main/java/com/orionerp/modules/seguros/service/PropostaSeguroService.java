package com.orionerp.modules.seguros.service;

import com.orionerp.common.PageResponse;
import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.cadastros.domain.Cliente;
import com.orionerp.modules.cadastros.repository.ClienteRepository;
import com.orionerp.modules.seguros.domain.Corretora;
import com.orionerp.modules.seguros.domain.PropostaSeguro;
import com.orionerp.modules.seguros.domain.Seguradora;
import com.orionerp.modules.seguros.dto.PropostaSeguroRequest;
import com.orionerp.modules.seguros.dto.PropostaSeguroResponse;
import com.orionerp.modules.seguros.repository.CorretoraRepository;
import com.orionerp.modules.seguros.repository.PropostaSeguroRepository;
import com.orionerp.modules.seguros.repository.SeguradoraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PropostaSeguroService {

    private final PropostaSeguroRepository repository;
    private final ClienteRepository clienteRepository;
    private final SeguradoraRepository seguradoraRepository;
    private final CorretoraRepository corretoraRepository;

    @Transactional(readOnly = true)
    public PageResponse<PropostaSeguroResponse> list(Long empresaId, Long clienteId, Long seguradoraId, String status, String search, int page, int size) {
        Page<PropostaSeguro> pg = repository.findAll(
                SegurosSpecifications.propostaFilter(empresaId, clienteId, seguradoraId, status, search),
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
        return PageResponse.<PropostaSeguroResponse>builder()
                .items(pg.getContent().stream().map(this::toResponse).toList())
                .page(pg.getNumber()).size(pg.getSize())
                .totalElements(pg.getTotalElements()).totalPages(pg.getTotalPages())
                .first(pg.isFirst()).last(pg.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public PropostaSeguroResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public PropostaSeguroResponse create(PropostaSeguroRequest req, String user) {
        if (req.vigenciaFim().isBefore(req.vigenciaInicio())) {
            throw new BusinessException("Data final de vigência deve ser posterior à data inicial");
        }
        PropostaSeguro e = new PropostaSeguro();
        applyFields(e, req);
        e.setCreatedBy(user);
        return toResponse(repository.save(e));
    }

    @Transactional
    public PropostaSeguroResponse update(Long id, PropostaSeguroRequest req, String user) {
        if (req.vigenciaFim().isBefore(req.vigenciaInicio())) {
            throw new BusinessException("Data final de vigência deve ser posterior à data inicial");
        }
        PropostaSeguro e = findOrThrow(id);
        applyFields(e, req);
        e.setUpdatedBy(user);
        return toResponse(repository.save(e));
    }

    @Transactional
    public void delete(Long id, String user) {
        PropostaSeguro e = findOrThrow(id);
        e.softDelete();
        e.setUpdatedBy(user);
        repository.save(e);
    }

    private PropostaSeguro findOrThrow(Long id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proposta de seguro não encontrada"));
    }

    private void applyFields(PropostaSeguro e, PropostaSeguroRequest r) {
        e.setEmpresaId(r.empresaId());
        e.setFilialId(r.filialId());
        e.setNumero(r.numero());
        e.setClienteId(r.clienteId());
        e.setSeguradoraId(r.seguradoraId());
        e.setCorretoraId(r.corretoraId());
        e.setRamo(r.ramo());
        e.setVigenciaInicio(r.vigenciaInicio());
        e.setVigenciaFim(r.vigenciaFim());
        e.setPremioLiquido(r.premioLiquido() != null ? r.premioLiquido() : BigDecimal.ZERO);
        e.setPremioTotal(r.premioTotal() != null ? r.premioTotal() : BigDecimal.ZERO);
        e.setPercentualComissao(r.percentualComissao() != null ? r.percentualComissao() : BigDecimal.ZERO);
        e.setResponsavel(r.responsavel());
        e.setObservacao(r.observacao());
        if (r.status() != null && !r.status().isBlank()) e.setStatus(r.status());
    }

    private PropostaSeguroResponse toResponse(PropostaSeguro e) {
        String clienteNome = clienteRepository.findById(e.getClienteId())
                .map(Cliente::getRazaoSocial).orElse(null);
        String seguradoraNome = seguradoraRepository.findByIdAndDeletedFalse(e.getSeguradoraId())
                .map(Seguradora::getNome).orElse(null);
        String corretoraNome = e.getCorretoraId() != null
                ? corretoraRepository.findByIdAndDeletedFalse(e.getCorretoraId()).map(Corretora::getNome).orElse(null)
                : null;

        return new PropostaSeguroResponse(
                e.getId(), e.getEmpresaId(), e.getFilialId(), e.getNumero(),
                e.getClienteId(), clienteNome,
                e.getSeguradoraId(), seguradoraNome,
                e.getCorretoraId(), corretoraNome,
                e.getRamo(), e.getVigenciaInicio(), e.getVigenciaFim(),
                e.getPremioLiquido(), e.getPremioTotal(), e.getPercentualComissao(),
                e.getResponsavel(), e.getObservacao(), e.getStatus()
        );
    }
}
