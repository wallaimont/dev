package com.orionerp.modules.cadastros.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.cadastros.dto.CategoriaRequest;
import com.orionerp.modules.cadastros.dto.CategoriaResponse;
import com.orionerp.modules.cadastros.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cadastros/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CategoriaResponse>>> list(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(categoriaService.list(empresaId, search, pageable))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoriaResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(categoriaService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoriaResponse>> create(@RequestBody @Valid CategoriaRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(categoriaService.create(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoriaResponse>> update(@PathVariable Long id,
                                                                 @RequestBody @Valid CategoriaRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(categoriaService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        categoriaService.delete(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
