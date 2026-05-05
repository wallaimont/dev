package com.orbyt.marketplace.identity.api;

import com.orbyt.marketplace.identity.api.dto.AuthResponse;
import com.orbyt.marketplace.identity.api.dto.LoginRequest;
import com.orbyt.marketplace.identity.api.dto.RegisterRequest;
import com.orbyt.marketplace.identity.application.AuthService;
import com.orbyt.marketplace.identity.application.AuthUserPrincipal;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestHeader("X-Refresh-Token") String refreshToken) {
        return authService.refresh(refreshToken);
    }

    @PostMapping("/logout")
    public void logout(@RequestHeader("X-Refresh-Token") String refreshToken) {
        authService.logout(refreshToken);
    }

    @GetMapping("/me")
    public Map<String, Object> me(@AuthenticationPrincipal AuthUserPrincipal principal) {
        return Map.of(
                "id", principal.getUserId(),
                "email", principal.getEmail(),
                "roles", new String[]{"BUYER"},
                "tenantId", principal.getTenantId()
        );
    }
}
