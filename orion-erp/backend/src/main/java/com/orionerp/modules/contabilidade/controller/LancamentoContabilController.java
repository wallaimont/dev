package com.orionerp.modules.contabilidade.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.contabilidade.dto.LancamentoContabilRequest;
import com.orionerp.modules.contabilidade.dto.LancamentoContabilResponse;
import com.orionerp.modules.contabilidade.service.LancamentoContabilService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/contabilidade/lancamentos")
@RequiredArgsConstructor
public class LancamentoContabilController {

    private final LancamentoContabilService service;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<LancamentoContabilResponse>>> listar(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) String lote,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) String status,
            Pageable pageable) {
        var page = service.listar(empresaId, lote, dataInicio, dataFim, status, pageable);
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(page)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LancamentoContabilResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LancamentoContabilResponse>> criar(@Valid @RequestBody LancamentoContabilRequest request) {
        return ResponseEntity.ok(ApiResponse.created(service.criar(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<LancamentoContabilResponse>> atualizar(@PathVariable Long id,
                                                                              @Valid @RequestBody LancamentoContabilRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.atualizar(id, request)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<LancamentoContabilResponse>> alterarStatus(@PathVariable Long id,
                                                                                  @RequestParam String status) {
        return ResponseEntity.ok(ApiResponse.ok(service.alterarStatus(id, status)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
