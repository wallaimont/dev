package com.orionerp.modules.cadastros.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.cadastros.dto.SubgrupoProdutoRequest;
import com.orionerp.modules.cadastros.dto.SubgrupoProdutoResponse;
import com.orionerp.modules.cadastros.service.SubgrupoProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cadastros/subgrupos-produto")
@RequiredArgsConstructor
public class SubgrupoProdutoController {

    private final SubgrupoProdutoService subgrupoProdutoService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<SubgrupoProdutoResponse>>> list(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) Long grupoId,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(subgrupoProdutoService.list(empresaId, grupoId, search, pageable))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubgrupoProdutoResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(subgrupoProdutoService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SubgrupoProdutoResponse>> create(@RequestBody @Valid SubgrupoProdutoRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(subgrupoProdutoService.create(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<SubgrupoProdutoResponse>> update(@PathVariable Long id,
                                                                      @RequestBody @Valid SubgrupoProdutoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(subgrupoProdutoService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        subgrupoProdutoService.delete(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
