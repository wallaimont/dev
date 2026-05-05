package com.orionerp.modules.cadastros.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.cadastros.dto.UnidadeMedidaRequest;
import com.orionerp.modules.cadastros.dto.UnidadeMedidaResponse;
import com.orionerp.modules.cadastros.service.UnidadeMedidaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cadastros/unidades-medida")
@RequiredArgsConstructor
public class UnidadeMedidaController {

    private final UnidadeMedidaService unidadeMedidaService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<UnidadeMedidaResponse>>> list(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(unidadeMedidaService.list(pageable))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UnidadeMedidaResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(unidadeMedidaService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UnidadeMedidaResponse>> create(@RequestBody @Valid UnidadeMedidaRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(unidadeMedidaService.create(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UnidadeMedidaResponse>> update(@PathVariable Long id,
                                                                     @RequestBody @Valid UnidadeMedidaRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(unidadeMedidaService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        unidadeMedidaService.delete(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
