package com.orionerp.modules.seguros.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.seguros.dto.SeguradoraRequest;
import com.orionerp.modules.seguros.dto.SeguradoraResponse;
import com.orionerp.modules.seguros.service.SeguradoraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/seguros/seguradoras")
@RequiredArgsConstructor
public class SeguradoraController {

    private final SeguradoraService service;

    @GetMapping
    @PreAuthorize("hasAuthority('seguradoras:listar')")
    public ResponseEntity<ApiResponse<PageResponse<SeguradoraResponse>>> list(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(service.list(empresaId, search, page, size)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('seguradoras:listar')")
    public ResponseEntity<ApiResponse<SeguradoraResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('seguradoras:criar')")
    public ResponseEntity<ApiResponse<SeguradoraResponse>> create(
            @Valid @RequestBody SeguradoraRequest request, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(service.create(request, auth.getName())));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('seguradoras:editar')")
    public ResponseEntity<ApiResponse<SeguradoraResponse>> update(
            @PathVariable Long id, @Valid @RequestBody SeguradoraRequest request, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(service.update(id, request, auth.getName())));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('seguradoras:excluir')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, Authentication auth) {
        service.delete(id, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok(null, "Seguradora removida com sucesso"));
    }
}
