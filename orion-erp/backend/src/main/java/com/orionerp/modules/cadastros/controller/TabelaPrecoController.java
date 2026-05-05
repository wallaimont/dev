package com.orionerp.modules.cadastros.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.cadastros.dto.TabelaPrecoRequest;
import com.orionerp.modules.cadastros.dto.TabelaPrecoResponse;
import com.orionerp.modules.cadastros.service.TabelaPrecoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cadastros/tabelas-preco")
@RequiredArgsConstructor
public class TabelaPrecoController {

    private final TabelaPrecoService tabelaPrecoService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<TabelaPrecoResponse>>> list(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(tabelaPrecoService.list(empresaId, search, pageable))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TabelaPrecoResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(tabelaPrecoService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TabelaPrecoResponse>> create(@RequestBody @Valid TabelaPrecoRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(tabelaPrecoService.create(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<TabelaPrecoResponse>> update(@PathVariable Long id,
                                                                   @RequestBody @Valid TabelaPrecoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(tabelaPrecoService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        tabelaPrecoService.delete(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
