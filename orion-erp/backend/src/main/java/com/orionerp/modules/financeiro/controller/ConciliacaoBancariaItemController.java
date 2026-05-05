package com.orionerp.modules.financeiro.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.modules.financeiro.dto.ConciliacaoBancariaItemRequest;
import com.orionerp.modules.financeiro.dto.ConciliacaoBancariaItemResponse;
import com.orionerp.modules.financeiro.service.ConciliacaoBancariaItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/financeiro/conciliacao-itens")
@RequiredArgsConstructor
public class ConciliacaoBancariaItemController {

    private final ConciliacaoBancariaItemService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ConciliacaoBancariaItemResponse>>> listar(
            @RequestParam Long conciliacaoId) {
        return ResponseEntity.ok(ApiResponse.ok(service.listarPorConciliacao(conciliacaoId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ConciliacaoBancariaItemResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ConciliacaoBancariaItemResponse>> criar(
            @Valid @RequestBody ConciliacaoBancariaItemRequest request) {
        return ResponseEntity.ok(ApiResponse.created(service.criar(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ConciliacaoBancariaItemResponse>> atualizar(
            @PathVariable Long id, @Valid @RequestBody ConciliacaoBancariaItemRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.atualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
