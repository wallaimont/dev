package com.orionerp.modules.pcp.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.pcp.dto.OrdemProducaoRequest;
import com.orionerp.modules.pcp.dto.OrdemProducaoResponse;
import com.orionerp.modules.pcp.service.OrdemProducaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pcp/ordens-producao")
@RequiredArgsConstructor
public class OrdemProducaoController {

    private final OrdemProducaoService service;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OrdemProducaoResponse>>> listar(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) Long produtoId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String prioridade,
            @RequestParam(required = false) String term,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                PageResponse.from(service.listar(empresaId, produtoId, status, prioridade, term, pageable))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrdemProducaoResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrdemProducaoResponse>> criar(
            @Valid @RequestBody OrdemProducaoRequest request) {
        return ResponseEntity.ok(ApiResponse.created(service.criar(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<OrdemProducaoResponse>> atualizar(
            @PathVariable Long id, @Valid @RequestBody OrdemProducaoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.atualizar(id, request)));
    }

    @PatchMapping("/{id}/iniciar")
    public ResponseEntity<ApiResponse<OrdemProducaoResponse>> iniciar(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.iniciar(id)));
    }

    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<ApiResponse<OrdemProducaoResponse>> finalizar(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.finalizar(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
