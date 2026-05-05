package com.orionerp.modules.administration.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.modules.administration.dto.AuthResponse;
import com.orionerp.modules.administration.dto.AuthUserDto;
import com.orionerp.modules.administration.dto.LoginRequest;
import com.orionerp.modules.administration.dto.RefreshTokenRequest;
import com.orionerp.modules.administration.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticacao")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request,
                                                           HttpServletRequest httpRequest) {
        AuthResponse response = authService.login(request, httpRequest);
        return ResponseEntity.ok(ApiResponse.ok(response, "Login efetuado com sucesso"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refresh(request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Token renovado com sucesso"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(ApiResponse.ok(null, "Logout efetuado com sucesso"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthUserDto>> me() {
        AuthUserDto currentUser = authService.currentUser();
        return ResponseEntity.ok(ApiResponse.ok(currentUser));
    }
}
