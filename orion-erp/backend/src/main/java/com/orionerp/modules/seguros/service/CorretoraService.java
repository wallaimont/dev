package com.orionerp.modules.seguros.service;

import com.orionerp.common.PageResponse;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.seguros.domain.Corretora;
import com.orionerp.modules.seguros.dto.CorretoraRequest;
import com.orionerp.modules.seguros.dto.CorretoraResponse;
import com.orionerp.modules.seguros.repository.CorretoraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CorretoraService {

    private final CorretoraRepository repository;

    @Transactional(readOnly = true)
    public PageResponse<CorretoraResponse> list(Long empresaId, String search, int page, int size) {
        Page<Corretora> pg = repository.findAll(
                SegurosSpecifications.corretoraFilter(empresaId, search),
                PageRequest.of(page, size, Sort.by("nome"))
        );
        return PageResponse.<CorretoraResponse>builder()
                .items(pg.getContent().stream().map(this::toResponse).toList())
                .page(pg.getNumber()).size(pg.getSize())
                .totalElements(pg.getTotalElements()).totalPages(pg.getTotalPages())
                .first(pg.isFirst()).last(pg.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public CorretoraResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public CorretoraResponse create(CorretoraRequest req, String user) {
        Corretora e = new Corretora();
        applyFields(e, req);
        e.setCreatedBy(user);
        return toResponse(repository.save(e));
    }

    @Transactional
    public CorretoraResponse update(Long id, CorretoraRequest req, String user) {
        Corretora e = findOrThrow(id);
        applyFields(e, req);
        e.setUpdatedBy(user);
        return toResponse(repository.save(e));
    }

    @Transactional
    public void delete(Long id, String user) {
        Corretora e = findOrThrow(id);
        e.softDelete();
        e.setUpdatedBy(user);
        repository.save(e);
    }

    private Corretora findOrThrow(Long id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Corretora não encontrada"));
    }

    private void applyFields(Corretora e, CorretoraRequest r) {
        e.setEmpresaId(r.empresaId());
        e.setCodigo(r.codigo());
        e.setNome(r.nome());
        e.setCnpj(r.cnpj());
        e.setResponsavel(r.responsavel());
        e.setEmail(r.email());
        e.setTelefone(r.telefone());
        e.setEndereco(r.endereco());
        e.setCidade(r.cidade());
        e.setUf(r.uf());
        e.setCep(r.cep());
        e.setPercentualComissao(r.percentualComissao() != null ? r.percentualComissao() : BigDecimal.ZERO);
        e.setObservacao(r.observacao());
        if (r.ativo() != null) e.setAtivo(r.ativo());
    }

    private CorretoraResponse toResponse(Corretora e) {
        return new CorretoraResponse(
                e.getId(), e.getEmpresaId(), e.getCodigo(), e.getNome(), e.getCnpj(),
                e.getResponsavel(), e.getEmail(), e.getTelefone(), e.getEndereco(),
                e.getCidade(), e.getUf(), e.getCep(), e.getPercentualComissao(),
                e.getObservacao(), e.getAtivo()
        );
    }
}
