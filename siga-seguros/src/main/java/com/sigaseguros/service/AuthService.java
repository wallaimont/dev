package com.sigaseguros.service;

import com.sigaseguros.dto.*;
import com.sigaseguros.entity.Usuario;
import com.sigaseguros.exception.BusinessException;
import com.sigaseguros.exception.ResourceNotFoundException;
import com.sigaseguros.repository.UsuarioRepository;
import com.sigaseguros.security.JwtTokenProvider;
import com.sigaseguros.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmailAndActiveTrue(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Credenciais inválidas"));

        if (usuario.getBloqueadoAte() != null && usuario.getBloqueadoAte().isAfter(LocalDateTime.now())) {
            throw new BusinessException("Conta bloqueada temporariamente. Tente novamente mais tarde.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha()));

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

            // Reset tentativas
            usuario.setTentativasLogin(0);
            usuario.setBloqueadoAte(null);
            usuario.setUltimoAcesso(LocalDateTime.now());
            usuarioRepository.save(usuario);

            String token = tokenProvider.generateToken(userPrincipal);
            String refreshToken = tokenProvider.generateRefreshToken(userPrincipal);

            auditoriaService.registrar("Usuario", usuario.getId(), "LOGIN");

            return LoginResponse.builder()
                    .token(token)
                    .refreshToken(refreshToken)
                    .nome(userPrincipal.getNome())
                    .email(userPrincipal.getEmail())
                    .perfil(userPrincipal.getPerfil().name())
                    .expiresIn(tokenProvider.getJwtExpirationMs())
                    .build();

        } catch (BadCredentialsException e) {
            int tentativas = (usuario.getTentativasLogin() != null ? usuario.getTentativasLogin() : 0) + 1;
            usuario.setTentativasLogin(tentativas);
            if (tentativas >= 5) {
                usuario.setBloqueadoAte(LocalDateTime.now().plusMinutes(30));
            }
            usuarioRepository.save(usuario);
            throw e;
        }
    }

    @Transactional
    public void alterarSenha(String email, AlterarSenhaDTO dto) {
        Usuario usuario = usuarioRepository.findByEmailAndActiveTrue(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (!passwordEncoder.matches(dto.getSenhaAtual(), usuario.getSenha())) {
            throw new BusinessException("Senha atual incorreta");
        }

        usuario.setSenha(passwordEncoder.encode(dto.getNovaSenha()));
        usuario.setSenhaTemporaria(false);
        usuarioRepository.save(usuario);

        auditoriaService.registrar("Usuario", usuario.getId(), "ALTERACAO_SENHA");
    }

    public LoginResponse refreshToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new BusinessException("Refresh token inválido ou expirado");
        }

        String email = tokenProvider.getEmailFromToken(refreshToken);
        Usuario usuario = usuarioRepository.findByEmailAndActiveTrue(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        UserPrincipal userPrincipal = new UserPrincipal(usuario);
        String newToken = tokenProvider.generateToken(userPrincipal);
        String newRefreshToken = tokenProvider.generateRefreshToken(userPrincipal);

        return LoginResponse.builder()
                .token(newToken)
                .refreshToken(newRefreshToken)
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .perfil(usuario.getPerfil().name())
                .expiresIn(tokenProvider.getJwtExpirationMs())
                .build();
    }
}
