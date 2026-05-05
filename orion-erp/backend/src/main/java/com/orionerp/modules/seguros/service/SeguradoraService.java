package com.orionerp.modules.seguros.service;

import com.orionerp.common.PageResponse;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.seguros.domain.Seguradora;
import com.orionerp.modules.seguros.dto.SeguradoraRequest;
import com.orionerp.modules.seguros.dto.SeguradoraResponse;
import com.orionerp.modules.seguros.repository.SeguradoraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SeguradoraService {

    private final SeguradoraRepository repository;

    @Transactional(readOnly = true)
    public PageResponse<SeguradoraResponse> list(Long empresaId, String search, int page, int size) {
        Page<Seguradora> pg = repository.findAll(
                SegurosSpecifications.seguradoraFilter(empresaId, search),
                PageRequest.of(page, size, Sort.by("nome"))
        );
        return PageResponse.<SeguradoraResponse>builder()
                .items(pg.getContent().stream().map(this::toResponse).toList())
                .page(pg.getNumber()).size(pg.getSize())
                .totalElements(pg.getTotalElements()).totalPages(pg.getTotalPages())
                .first(pg.isFirst()).last(pg.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public SeguradoraResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public SeguradoraResponse create(SeguradoraRequest req, String user) {
        Seguradora e = new Seguradora();
        applyFields(e, req);
        e.setCreatedBy(user);
        return toResponse(repository.save(e));
    }

    @Transactional
    public SeguradoraResponse update(Long id, SeguradoraRequest req, String user) {
        Seguradora e = findOrThrow(id);
        applyFields(e, req);
        e.setUpdatedBy(user);
        return toResponse(repository.save(e));
    }

    @Transactional
    public void delete(Long id, String user) {
        Seguradora e = findOrThrow(id);
        e.softDelete();
        e.setUpdatedBy(user);
        repository.save(e);
    }

    private Seguradora findOrThrow(Long id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seguradora não encontrada"));
    }

    private void applyFields(Seguradora e, SeguradoraRequest r) {
        e.setEmpresaId(r.empresaId());
        e.setCodigo(r.codigo());
        e.setNome(r.nome());
        e.setCnpj(r.cnpj());
        e.setRegistroSusep(r.registroSusep());
        e.setEmail(r.email());
        e.setTelefone(r.telefone());
        e.setEndereco(r.endereco());
        e.setCidade(r.cidade());
        e.setUf(r.uf());
        e.setCep(r.cep());
        e.setContato(r.contato());
        e.setObservacao(r.observacao());
        if (r.ativo() != null) e.setAtivo(r.ativo());
    }

    private SeguradoraResponse toResponse(Seguradora e) {
        return new SeguradoraResponse(
                e.getId(), e.getEmpresaId(), e.getCodigo(), e.getNome(), e.getCnpj(),
                e.getRegistroSusep(), e.getEmail(), e.getTelefone(), e.getEndereco(),
                e.getCidade(), e.getUf(), e.getCep(), e.getContato(), e.getObservacao(), e.getAtivo()
        );
    }
}
