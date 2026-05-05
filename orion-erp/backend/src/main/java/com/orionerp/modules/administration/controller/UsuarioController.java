package com.orionerp.modules.administration.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.administration.dto.ResetPasswordRequest;
import com.orionerp.modules.administration.dto.UsuarioRequest;
import com.orionerp.modules.administration.dto.UsuarioResponse;
import com.orionerp.modules.administration.service.UsuarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/administracao/usuarios")
@RequiredArgsConstructor
@Tag(name = "Administracao - Usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    @PreAuthorize("hasAuthority('usuarios:listar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<UsuarioResponse>>> list(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) Long filialId,
            @RequestParam(required = false) String term,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(usuarioService.list(empresaId, filialId, term, page, size)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('usuarios:listar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<UsuarioResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(usuarioService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('usuarios:criar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<UsuarioResponse>> create(@Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.ok(ApiResponse.created(usuarioService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('usuarios:editar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<UsuarioResponse>> update(@PathVariable Long id,
                                                               @Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(usuarioService.update(id, request), "Usuario atualizado com sucesso"));
    }

    @PatchMapping("/{id}/reset-password")
    @PreAuthorize("hasAuthority('usuarios:editar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@PathVariable Long id,
                                                           @Valid @RequestBody ResetPasswordRequest request) {
        usuarioService.resetPassword(id, request.novaSenha());
        return ResponseEntity.ok(ApiResponse.ok(null, "Senha redefinida com sucesso"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('usuarios:excluir') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        usuarioService.delete(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
