package com.orionerp.modules.cadastros.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.cadastros.dto.CondicaoPagamentoRequest;
import com.orionerp.modules.cadastros.dto.CondicaoPagamentoResponse;
import com.orionerp.modules.cadastros.service.CondicaoPagamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cadastros/condicoes-pagamento")
@RequiredArgsConstructor
public class CondicaoPagamentoController {

    private final CondicaoPagamentoService condicaoPagamentoService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CondicaoPagamentoResponse>>> list(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(condicaoPagamentoService.list(empresaId, tipo, search, pageable))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CondicaoPagamentoResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(condicaoPagamentoService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CondicaoPagamentoResponse>> create(@RequestBody @Valid CondicaoPagamentoRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(condicaoPagamentoService.create(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<CondicaoPagamentoResponse>> update(@PathVariable Long id,
                                                                        @RequestBody @Valid CondicaoPagamentoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(condicaoPagamentoService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        condicaoPagamentoService.delete(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
