package com.orionerp.modules.administration.service;

import com.orionerp.audit.LoginAuditService;
import com.orionerp.exception.UnauthorizedException;
import com.orionerp.modules.administration.domain.Usuario;
import com.orionerp.modules.administration.dto.AuthResponse;
import com.orionerp.modules.administration.dto.AuthUserDto;
import com.orionerp.modules.administration.dto.LoginRequest;
import com.orionerp.modules.administration.dto.RefreshTokenRequest;
import com.orionerp.modules.administration.repository.UsuarioRepository;
import com.orionerp.security.JwtProperties;
import com.orionerp.security.JwtTokenProvider;
import com.orionerp.security.RefreshToken;
import com.orionerp.security.RefreshTokenRepository;
import com.orionerp.security.SecurityPolicyProperties;
import com.orionerp.security.SecurityUtils;
import com.orionerp.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final SecurityPolicyProperties securityPolicyProperties;
    private final RefreshTokenRepository refreshTokenRepository;
    private final LoginAuditService loginAuditService;

    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        String email = request.email().trim().toLowerCase();
        Usuario usuario = usuarioRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> {
                    loginAuditService.failed(email, "Usuario ou senha invalidos", httpRequest);
                    return new UnauthorizedException("Usuario ou senha invalidos");
                });

        validateUserState(usuario, email, httpRequest);

        if (!passwordEncoder.matches(request.senha(), usuario.getSenhaHash())) {
            processFailedAttempt(usuario, email, httpRequest);
            throw new UnauthorizedException("Usuario ou senha invalidos");
        }

        usuario.setTentativasLogin(0);
        usuario.setBloqueado(false);
        usuario.setBloqueadoAte(null);
        usuario.setUltimoLogin(LocalDateTime.now());
        usuarioRepository.save(usuario);

        refreshTokenRepository.deleteByUsuario_Id(usuario.getId());
        RefreshToken refreshToken = createRefreshToken(usuario);

        loginAuditService.success(usuario.getId(), usuario.getEmail(), httpRequest);

        UserPrincipal principal = UserPrincipal.from(usuario);
        String accessToken = jwtTokenProvider.generateAccessToken(principal);

        return new AuthResponse(
                "Bearer",
                accessToken,
                refreshToken.getToken(),
                jwtTokenProvider.getAccessTokenExpirySeconds(),
                toAuthUserDto(usuario)
        );
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenAndRevogadoFalse(request.refreshToken())
                .orElseThrow(() -> new UnauthorizedException("Refresh token invalido"));

        if (refreshToken.isExpired()) {
            refreshToken.setRevogado(true);
            refreshTokenRepository.save(refreshToken);
            throw new UnauthorizedException("Refresh token expirado");
        }

        Usuario usuario = refreshToken.getUsuario();
        if (Boolean.TRUE.equals(usuario.getDeleted()) || !Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new UnauthorizedException("Usuario inativo");
        }

        refreshToken.setRevogado(true);
        refreshTokenRepository.save(refreshToken);

        RefreshToken newRefreshToken = createRefreshToken(usuario);
        String accessToken = jwtTokenProvider.generateAccessToken(UserPrincipal.from(usuario));

        return new AuthResponse(
                "Bearer",
                accessToken,
                newRefreshToken.getToken(),
                jwtTokenProvider.getAccessTokenExpirySeconds(),
                toAuthUserDto(usuario)
        );
    }

    @Transactional
    public void logout(RefreshTokenRequest request) {
        refreshTokenRepository.findByTokenAndRevogadoFalse(request.refreshToken())
                .ifPresent(token -> {
                    token.setRevogado(true);
                    refreshTokenRepository.save(token);
                });
    }

    @Transactional(readOnly = true)
    public AuthUserDto currentUser() {
        Long userId = SecurityUtils.currentUserId();
        if (userId == null) {
            throw new UnauthorizedException("Usuario nao autenticado");
        }

        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("Usuario nao encontrado"));

        return toAuthUserDto(usuario);
    }

    private void validateUserState(Usuario usuario, String email, HttpServletRequest httpRequest) {
        if (Boolean.TRUE.equals(usuario.getDeleted()) || !Boolean.TRUE.equals(usuario.getAtivo())) {
            loginAuditService.failed(email, "Usuario inativo", httpRequest);
            throw new UnauthorizedException("Usuario inativo");
        }

        if (usuario.isLockedNow()) {
            loginAuditService.failed(email, "Usuario bloqueado", httpRequest);
            throw new UnauthorizedException("Usuario bloqueado temporariamente");
        }
    }

    private void processFailedAttempt(Usuario usuario, String email, HttpServletRequest httpRequest) {
        int attempts = usuario.getTentativasLogin() + 1;
        usuario.setTentativasLogin(attempts);

        if (attempts >= securityPolicyProperties.getMaxLoginAttempts()) {
            usuario.setBloqueado(true);
            usuario.setBloqueadoAte(LocalDateTime.now().plusMinutes(securityPolicyProperties.getLockMinutes()));
        }

        usuarioRepository.save(usuario);
        loginAuditService.failed(email, "Usuario ou senha invalidos", httpRequest);
    }

    private RefreshToken createRefreshToken(Usuario usuario) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsuario(usuario);
        refreshToken.setToken(UUID.randomUUID() + "." + UUID.randomUUID());
        refreshToken.setExpiraEm(LocalDateTime.now().plusSeconds(jwtProperties.getRefreshExpirationMs() / 1000));

        return refreshTokenRepository.save(refreshToken);
    }

    private AuthUserDto toAuthUserDto(Usuario usuario) {
        List<String> permissoes = usuario.getPerfil().getPermissoes().stream()
                .map(p -> p.getRecurso() + ":" + p.getAcao())
                .sorted(Comparator.naturalOrder())
                .toList();

        return new AuthUserDto(
                usuario.getId(),
                usuario.getEmpresaId(),
                usuario.getFilialId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil().getNome(),
                permissoes
        );
    }
}
