package com.orionerp.modules.vendas.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.modules.vendas.dto.TabelaPrecoItemRequest;
import com.orionerp.modules.vendas.dto.TabelaPrecoItemResponse;
import com.orionerp.modules.vendas.service.TabelaPrecoItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vendas/tabela-preco-itens")
@RequiredArgsConstructor
public class TabelaPrecoItemController {

    private final TabelaPrecoItemService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TabelaPrecoItemResponse>>> listar(
            @RequestParam Long tabelaPrecoId) {
        return ResponseEntity.ok(ApiResponse.ok(service.listarPorTabela(tabelaPrecoId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TabelaPrecoItemResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TabelaPrecoItemResponse>> criar(
            @Valid @RequestBody TabelaPrecoItemRequest request) {
        return ResponseEntity.ok(ApiResponse.created(service.criar(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<TabelaPrecoItemResponse>> atualizar(
            @PathVariable Long id, @Valid @RequestBody TabelaPrecoItemRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.atualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
