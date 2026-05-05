package com.orionerp.modules.seguros.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.seguros.dto.CorretoraRequest;
import com.orionerp.modules.seguros.dto.CorretoraResponse;
import com.orionerp.modules.seguros.service.CorretoraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/seguros/corretoras")
@RequiredArgsConstructor
public class CorretoraController {

    private final CorretoraService service;

    @GetMapping
    @PreAuthorize("hasAuthority('corretoras:listar')")
    public ResponseEntity<ApiResponse<PageResponse<CorretoraResponse>>> list(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(service.list(empresaId, search, page, size)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('corretoras:listar')")
    public ResponseEntity<ApiResponse<CorretoraResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('corretoras:criar')")
    public ResponseEntity<ApiResponse<CorretoraResponse>> create(
            @Valid @RequestBody CorretoraRequest request, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(service.create(request, auth.getName())));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('corretoras:editar')")
    public ResponseEntity<ApiResponse<CorretoraResponse>> update(
            @PathVariable Long id, @Valid @RequestBody CorretoraRequest request, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(service.update(id, request, auth.getName())));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('corretoras:excluir')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, Authentication auth) {
        service.delete(id, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok(null, "Corretora removida com sucesso"));
    }
}
