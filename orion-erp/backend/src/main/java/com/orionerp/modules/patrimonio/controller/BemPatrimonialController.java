package com.orionerp.modules.patrimonio.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.patrimonio.dto.BemPatrimonialRequest;
import com.orionerp.modules.patrimonio.dto.BemPatrimonialResponse;
import com.orionerp.modules.patrimonio.service.BemPatrimonialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patrimonio/bens")
@RequiredArgsConstructor
public class BemPatrimonialController {

    private final BemPatrimonialService service;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<BemPatrimonialResponse>>> listar(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) String grupo,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String term,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                PageResponse.from(service.listar(empresaId, grupo, status, term, pageable))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BemPatrimonialResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BemPatrimonialResponse>> criar(
            @Valid @RequestBody BemPatrimonialRequest request) {
        return ResponseEntity.ok(ApiResponse.created(service.criar(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<BemPatrimonialResponse>> atualizar(
            @PathVariable Long id, @Valid @RequestBody BemPatrimonialRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.atualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
