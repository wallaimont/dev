package com.orionerp.modules.rh.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.rh.dto.PontoEletronicoRequest;
import com.orionerp.modules.rh.dto.PontoEletronicoResponse;
import com.orionerp.modules.rh.service.PontoEletronicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/rh/ponto-eletronico")
@RequiredArgsConstructor
public class PontoEletronicoController {

    private final PontoEletronicoService service;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PontoEletronicoResponse>>> listar(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) Long funcionarioId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) String tipo,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                PageResponse.from(service.listar(empresaId, funcionarioId, dataInicio, dataFim, tipo, pageable))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PontoEletronicoResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PontoEletronicoResponse>> criar(
            @Valid @RequestBody PontoEletronicoRequest request) {
        return ResponseEntity.ok(ApiResponse.created(service.criar(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<PontoEletronicoResponse>> atualizar(
            @PathVariable Long id, @Valid @RequestBody PontoEletronicoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.atualizar(id, request)));
    }

    @PatchMapping("/{id}/aprovar")
    public ResponseEntity<ApiResponse<PontoEletronicoResponse>> aprovar(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.aprovar(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
