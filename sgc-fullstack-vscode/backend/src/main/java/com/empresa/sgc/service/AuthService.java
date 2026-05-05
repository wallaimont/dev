package com.empresa.sgc.service;

import com.empresa.sgc.dto.AuthResponse;
import com.empresa.sgc.dto.LoginRequest;
import com.empresa.sgc.dto.RegisterRequest;
import com.empresa.sgc.entity.Perfil;
import com.empresa.sgc.entity.Usuario;
import com.empresa.sgc.exception.BusinessException;
import com.empresa.sgc.repository.UsuarioRepository;
import com.empresa.sgc.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Já existe um usuário com esse e-mail");
        }

        Usuario usuario = Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .perfil(request.getPerfil() != null ? request.getPerfil() : Perfil.ADMIN)
                .ativo(true)
                .build();

        usuarioRepository.save(usuario);
        String token = jwtService.generateToken(usuario.getEmail());

        return new AuthResponse(token, usuario.getNome(), usuario.getEmail(), usuario.getPerfil().name());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
        );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        String token = jwtService.generateToken(usuario.getEmail());
        return new AuthResponse(token, usuario.getNome(), usuario.getEmail(), usuario.getPerfil().name());
    }
}
