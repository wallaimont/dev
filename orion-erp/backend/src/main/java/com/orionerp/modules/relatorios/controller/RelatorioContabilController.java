package com.orionerp.modules.relatorios.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.modules.relatorios.dto.RelatorioDreSimplificadoResponse;
import com.orionerp.modules.relatorios.service.RelatorioContabilService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/relatorios/contabil")
@RequiredArgsConstructor
public class RelatorioContabilController {

    private final RelatorioContabilService service;

    @GetMapping("/dre")
    public ResponseEntity<ApiResponse<RelatorioDreSimplificadoResponse>> dre(
            @RequestParam Long empresaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        return ResponseEntity.ok(ApiResponse.ok(service.dreSimplificado(empresaId, inicio, fim)));
    }
}
