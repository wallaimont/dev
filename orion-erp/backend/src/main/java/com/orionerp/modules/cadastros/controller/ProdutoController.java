package com.orionerp.modules.cadastros.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.cadastros.dto.ProdutoRequest;
import com.orionerp.modules.cadastros.dto.ProdutoResponse;
import com.orionerp.modules.cadastros.service.ProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cadastros/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProdutoResponse>>> list(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) Long grupoId,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(produtoService.list(empresaId, grupoId, categoriaId, tipo, search, pageable))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProdutoResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(produtoService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProdutoResponse>> create(@RequestBody @Valid ProdutoRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(produtoService.create(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ProdutoResponse>> update(@PathVariable Long id,
                                                               @RequestBody @Valid ProdutoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(produtoService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        produtoService.delete(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
