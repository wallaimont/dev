package com.orionerp.modules.seguros.service;

import com.orionerp.common.PageResponse;
import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.cadastros.domain.Cliente;
import com.orionerp.modules.cadastros.repository.ClienteRepository;
import com.orionerp.modules.seguros.domain.Apolice;
import com.orionerp.modules.seguros.domain.Corretora;
import com.orionerp.modules.seguros.domain.PropostaSeguro;
import com.orionerp.modules.seguros.domain.Seguradora;
import com.orionerp.modules.seguros.dto.ApoliceRequest;
import com.orionerp.modules.seguros.dto.ApoliceResponse;
import com.orionerp.modules.seguros.repository.ApoliceRepository;
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
public class ApoliceService {

    private final ApoliceRepository repository;
    private final PropostaSeguroRepository propostaRepository;
    private final ClienteRepository clienteRepository;
    private final SeguradoraRepository seguradoraRepository;
    private final CorretoraRepository corretoraRepository;

    @Transactional(readOnly = true)
    public PageResponse<ApoliceResponse> list(Long empresaId, Long clienteId, Long seguradoraId, String status, String search, int page, int size) {
        Page<Apolice> pg = repository.findAll(
                SegurosSpecifications.apoliceFilter(empresaId, clienteId, seguradoraId, status, search),
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
        return PageResponse.<ApoliceResponse>builder()
                .items(pg.getContent().stream().map(this::toResponse).toList())
                .page(pg.getNumber()).size(pg.getSize())
                .totalElements(pg.getTotalElements()).totalPages(pg.getTotalPages())
                .first(pg.isFirst()).last(pg.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public ApoliceResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public ApoliceResponse create(ApoliceRequest req, String user) {
        if (req.vigenciaFim().isBefore(req.vigenciaInicio())) {
            throw new BusinessException("Data final de vigência deve ser posterior à data inicial");
        }
        Apolice e = new Apolice();
        applyFields(e, req);
        e.setCreatedBy(user);
        return toResponse(repository.save(e));
    }

    @Transactional
    public ApoliceResponse update(Long id, ApoliceRequest req, String user) {
        if (req.vigenciaFim().isBefore(req.vigenciaInicio())) {
            throw new BusinessException("Data final de vigência deve ser posterior à data inicial");
        }
        Apolice e = findOrThrow(id);
        applyFields(e, req);
        e.setUpdatedBy(user);
        return toResponse(repository.save(e));
    }

    @Transactional
    public void delete(Long id, String user) {
        Apolice e = findOrThrow(id);
        e.softDelete();
        e.setUpdatedBy(user);
        repository.save(e);
    }

    private Apolice findOrThrow(Long id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Apólice não encontrada"));
    }

    private void applyFields(Apolice e, ApoliceRequest r) {
        e.setEmpresaId(r.empresaId());
        e.setFilialId(r.filialId());
        e.setNumero(r.numero());
        e.setPropostaId(r.propostaId());
        e.setClienteId(r.clienteId());
        e.setSeguradoraId(r.seguradoraId());
        e.setCorretoraId(r.corretoraId());
        e.setRamo(r.ramo());
        e.setVigenciaInicio(r.vigenciaInicio());
        e.setVigenciaFim(r.vigenciaFim());
        e.setPremioTotal(r.premioTotal() != null ? r.premioTotal() : BigDecimal.ZERO);
        e.setImportanciaSegurada(r.importanciaSegurada() != null ? r.importanciaSegurada() : BigDecimal.ZERO);
        e.setFranquia(r.franquia() != null ? r.franquia() : BigDecimal.ZERO);
        e.setPercentualComissao(r.percentualComissao() != null ? r.percentualComissao() : BigDecimal.ZERO);
        e.setCertificadoInclusao(r.certificadoInclusao());
        e.setObservacao(r.observacao());
        if (r.status() != null && !r.status().isBlank()) e.setStatus(r.status());
    }

    private ApoliceResponse toResponse(Apolice e) {
        String propostaNumero = e.getPropostaId() != null
                ? propostaRepository.findByIdAndDeletedFalse(e.getPropostaId()).map(PropostaSeguro::getNumero).orElse(null)
                : null;
        String clienteNome = clienteRepository.findById(e.getClienteId())
                .map(Cliente::getRazaoSocial).orElse(null);
        String seguradoraNome = seguradoraRepository.findByIdAndDeletedFalse(e.getSeguradoraId())
                .map(Seguradora::getNome).orElse(null);
        String corretoraNome = e.getCorretoraId() != null
                ? corretoraRepository.findByIdAndDeletedFalse(e.getCorretoraId()).map(Corretora::getNome).orElse(null)
                : null;

        return new ApoliceResponse(
                e.getId(), e.getEmpresaId(), e.getFilialId(), e.getNumero(),
                e.getPropostaId(), propostaNumero,
                e.getClienteId(), clienteNome,
                e.getSeguradoraId(), seguradoraNome,
                e.getCorretoraId(), corretoraNome,
                e.getRamo(), e.getVigenciaInicio(), e.getVigenciaFim(),
                e.getPremioTotal(), e.getImportanciaSegurada(), e.getFranquia(),
                e.getPercentualComissao(), e.getCertificadoInclusao(),
                e.getObservacao(), e.getStatus()
        );
    }
}
