package com.orionerp.modules.rh.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.rh.dto.DepartamentoRhRequest;
import com.orionerp.modules.rh.dto.DepartamentoRhResponse;
import com.orionerp.modules.rh.service.DepartamentoRhService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rh/departamentos")
@RequiredArgsConstructor
public class DepartamentoRhController {

    private final DepartamentoRhService service;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<DepartamentoRhResponse>>> listar(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) String term,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(service.listar(empresaId, term, pageable))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartamentoRhResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DepartamentoRhResponse>> criar(@Valid @RequestBody DepartamentoRhRequest request) {
        return ResponseEntity.ok(ApiResponse.created(service.criar(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartamentoRhResponse>> atualizar(
            @PathVariable Long id, @Valid @RequestBody DepartamentoRhRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.atualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
