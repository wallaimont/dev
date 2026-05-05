package com.orionerp.modules.relatorios.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.modules.relatorios.dto.RelatorioContasVencidasResponse;
import com.orionerp.modules.relatorios.dto.RelatorioFinanceiroResumo;
import com.orionerp.modules.relatorios.dto.RelatorioFluxoCaixaResponse;
import com.orionerp.modules.relatorios.service.RelatorioFinanceiroService;
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
@RequestMapping("/api/relatorios/financeiro")
@RequiredArgsConstructor
public class RelatorioFinanceiroController {

    private final RelatorioFinanceiroService service;

    @GetMapping("/resumo")
    public ResponseEntity<ApiResponse<RelatorioFinanceiroResumo>> resumo(
            @RequestParam Long empresaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        return ResponseEntity.ok(ApiResponse.ok(service.resumoFinanceiro(empresaId, inicio, fim)));
    }

    @GetMapping("/contas-vencidas")
    public ResponseEntity<ApiResponse<List<RelatorioContasVencidasResponse>>> contasVencidas(
            @RequestParam Long empresaId) {

        return ResponseEntity.ok(ApiResponse.ok(service.contasVencidas(empresaId)));
    }

    @GetMapping("/fluxo-caixa")
    public ResponseEntity<ApiResponse<List<RelatorioFluxoCaixaResponse>>> fluxoCaixa(
            @RequestParam Long empresaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        return ResponseEntity.ok(ApiResponse.ok(service.fluxoCaixa(empresaId, inicio, fim)));
    }
}
