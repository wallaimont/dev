package com.orionerp.modules.seguros.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.seguros.dto.ApoliceRequest;
import com.orionerp.modules.seguros.dto.ApoliceResponse;
import com.orionerp.modules.seguros.service.ApoliceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/seguros/apolices")
@RequiredArgsConstructor
public class ApoliceController {

    private final ApoliceService service;

    @GetMapping
    @PreAuthorize("hasAuthority('apolices:listar')")
    public ResponseEntity<ApiResponse<PageResponse<ApoliceResponse>>> list(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) Long seguradoraId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(service.list(empresaId, clienteId, seguradoraId, status, search, page, size)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('apolices:listar')")
    public ResponseEntity<ApiResponse<ApoliceResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('apolices:criar')")
    public ResponseEntity<ApiResponse<ApoliceResponse>> create(
            @Valid @RequestBody ApoliceRequest request, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(service.create(request, auth.getName())));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('apolices:editar')")
    public ResponseEntity<ApiResponse<ApoliceResponse>> update(
            @PathVariable Long id, @Valid @RequestBody ApoliceRequest request, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(service.update(id, request, auth.getName())));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('apolices:excluir')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, Authentication auth) {
        service.delete(id, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok(null, "Apólice removida com sucesso"));
    }
}
