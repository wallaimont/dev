package com.orionerp.modules.financeiro.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.financeiro.dto.ConciliacaoBancariaRequest;
import com.orionerp.modules.financeiro.dto.ConciliacaoBancariaResponse;
import com.orionerp.modules.financeiro.service.ConciliacaoBancariaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/financeiro/conciliacoes")
@RequiredArgsConstructor
public class ConciliacaoBancariaController {

    private final ConciliacaoBancariaService service;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ConciliacaoBancariaResponse>>> listar(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) Long contaBancariaId,
            @RequestParam(required = false) String status,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(
                service.listar(empresaId, contaBancariaId, status, pageable))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ConciliacaoBancariaResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ConciliacaoBancariaResponse>> criar(
            @Valid @RequestBody ConciliacaoBancariaRequest request) {
        return ResponseEntity.ok(ApiResponse.created(service.criar(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ConciliacaoBancariaResponse>> atualizar(
            @PathVariable Long id, @Valid @RequestBody ConciliacaoBancariaRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.atualizar(id, request)));
    }

    @PatchMapping("/{id}/fechar")
    public ResponseEntity<ApiResponse<ConciliacaoBancariaResponse>> fechar(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.fechar(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
