package com.orionerp.modules.relatorios.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.modules.relatorios.dto.RelatorioComissaoVendedorResponse;
import com.orionerp.modules.relatorios.dto.RelatorioVendasPeriodoResponse;
import com.orionerp.modules.relatorios.service.RelatorioVendasService;
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
@RequestMapping("/api/relatorios/vendas")
@RequiredArgsConstructor
public class RelatorioVendasController {

    private final RelatorioVendasService service;

    @GetMapping("/periodo")
    public ResponseEntity<ApiResponse<RelatorioVendasPeriodoResponse>> vendasPorPeriodo(
            @RequestParam Long empresaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        return ResponseEntity.ok(ApiResponse.ok(service.vendasPorPeriodo(empresaId, inicio, fim)));
    }

    @GetMapping("/comissoes")
    public ResponseEntity<ApiResponse<List<RelatorioComissaoVendedorResponse>>> comissoes(
            @RequestParam Long empresaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        return ResponseEntity.ok(ApiResponse.ok(service.comissoesPorVendedor(empresaId, inicio, fim)));
    }
}
