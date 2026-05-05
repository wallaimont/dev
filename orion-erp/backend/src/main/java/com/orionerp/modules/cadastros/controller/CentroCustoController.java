package com.orionerp.modules.cadastros.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.cadastros.dto.CentroCustoRequest;
import com.orionerp.modules.cadastros.dto.CentroCustoResponse;
import com.orionerp.modules.cadastros.service.CentroCustoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cadastros/centros-custo")
@RequiredArgsConstructor
public class CentroCustoController {

    private final CentroCustoService centroCustoService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CentroCustoResponse>>> list(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(centroCustoService.list(empresaId, search, pageable))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CentroCustoResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(centroCustoService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CentroCustoResponse>> create(@RequestBody @Valid CentroCustoRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(centroCustoService.create(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<CentroCustoResponse>> update(@PathVariable Long id,
                                                                   @RequestBody @Valid CentroCustoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(centroCustoService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        centroCustoService.delete(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
