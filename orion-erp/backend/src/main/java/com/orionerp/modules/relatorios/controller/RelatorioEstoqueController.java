package com.orionerp.modules.relatorios.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.modules.relatorios.dto.RelatorioEstoquePosicaoResponse;
import com.orionerp.modules.relatorios.dto.RelatorioMovimentacaoEstoqueResponse;
import com.orionerp.modules.relatorios.service.RelatorioEstoqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/relatorios/estoque")
@RequiredArgsConstructor
public class RelatorioEstoqueController {

    private final RelatorioEstoqueService service;

    @GetMapping("/posicao")
    public ResponseEntity<ApiResponse<List<RelatorioEstoquePosicaoResponse>>> posicao(
            @RequestParam Long empresaId) {

        return ResponseEntity.ok(ApiResponse.ok(service.posicaoEstoque(empresaId)));
    }

    @GetMapping("/movimentacoes")
    public ResponseEntity<ApiResponse<List<RelatorioMovimentacaoEstoqueResponse>>> movimentacoes(
            @RequestParam Long empresaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        return ResponseEntity.ok(ApiResponse.ok(service.movimentacoes(empresaId, inicio, fim)));
    }
}
