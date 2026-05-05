package com.orionerp.modules.cadastros.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.cadastros.dto.MarcaRequest;
import com.orionerp.modules.cadastros.dto.MarcaResponse;
import com.orionerp.modules.cadastros.service.MarcaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cadastros/marcas")
@RequiredArgsConstructor
public class MarcaController {

    private final MarcaService marcaService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<MarcaResponse>>> list(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(marcaService.list(empresaId, search, pageable))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MarcaResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(marcaService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MarcaResponse>> create(@RequestBody @Valid MarcaRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(marcaService.create(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<MarcaResponse>> update(@PathVariable Long id,
                                                             @RequestBody @Valid MarcaRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(marcaService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        marcaService.delete(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
