package com.orionerp.modules.compras.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.compras.dto.RecebimentoRequest;
import com.orionerp.modules.compras.dto.RecebimentoResponse;
import com.orionerp.modules.compras.service.RecebimentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/compras/recebimentos")
@RequiredArgsConstructor
public class RecebimentoController {

    private final RecebimentoService recebimentoService;

    @GetMapping
    @PreAuthorize("hasAuthority('recebimentos:listar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<RecebimentoResponse>>> list(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) Long filialId,
            @RequestParam(required = false) Long fornecedorId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(
                recebimentoService.list(empresaId, filialId, fornecedorId, status, page, size)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('recebimentos:listar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<RecebimentoResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(recebimentoService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('recebimentos:criar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<RecebimentoResponse>> create(
            @Valid @RequestBody RecebimentoRequest request, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                recebimentoService.create(request, auth.getName())));
    }

    @PostMapping("/{id}/finalizar")
    @PreAuthorize("hasAuthority('recebimentos:finalizar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<RecebimentoResponse>> finalizar(
            @PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                recebimentoService.finalizar(id, auth.getName())));
    }
}
