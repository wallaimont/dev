package com.orionerp.modules.pcp.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.pcp.dto.EstruturaProdutoRequest;
import com.orionerp.modules.pcp.dto.EstruturaProdutoResponse;
import com.orionerp.modules.pcp.service.EstruturaProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pcp/estruturas-produto")
@RequiredArgsConstructor
public class EstruturaProdutoController {

    private final EstruturaProdutoService service;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<EstruturaProdutoResponse>>> listar(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) Long produtoPaiId,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                PageResponse.from(service.listar(empresaId, produtoPaiId, pageable))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EstruturaProdutoResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EstruturaProdutoResponse>> criar(
            @Valid @RequestBody EstruturaProdutoRequest request) {
        return ResponseEntity.ok(ApiResponse.created(service.criar(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<EstruturaProdutoResponse>> atualizar(
            @PathVariable Long id, @Valid @RequestBody EstruturaProdutoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.atualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
