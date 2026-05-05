package com.orionerp.modules.administration.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.administration.dto.PerfilRequest;
import com.orionerp.modules.administration.dto.PerfilResponse;
import com.orionerp.modules.administration.service.PerfilService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/administracao/perfis")
@RequiredArgsConstructor
@Tag(name = "Administracao - Perfis")
public class PerfilController {

    private final PerfilService perfilService;

    @GetMapping
    @PreAuthorize("hasAuthority('perfis:listar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<PerfilResponse>>> list(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) String term,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(perfilService.list(empresaId, term, page, size)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('perfis:listar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PerfilResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(perfilService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('perfis:criar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PerfilResponse>> create(@Valid @RequestBody PerfilRequest request) {
        return ResponseEntity.ok(ApiResponse.created(perfilService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('perfis:editar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PerfilResponse>> update(@PathVariable Long id,
                                                              @Valid @RequestBody PerfilRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(perfilService.update(id, request), "Perfil atualizado com sucesso"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('perfis:excluir') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        perfilService.delete(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
