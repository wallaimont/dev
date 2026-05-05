package com.orionerp.modules.cadastros.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.cadastros.dto.GrupoProdutoRequest;
import com.orionerp.modules.cadastros.dto.GrupoProdutoResponse;
import com.orionerp.modules.cadastros.service.GrupoProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cadastros/grupos-produto")
@RequiredArgsConstructor
public class GrupoProdutoController {

    private final GrupoProdutoService grupoProdutoService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<GrupoProdutoResponse>>> list(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(grupoProdutoService.list(empresaId, search, pageable))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GrupoProdutoResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(grupoProdutoService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GrupoProdutoResponse>> create(@RequestBody @Valid GrupoProdutoRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(grupoProdutoService.create(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<GrupoProdutoResponse>> update(@PathVariable Long id,
                                                                    @RequestBody @Valid GrupoProdutoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(grupoProdutoService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        grupoProdutoService.delete(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
