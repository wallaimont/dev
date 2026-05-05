package com.orionerp.modules.rh.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.modules.rh.dto.FolhaPagamentoItemRequest;
import com.orionerp.modules.rh.dto.FolhaPagamentoItemResponse;
import com.orionerp.modules.rh.service.FolhaPagamentoItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rh/folha-pagamento-itens")
@RequiredArgsConstructor
public class FolhaPagamentoItemController {

    private final FolhaPagamentoItemService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<FolhaPagamentoItemResponse>>> listarPorFolha(
            @RequestParam Long folhaPagamentoId) {
        return ResponseEntity.ok(ApiResponse.ok(service.listarPorFolha(folhaPagamentoId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FolhaPagamentoItemResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FolhaPagamentoItemResponse>> criar(
            @Valid @RequestBody FolhaPagamentoItemRequest request) {
        return ResponseEntity.ok(ApiResponse.created(service.criar(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<FolhaPagamentoItemResponse>> atualizar(
            @PathVariable Long id, @Valid @RequestBody FolhaPagamentoItemRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.atualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
