package com.orionerp.modules.cadastros.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.cadastros.dto.TransportadoraRequest;
import com.orionerp.modules.cadastros.dto.TransportadoraResponse;
import com.orionerp.modules.cadastros.service.TransportadoraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cadastros/transportadoras")
@RequiredArgsConstructor
public class TransportadoraController {

    private final TransportadoraService transportadoraService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<TransportadoraResponse>>> list(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(transportadoraService.list(empresaId, search, pageable))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransportadoraResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(transportadoraService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TransportadoraResponse>> create(@RequestBody @Valid TransportadoraRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(transportadoraService.create(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<TransportadoraResponse>> update(@PathVariable Long id,
                                                                     @RequestBody @Valid TransportadoraRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(transportadoraService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        transportadoraService.delete(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
