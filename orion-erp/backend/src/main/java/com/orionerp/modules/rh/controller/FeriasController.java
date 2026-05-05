package com.orionerp.modules.rh.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.rh.dto.FeriasRequest;
import com.orionerp.modules.rh.dto.FeriasResponse;
import com.orionerp.modules.rh.service.FeriasService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rh/ferias")
@RequiredArgsConstructor
public class FeriasController {

    private final FeriasService service;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<FeriasResponse>>> listar(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) Long funcionarioId,
            @RequestParam(required = false) String status,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                PageResponse.from(service.listar(empresaId, funcionarioId, status, pageable))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FeriasResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FeriasResponse>> criar(@Valid @RequestBody FeriasRequest request) {
        return ResponseEntity.ok(ApiResponse.created(service.criar(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<FeriasResponse>> atualizar(
            @PathVariable Long id, @Valid @RequestBody FeriasRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.atualizar(id, request)));
    }

    @PatchMapping("/{id}/aprovar")
    public ResponseEntity<ApiResponse<FeriasResponse>> aprovar(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.aprovar(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
