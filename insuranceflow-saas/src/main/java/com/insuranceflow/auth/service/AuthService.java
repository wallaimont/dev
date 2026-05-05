package com.insuranceflow.auth.service;

import com.insuranceflow.auth.dto.*;
import com.insuranceflow.auth.model.*;
import com.insuranceflow.auth.repository.UsuarioRepository;
import com.insuranceflow.common.exception.BusinessException;
import com.insuranceflow.master.model.Empresa;
import com.insuranceflow.master.repository.EmpresaRepository;
import com.insuranceflow.security.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager;
    private final JwtTokenProvider tokenProvider;
    private final UsuarioRepository usuarioRepo;
    private final EmpresaRepository empresaRepo;
    private final SecurityAuditService securityAuditService;
    private final TokenBlacklistService tokenBlacklistService;

    @Value("${app.security.lockout.max-attempts:5}")
    private int maxAttempts;

    @Value("${app.security.lockout.lock-duration-minutes:30}")
    private int lockDurationMinutes;

    @Transactional
    public LoginResponse login(LoginRequest request, String ipAddress, String userAgent) {
        var usuarioOpt = usuarioRepo.findByEmailAndActiveTrue(request.getEmail());

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (usuario.isLocked()) {
                securityAuditService.logEvent("LOGIN_BLOCKED_LOCKED", null, request.getEmail(),
                    null, ipAddress, userAgent, "Conta bloqueada até " + usuario.getLockedUntil(), false);
                throw new BusinessException("Conta bloqueada temporariamente. Tente novamente após " +
                    usuario.getLockedUntil().toString().replace("T", " "), HttpStatus.FORBIDDEN);
            }
        }

        try {
            Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha()));

            UserPrincipal principal = (UserPrincipal) auth.getPrincipal();

            if (principal.getEmpresaId() != null) {
                Empresa empresa = empresaRepo.findById(principal.getEmpresaId()).orElse(null);
                if (empresa != null && Boolean.TRUE.equals(empresa.getBloqueada())) {
                    securityAuditService.logEvent("LOGIN_BLOCKED_EMPRESA", principal.getId(),
                        principal.getEmail(), principal.getEmpresaId(), ipAddress, userAgent,
                        "Empresa bloqueada", false);
                    throw new BusinessException("Sua empresa está bloqueada. Entre em contato com o suporte.");
                }
            }

            // Login bem-sucedido: resetar tentativas
            usuarioOpt.ifPresent(u -> {
                u.setLoginAttempts(0);
                u.setLockedUntil(null);
                u.setUltimoAcesso(LocalDateTime.now());
                usuarioRepo.save(u);
            });

            String token = tokenProvider.generateToken(principal);
            String refreshToken = tokenProvider.generateRefreshToken(principal);

            securityAuditService.logEvent("LOGIN_SUCCESS", principal.getId(),
                principal.getEmail(), principal.getEmpresaId(), ipAddress, userAgent, null, true);

            String empresaNome = null;
            if (principal.getEmpresaId() != null) {
                empresaNome = empresaRepo.findById(principal.getEmpresaId())
                    .map(Empresa::getNomeFantasia).orElse(null);
            }

            return LoginResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .tipo("Bearer")
                .nome(principal.getNome())
                .email(principal.getEmail())
                .perfil(principal.getRole())
                .empresaId(principal.getEmpresaId())
                .empresaNome(empresaNome)
                .build();

        } catch (AuthenticationException ex) {
            // Login falhou: incrementar tentativas
            usuarioOpt.ifPresent(u -> {
                Integer currentAttempts = u.getLoginAttempts();
                int attempts = (currentAttempts != null ? currentAttempts.intValue() : 0) + 1;
                u.setLoginAttempts(attempts);
                if (attempts >= maxAttempts) {
                    u.setLockedUntil(LocalDateTime.now().plusMinutes(lockDurationMinutes));
                    log.warn("Conta bloqueada por {} minutos: {}", lockDurationMinutes, u.getEmail());
                }
                usuarioRepo.save(u);
            });

            securityAuditService.logEvent("LOGIN_FAILED", null, request.getEmail(),
                null, ipAddress, userAgent, "Credenciais inválidas", false);

            throw new BadCredentialsException("Credenciais inválidas");
        }
    }

    @Transactional
    public LoginResponse refreshToken(String refreshToken, String ipAddress, String userAgent) {
        if (refreshToken == null || !tokenProvider.validateToken(refreshToken)) {
            throw new BusinessException("Refresh token inválido", HttpStatus.UNAUTHORIZED);
        }

        if (tokenBlacklistService.isBlacklisted(refreshToken)) {
            securityAuditService.logEvent("REFRESH_BLACKLISTED", null, null,
                null, ipAddress, userAgent, "Tentativa de usar refresh token revogado", false);
            throw new BusinessException("Token revogado", HttpStatus.UNAUTHORIZED);
        }

        var userId = tokenProvider.getUserIdFromToken(refreshToken);
        var usuario = usuarioRepo.findByIdAndActiveTrue(userId)
            .orElseThrow(() -> new BusinessException("Usuário não encontrado", HttpStatus.UNAUTHORIZED));

        UserPrincipal principal = UserPrincipal.builder()
            .id(usuario.getId())
            .email(usuario.getEmail())
            .nome(usuario.getNome())
            .role(usuario.getPerfil().name())
            .empresaId(usuario.getEmpresaId())
            .active(usuario.getActive())
            .build();

        // Revogar refresh token antigo (rotation)
        tokenBlacklistService.blacklist(refreshToken, userId, "REFRESH", "Token rotacionado");

        String token = tokenProvider.generateToken(principal);
        String newRefresh = tokenProvider.generateRefreshToken(principal);

        String empresaNome = null;
        if (principal.getEmpresaId() != null) {
            empresaNome = empresaRepo.findById(principal.getEmpresaId())
                .map(Empresa::getNomeFantasia).orElse(null);
        }

        return LoginResponse.builder()
            .token(token)
            .refreshToken(newRefresh)
            .tipo("Bearer")
            .nome(principal.getNome())
            .email(principal.getEmail())
            .perfil(principal.getRole())
            .empresaId(principal.getEmpresaId())
            .empresaNome(empresaNome)
            .build();
    }

    @Transactional
    public void logout(String accessToken, String refreshToken, UserPrincipal principal,
                       String ipAddress, String userAgent) {
        if (accessToken != null) {
            tokenBlacklistService.blacklist(accessToken, principal.getId(), "ACCESS", "Logout");
        }
        if (refreshToken != null) {
            tokenBlacklistService.blacklist(refreshToken, principal.getId(), "REFRESH", "Logout");
        }
        securityAuditService.logEvent("LOGOUT", principal.getId(), principal.getEmail(),
            principal.getEmpresaId(), ipAddress, userAgent, null, true);
    }
}
