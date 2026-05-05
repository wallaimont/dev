package com.orionerp.modules.cadastros.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.cadastros.dto.NaturezaFinanceiraRequest;
import com.orionerp.modules.cadastros.dto.NaturezaFinanceiraResponse;
import com.orionerp.modules.cadastros.service.NaturezaFinanceiraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cadastros/naturezas-financeiras")
@RequiredArgsConstructor
public class NaturezaFinanceiraController {

    private final NaturezaFinanceiraService naturezaFinanceiraService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<NaturezaFinanceiraResponse>>> list(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(naturezaFinanceiraService.list(empresaId, tipo, search, pageable))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NaturezaFinanceiraResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(naturezaFinanceiraService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NaturezaFinanceiraResponse>> create(@RequestBody @Valid NaturezaFinanceiraRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(naturezaFinanceiraService.create(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<NaturezaFinanceiraResponse>> update(@PathVariable Long id,
                                                                         @RequestBody @Valid NaturezaFinanceiraRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(naturezaFinanceiraService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        naturezaFinanceiraService.delete(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
