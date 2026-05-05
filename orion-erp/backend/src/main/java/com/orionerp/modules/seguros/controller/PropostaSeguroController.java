package com.orionerp.modules.seguros.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.seguros.dto.PropostaSeguroRequest;
import com.orionerp.modules.seguros.dto.PropostaSeguroResponse;
import com.orionerp.modules.seguros.service.PropostaSeguroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/seguros/propostas")
@RequiredArgsConstructor
public class PropostaSeguroController {

    private final PropostaSeguroService service;

    @GetMapping
    @PreAuthorize("hasAuthority('propostas_seguro:listar')")
    public ResponseEntity<ApiResponse<PageResponse<PropostaSeguroResponse>>> list(
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
    @PreAuthorize("hasAuthority('propostas_seguro:listar')")
    public ResponseEntity<ApiResponse<PropostaSeguroResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('propostas_seguro:criar')")
    public ResponseEntity<ApiResponse<PropostaSeguroResponse>> create(
            @Valid @RequestBody PropostaSeguroRequest request, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(service.create(request, auth.getName())));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('propostas_seguro:editar')")
    public ResponseEntity<ApiResponse<PropostaSeguroResponse>> update(
            @PathVariable Long id, @Valid @RequestBody PropostaSeguroRequest request, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(service.update(id, request, auth.getName())));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('propostas_seguro:excluir')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, Authentication auth) {
        service.delete(id, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok(null, "Proposta removida com sucesso"));
    }
}
