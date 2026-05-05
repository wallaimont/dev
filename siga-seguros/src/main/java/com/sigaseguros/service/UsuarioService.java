package com.sigaseguros.service;

import com.sigaseguros.dto.UsuarioDTO;
import com.sigaseguros.entity.Usuario;
import com.sigaseguros.enums.Perfil;
import com.sigaseguros.exception.BusinessException;
import com.sigaseguros.exception.ResourceNotFoundException;
import com.sigaseguros.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Transactional(readOnly = true)
    public Page<UsuarioDTO> listar(String nome, String email, Perfil perfil, Pageable pageable) {
        return usuarioRepository.findAllWithFilters(nome, email, perfil, pageable)
                .map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public UsuarioDTO buscarPorId(Long id) {
        return toDTO(findById(id));
    }

    @Transactional
    public UsuarioDTO criar(UsuarioDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("E-mail já cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha() != null ? dto.getSenha() : "mudar123"));
        usuario.setPerfil(dto.getPerfil());
        usuario.setActive(true);
        usuario.setSenhaTemporaria(true);

        usuario = usuarioRepository.save(usuario);
        auditoriaService.registrar("Usuario", usuario.getId(), "CRIAR");
        return toDTO(usuario);
    }

    @Transactional
    public UsuarioDTO atualizar(Long id, UsuarioDTO dto) {
        Usuario usuario = findById(id);

        if (!usuario.getEmail().equals(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("E-mail já cadastrado");
        }

        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setPerfil(dto.getPerfil());

        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        }

        usuario = usuarioRepository.save(usuario);
        auditoriaService.registrar("Usuario", usuario.getId(), "ATUALIZAR");
        return toDTO(usuario);
    }

    @Transactional
    public void inativar(Long id) {
        Usuario usuario = findById(id);
        usuario.setActive(false);
        usuarioRepository.save(usuario);
        auditoriaService.registrar("Usuario", id, "INATIVAR");
    }

    @Transactional
    public void ativar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        usuario.setActive(true);
        usuario.setTentativasLogin(0);
        usuario.setBloqueadoAte(null);
        usuarioRepository.save(usuario);
        auditoriaService.registrar("Usuario", id, "ATIVAR");
    }

    @Transactional
    public void resetarSenha(Long id) {
        Usuario usuario = findById(id);
        usuario.setSenha(passwordEncoder.encode("mudar123"));
        usuario.setSenhaTemporaria(true);
        usuario.setTentativasLogin(0);
        usuario.setBloqueadoAte(null);
        usuarioRepository.save(usuario);
        auditoriaService.registrar("Usuario", id, "RESET_SENHA");
    }

    private Usuario findById(Long id) {
        return usuarioRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

    private UsuarioDTO toDTO(Usuario u) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(u.getId());
        dto.setNome(u.getNome());
        dto.setEmail(u.getEmail());
        dto.setPerfil(u.getPerfil());
        dto.setActive(u.getActive());
        dto.setUltimoAcesso(u.getUltimoAcesso() != null ? u.getUltimoAcesso().format(FMT) : null);
        dto.setCreatedAt(u.getCreatedAt() != null ? u.getCreatedAt().format(FMT) : null);
        return dto;
    }
}
