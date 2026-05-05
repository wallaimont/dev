package com.orionerp.modules.administration.service;

import com.orionerp.common.PageResponse;
import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.administration.domain.Perfil;
import com.orionerp.modules.administration.domain.Permissao;
import com.orionerp.modules.administration.dto.PerfilRequest;
import com.orionerp.modules.administration.dto.PerfilResponse;
import com.orionerp.modules.administration.dto.PermissaoResponse;
import com.orionerp.modules.administration.repository.PerfilRepository;
import com.orionerp.modules.administration.repository.PermissaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerfilService {

    private final PerfilRepository perfilRepository;
    private final PermissaoRepository permissaoRepository;

    @Transactional(readOnly = true)
    public PageResponse<PerfilResponse> list(Long empresaId, String term, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "nome"));
        var result = perfilRepository.findAll(AdministrationSpecifications.perfilFilter(empresaId, term), pageable)
                .map(this::toResponse);
        return PageResponse.from(result);
    }

    @Transactional(readOnly = true)
    public PerfilResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public PerfilResponse create(PerfilRequest request) {
        validateUnique(null, request.empresaId(), request.codigo());

        Perfil perfil = new Perfil();
        apply(perfil, request);
        return toResponse(perfilRepository.save(perfil));
    }

    @Transactional
    public PerfilResponse update(Long id, PerfilRequest request) {
        Perfil perfil = findById(id);
        validateUnique(id, request.empresaId(), request.codigo());

        apply(perfil, request);
        return toResponse(perfilRepository.save(perfil));
    }

    @Transactional
    public void delete(Long id) {
        Perfil perfil = findById(id);
        perfil.softDelete();
        perfilRepository.save(perfil);
    }

    private void validateUnique(Long id, Long empresaId, String codigo) {
        boolean exists = id == null
                ? perfilRepository.existsByEmpresaIdAndCodigoIgnoreCaseAndDeletedFalse(empresaId, codigo)
                : perfilRepository.existsByEmpresaIdAndCodigoIgnoreCaseAndDeletedFalseAndIdNot(empresaId, codigo, id);
        if (exists) {
            throw new BusinessException("Ja existe perfil com o codigo informado");
        }
    }

    private Perfil findById(Long id) {
        return perfilRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil nao encontrado"));
    }

    private void apply(Perfil perfil, PerfilRequest request) {
        perfil.setEmpresaId(request.empresaId());
        perfil.setCodigo(request.codigo().trim());
        perfil.setNome(request.nome().trim());
        perfil.setDescricao(request.descricao());
        perfil.setAdmin(Boolean.TRUE.equals(request.admin()));
        if (request.ativo() != null) {
            perfil.setAtivo(request.ativo());
        }

        List<Long> permissaoIds = request.permissaoIds() != null ? request.permissaoIds() : List.of();
        Set<Permissao> permissoes = permissaoRepository.findByIdIn(permissaoIds).stream().collect(Collectors.toSet());
        perfil.setPermissoes(permissoes);
    }

    private PerfilResponse toResponse(Perfil perfil) {
        List<PermissaoResponse> permissoes = new ArrayList<>(perfil.getPermissoes()).stream()
                .map(p -> new PermissaoResponse(
                        p.getId(),
                        p.getUuid(),
                        p.getModulo(),
                        p.getRecurso(),
                        p.getAcao(),
                        p.getDescricao()))
                .sorted((a, b) -> (a.modulo() + a.recurso() + a.acao()).compareToIgnoreCase(b.modulo() + b.recurso() + b.acao()))
                .toList();

        return new PerfilResponse(
                perfil.getId(),
                perfil.getUuid(),
                perfil.getEmpresaId(),
                perfil.getCodigo(),
                perfil.getNome(),
                perfil.getDescricao(),
                perfil.getAdmin(),
                perfil.getAtivo(),
                permissoes
        );
    }
}
