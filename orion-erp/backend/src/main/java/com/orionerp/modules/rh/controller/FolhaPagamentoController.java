package com.orionerp.modules.rh.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.rh.dto.FolhaPagamentoRequest;
import com.orionerp.modules.rh.dto.FolhaPagamentoResponse;
import com.orionerp.modules.rh.service.FolhaPagamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rh/folha-pagamento")
@RequiredArgsConstructor
public class FolhaPagamentoController {

    private final FolhaPagamentoService service;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<FolhaPagamentoResponse>>> listar(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String status,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                PageResponse.from(service.listar(empresaId, ano, mes, tipo, status, pageable))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FolhaPagamentoResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FolhaPagamentoResponse>> criar(
            @Valid @RequestBody FolhaPagamentoRequest request) {
        return ResponseEntity.ok(ApiResponse.created(service.criar(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<FolhaPagamentoResponse>> atualizar(
            @PathVariable Long id, @Valid @RequestBody FolhaPagamentoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.atualizar(id, request)));
    }

    @PatchMapping("/{id}/fechar")
    public ResponseEntity<ApiResponse<FolhaPagamentoResponse>> fechar(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.fechar(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
