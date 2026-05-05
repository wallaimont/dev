package com.orionerp.modules.contabilidade.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.contabilidade.dto.PlanoContasRequest;
import com.orionerp.modules.contabilidade.dto.PlanoContasResponse;
import com.orionerp.modules.contabilidade.service.PlanoContasService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contabilidade/plano-contas")
@RequiredArgsConstructor
public class PlanoContasController {

    private final PlanoContasService service;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PlanoContasResponse>>> listar(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String tipo,
            Pageable pageable) {
        var page = service.listar(empresaId, search, tipo, pageable);
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(page)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PlanoContasResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PlanoContasResponse>> criar(@Valid @RequestBody PlanoContasRequest request) {
        return ResponseEntity.ok(ApiResponse.created(service.criar(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<PlanoContasResponse>> atualizar(@PathVariable Long id,
                                                                       @Valid @RequestBody PlanoContasRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.atualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
