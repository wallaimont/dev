package com.orionerp.modules.estoque.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.modules.estoque.dto.SaldoEstoqueResponse;
import com.orionerp.modules.estoque.service.SaldoEstoqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/estoque/saldos")
@RequiredArgsConstructor
public class SaldoEstoqueController {

    private final SaldoEstoqueService saldoEstoqueService;

    @GetMapping("/produto/{produtoId}")
    public ResponseEntity<ApiResponse<List<SaldoEstoqueResponse>>> listarPorProduto(
            @PathVariable Long produtoId, @RequestParam Long empresaId) {
        return ResponseEntity.ok(ApiResponse.ok(saldoEstoqueService.listarPorProduto(empresaId, produtoId)));
    }

    @GetMapping("/armazem/{armazemId}")
    public ResponseEntity<ApiResponse<List<SaldoEstoqueResponse>>> listarPorArmazem(
            @PathVariable Long armazemId, @RequestParam Long empresaId) {
        return ResponseEntity.ok(ApiResponse.ok(saldoEstoqueService.listarPorArmazem(empresaId, armazemId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SaldoEstoqueResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(saldoEstoqueService.buscarPorId(id)));
    }
}
