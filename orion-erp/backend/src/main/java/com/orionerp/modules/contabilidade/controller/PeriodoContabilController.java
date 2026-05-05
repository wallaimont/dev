package com.orionerp.modules.contabilidade.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.modules.contabilidade.dto.PeriodoContabilRequest;
import com.orionerp.modules.contabilidade.dto.PeriodoContabilResponse;
import com.orionerp.modules.contabilidade.service.PeriodoContabilService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contabilidade/periodos")
@RequiredArgsConstructor
public class PeriodoContabilController {

    private final PeriodoContabilService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PeriodoContabilResponse>>> listar(
            @RequestParam Long empresaId,
            @RequestParam Integer ano) {
        return ResponseEntity.ok(ApiResponse.ok(service.listarPorAno(empresaId, ano)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PeriodoContabilResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PeriodoContabilResponse>> criar(@Valid @RequestBody PeriodoContabilRequest request) {
        return ResponseEntity.ok(ApiResponse.created(service.criar(request)));
    }

    @PostMapping("/{id}/fechar")
    public ResponseEntity<ApiResponse<PeriodoContabilResponse>> fechar(@PathVariable Long id,
                                                                        @RequestParam String usuario) {
        return ResponseEntity.ok(ApiResponse.ok(service.fechar(id, usuario)));
    }

    @PostMapping("/{id}/reabrir")
    public ResponseEntity<ApiResponse<PeriodoContabilResponse>> reabrir(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.reabrir(id)));
    }
}
