package com.sigaseguros.controller;

import com.sigaseguros.dto.*;
import com.sigaseguros.enums.Perfil;
import com.sigaseguros.security.Permissions;
import com.sigaseguros.security.UserPrincipal;
import com.sigaseguros.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Realizar login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Login realizado com sucesso"));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar token")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(@RequestBody String refreshToken) {
        LoginResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/alterar-senha")
    @Operation(summary = "Alterar senha")
    public ResponseEntity<ApiResponse<Void>> alterarSenha(@Valid @RequestBody AlterarSenhaDTO dto,
                                                           Authentication authentication) {
        authService.alterarSenha(authentication.getName(), dto);
        return ResponseEntity.ok(ApiResponse.ok(null, "Senha alterada com sucesso"));
    }

    @GetMapping("/me")
    @Operation(summary = "Obter dados e permissões do usuário autenticado")
    public ResponseEntity<ApiResponse<UserInfoDTO>> me(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Perfil perfil = principal.getPerfil();
        UserInfoDTO info = UserInfoDTO.builder()
                .id(principal.getId())
                .nome(principal.getNome())
                .email(principal.getEmail())
                .perfil(perfil.name())
                .modules(Permissions.getModulesForPerfil(perfil))
                .permissions(Permissions.getPermissionMap(perfil))
                .build();
        return ResponseEntity.ok(ApiResponse.ok(info));
    }
}
