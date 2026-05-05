package com.orionerp.security;

import com.orionerp.exception.UnauthorizedException;
import com.orionerp.modules.administration.domain.Usuario;
import com.orionerp.modules.administration.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        Usuario usuario = usuarioRepository.findByEmailAndDeletedFalse(username)
                .orElseThrow(() -> new UnauthorizedException("Usuario ou senha invalidos"));

        return UserPrincipal.from(usuario);
    }
}
