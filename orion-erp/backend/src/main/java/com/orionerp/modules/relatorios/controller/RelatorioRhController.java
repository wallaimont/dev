package com.orionerp.modules.relatorios.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.modules.relatorios.dto.RelatorioAniversariantesResponse;
import com.orionerp.modules.relatorios.dto.RelatorioFolhaPagamentoResponse;
import com.orionerp.modules.relatorios.service.RelatorioRhService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/relatorios/rh")
@RequiredArgsConstructor
public class RelatorioRhController {

    private final RelatorioRhService service;

    @GetMapping("/folha-resumo")
    public ResponseEntity<ApiResponse<RelatorioFolhaPagamentoResponse>> resumoFolha(
            @RequestParam Long empresaId,
            @RequestParam Integer ano,
            @RequestParam Integer mes) {

        return ResponseEntity.ok(ApiResponse.ok(service.resumoFolha(empresaId, ano, mes)));
    }

    @GetMapping("/aniversariantes")
    public ResponseEntity<ApiResponse<List<RelatorioAniversariantesResponse>>> aniversariantes(
            @RequestParam Long empresaId,
            @RequestParam Integer mes) {

        return ResponseEntity.ok(ApiResponse.ok(service.aniversariantes(empresaId, mes)));
    }
}
