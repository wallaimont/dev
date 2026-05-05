package com.orionerp.modules.faturamento.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.modules.faturamento.dto.NotaFiscalItemRequest;
import com.orionerp.modules.faturamento.dto.NotaFiscalItemResponse;
import com.orionerp.modules.faturamento.service.NotaFiscalItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/faturamento/nota-fiscal-itens")
@RequiredArgsConstructor
public class NotaFiscalItemController {

    private final NotaFiscalItemService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotaFiscalItemResponse>>> listar(@RequestParam Long notaFiscalId) {
        return ResponseEntity.ok(ApiResponse.ok(service.listarPorNotaFiscal(notaFiscalId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NotaFiscalItemResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NotaFiscalItemResponse>> criar(@Valid @RequestBody NotaFiscalItemRequest request) {
        return ResponseEntity.ok(ApiResponse.created(service.criar(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<NotaFiscalItemResponse>> atualizar(@PathVariable Long id,
                                                                          @Valid @RequestBody NotaFiscalItemRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.atualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
