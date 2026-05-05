package com.orionerp.modules.cadastros.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.cadastros.dto.FornecedorRequest;
import com.orionerp.modules.cadastros.dto.FornecedorResponse;
import com.orionerp.modules.cadastros.service.FornecedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cadastros/fornecedores")
@RequiredArgsConstructor
public class FornecedorController {

    private final FornecedorService fornecedorService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<FornecedorResponse>>> list(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(fornecedorService.list(empresaId, search, pageable))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FornecedorResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(fornecedorService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FornecedorResponse>> create(@RequestBody @Valid FornecedorRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(fornecedorService.create(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<FornecedorResponse>> update(@PathVariable Long id,
                                                                  @RequestBody @Valid FornecedorRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(fornecedorService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        fornecedorService.delete(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
