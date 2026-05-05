package com.orionerp.modules.rh.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.rh.dto.BeneficioRequest;
import com.orionerp.modules.rh.dto.BeneficioResponse;
import com.orionerp.modules.rh.service.BeneficioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rh/beneficios")
@RequiredArgsConstructor
public class BeneficioController {

    private final BeneficioService service;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<BeneficioResponse>>> listar(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String term,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(service.listar(empresaId, tipo, term, pageable))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BeneficioResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BeneficioResponse>> criar(@Valid @RequestBody BeneficioRequest request) {
        return ResponseEntity.ok(ApiResponse.created(service.criar(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<BeneficioResponse>> atualizar(
            @PathVariable Long id, @Valid @RequestBody BeneficioRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.atualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
