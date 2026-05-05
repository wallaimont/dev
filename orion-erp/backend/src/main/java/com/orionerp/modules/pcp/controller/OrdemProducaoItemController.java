package com.orionerp.modules.pcp.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.modules.pcp.dto.OrdemProducaoItemRequest;
import com.orionerp.modules.pcp.dto.OrdemProducaoItemResponse;
import com.orionerp.modules.pcp.service.OrdemProducaoItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pcp/ordem-producao-itens")
@RequiredArgsConstructor
public class OrdemProducaoItemController {

    private final OrdemProducaoItemService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrdemProducaoItemResponse>>> listar(
            @RequestParam Long ordemProducaoId) {
        return ResponseEntity.ok(ApiResponse.ok(service.listarPorOrdem(ordemProducaoId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrdemProducaoItemResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrdemProducaoItemResponse>> criar(
            @Valid @RequestBody OrdemProducaoItemRequest request) {
        return ResponseEntity.ok(ApiResponse.created(service.criar(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<OrdemProducaoItemResponse>> atualizar(
            @PathVariable Long id, @Valid @RequestBody OrdemProducaoItemRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.atualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
