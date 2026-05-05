package com.orionerp.modules.patrimonio.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.patrimonio.dto.DepreciacaoRequest;
import com.orionerp.modules.patrimonio.dto.DepreciacaoResponse;
import com.orionerp.modules.patrimonio.service.DepreciacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patrimonio/depreciacoes")
@RequiredArgsConstructor
public class DepreciacaoController {

    private final DepreciacaoService service;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<DepreciacaoResponse>>> listar(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) Long bemPatrimonialId,
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) Integer mes,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                PageResponse.from(service.listar(empresaId, bemPatrimonialId, ano, mes, pageable))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DepreciacaoResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DepreciacaoResponse>> criar(
            @Valid @RequestBody DepreciacaoRequest request) {
        return ResponseEntity.ok(ApiResponse.created(service.criar(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<DepreciacaoResponse>> atualizar(
            @PathVariable Long id, @Valid @RequestBody DepreciacaoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.atualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
