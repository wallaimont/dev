package com.orionerp.modules.administration.service;

import com.orionerp.common.PageResponse;
import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.administration.domain.Perfil;
import com.orionerp.modules.administration.domain.Usuario;
import com.orionerp.modules.administration.dto.UsuarioRequest;
import com.orionerp.modules.administration.dto.UsuarioResponse;
import com.orionerp.modules.administration.repository.PerfilRepository;
import com.orionerp.modules.administration.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public PageResponse<UsuarioResponse> list(Long empresaId, Long filialId, String term, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "nome"));
        var result = usuarioRepository.findAll(AdministrationSpecifications.usuarioFilter(empresaId, filialId, term), pageable)
                .map(this::toResponse);
        return PageResponse.from(result);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public UsuarioResponse create(UsuarioRequest request) {
        if (request.senha() == null || request.senha().isBlank()) {
            throw new BusinessException("Senha obrigatoria para criar usuario");
        }
        validateEmail(null, request.email());

        Perfil perfil = findPerfil(request.perfilId());

        Usuario usuario = new Usuario();
        apply(usuario, request, perfil, true);
        return toResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponse update(Long id, UsuarioRequest request) {
        Usuario usuario = findById(id);
        validateEmail(id, request.email());

        Perfil perfil = findPerfil(request.perfilId());

        apply(usuario, request, perfil, false);
        return toResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public void resetPassword(Long id, String novaSenha) {
        if (novaSenha == null || novaSenha.length() < 8) {
            throw new BusinessException("Senha deve ter no minimo 8 caracteres");
        }

        Usuario usuario = findById(id);
        usuario.setSenhaHash(passwordEncoder.encode(novaSenha));
        usuario.setTrocarSenha(false);
        usuario.setTentativasLogin(0);
        usuario.setBloqueado(false);
        usuario.setBloqueadoAte(null);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void delete(Long id) {
        Usuario usuario = findById(id);
        usuario.softDelete();
        usuarioRepository.save(usuario);
    }

    private Usuario findById(Long id) {
        return usuarioRepository.findById(id)
                .filter(u -> !Boolean.TRUE.equals(u.getDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado"));
    }

    private Perfil findPerfil(Long perfilId) {
        return perfilRepository.findByIdAndDeletedFalse(perfilId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil nao encontrado"));
    }

    private void validateEmail(Long id, String email) {
        String normalized = email.trim().toLowerCase();
        boolean exists = id == null
                ? usuarioRepository.existsByEmailIgnoreCaseAndDeletedFalse(normalized)
                : usuarioRepository.existsByEmailIgnoreCaseAndDeletedFalseAndIdNot(normalized, id);
        if (exists) {
            throw new BusinessException("Ja existe usuario com o email informado");
        }
    }

    private void apply(Usuario usuario, UsuarioRequest request, Perfil perfil, boolean creating) {
        usuario.setEmpresaId(request.empresaId());
        usuario.setFilialId(request.filialId());
        usuario.setPerfil(perfil);
        usuario.setNome(request.nome().trim());
        usuario.setEmail(request.email().trim().toLowerCase());
        usuario.setTelefone(request.telefone());
        usuario.setTrocarSenha(Boolean.TRUE.equals(request.trocarSenha()));
        if (request.ativo() != null) {
            usuario.setAtivo(request.ativo());
        }

        if (request.senha() != null && !request.senha().isBlank()) {
            if (request.senha().length() < 8) {
                throw new BusinessException("Senha deve ter no minimo 8 caracteres");
            }
            usuario.setSenhaHash(passwordEncoder.encode(request.senha()));
            usuario.setTrocarSenha(Boolean.TRUE.equals(request.trocarSenha()));
        } else if (creating) {
            throw new BusinessException("Senha obrigatoria para criar usuario");
        }

        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            usuario.setBloqueado(false);
            usuario.setBloqueadoAte(null);
            usuario.setTentativasLogin(0);
        }
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getUuid(),
                usuario.getEmpresaId(),
                usuario.getFilialId(),
                usuario.getPerfil().getId(),
                usuario.getPerfil().getNome(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getTrocarSenha(),
                usuario.getBloqueado(),
                usuario.getBloqueadoAte(),
                usuario.getUltimoLogin(),
                usuario.getAtivo()
        );
    }
}
