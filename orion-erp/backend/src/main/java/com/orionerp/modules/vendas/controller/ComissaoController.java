package com.orionerp.modules.vendas.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.modules.vendas.dto.ComissaoRequest;
import com.orionerp.modules.vendas.dto.ComissaoResponse;
import com.orionerp.modules.vendas.service.ComissaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vendas/comissoes")
@RequiredArgsConstructor
public class ComissaoController {

    private final ComissaoService comissaoService;

    @GetMapping("/vendedor/{vendedorId}")
    public ResponseEntity<ApiResponse<List<ComissaoResponse>>> listarPorVendedor(
            @PathVariable Long vendedorId, @RequestParam Long empresaId) {
        return ResponseEntity.ok(ApiResponse.ok(comissaoService.listarPorVendedor(empresaId, vendedorId)));
    }

    @GetMapping("/pedido/{pedidoVendaId}")
    public ResponseEntity<ApiResponse<List<ComissaoResponse>>> listarPorPedido(
            @PathVariable Long pedidoVendaId) {
        return ResponseEntity.ok(ApiResponse.ok(comissaoService.listarPorPedido(pedidoVendaId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ComissaoResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(comissaoService.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ComissaoResponse>> criar(
            @Valid @RequestBody ComissaoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(comissaoService.criar(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ComissaoResponse>> atualizar(
            @PathVariable Long id, @Valid @RequestBody ComissaoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(comissaoService.atualizar(id, request)));
    }

    @PatchMapping("/{id}/pagar")
    public ResponseEntity<ApiResponse<Void>> pagar(@PathVariable Long id) {
        comissaoService.pagar(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
