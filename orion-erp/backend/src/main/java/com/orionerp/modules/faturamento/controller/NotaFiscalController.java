package com.orionerp.modules.faturamento.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.faturamento.dto.NotaFiscalRequest;
import com.orionerp.modules.faturamento.dto.NotaFiscalResponse;
import com.orionerp.modules.faturamento.service.NotaFiscalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/faturamento/notas-fiscais")
@RequiredArgsConstructor
public class NotaFiscalController {

    private final NotaFiscalService service;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<NotaFiscalResponse>>> listar(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String numero,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) String status,
            Pageable pageable) {
        var page = service.listar(empresaId, tipo, numero, dataInicio, dataFim, status, pageable);
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(page)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NotaFiscalResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NotaFiscalResponse>> criar(@Valid @RequestBody NotaFiscalRequest request) {
        return ResponseEntity.ok(ApiResponse.created(service.criar(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<NotaFiscalResponse>> atualizar(@PathVariable Long id,
                                                                      @Valid @RequestBody NotaFiscalRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.atualizar(id, request)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<NotaFiscalResponse>> alterarStatus(@PathVariable Long id,
                                                                          @RequestParam String status) {
        return ResponseEntity.ok(ApiResponse.ok(service.alterarStatus(id, status)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
