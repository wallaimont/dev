package com.orionerp.modules.pcp.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.pcp.dto.ApontamentoProducaoRequest;
import com.orionerp.modules.pcp.dto.ApontamentoProducaoResponse;
import com.orionerp.modules.pcp.service.ApontamentoProducaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pcp/apontamentos")
@RequiredArgsConstructor
public class ApontamentoProducaoController {

    private final ApontamentoProducaoService service;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ApontamentoProducaoResponse>>> listar(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) Long ordemProducaoId,
            @RequestParam(required = false) Long funcionarioId,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                PageResponse.from(service.listar(empresaId, ordemProducaoId, funcionarioId, pageable))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ApontamentoProducaoResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ApontamentoProducaoResponse>> criar(
            @Valid @RequestBody ApontamentoProducaoRequest request) {
        return ResponseEntity.ok(ApiResponse.created(service.criar(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ApontamentoProducaoResponse>> atualizar(
            @PathVariable Long id, @Valid @RequestBody ApontamentoProducaoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.atualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
