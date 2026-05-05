package com.insuranceflow.security;

import com.insuranceflow.auth.model.Usuario;
import com.insuranceflow.auth.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmailAndActiveTrue(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
        return mapToUserPrincipal(usuario);
    }

    public UserDetails loadUserById(UUID id) {
        Usuario usuario = usuarioRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com id: " + id));
        return mapToUserPrincipal(usuario);
    }

    private UserPrincipal mapToUserPrincipal(Usuario usuario) {
        return UserPrincipal.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .password(usuario.getSenha())
                .nome(usuario.getNome())
                .role(usuario.getPerfil().name())
                .empresaId(usuario.getEmpresaId())
                .active(usuario.getActive())
                .build();
    }
}
