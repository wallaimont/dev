package com.orionerp.modules.fiscal.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.modules.fiscal.dto.CstRequest;
import com.orionerp.modules.fiscal.dto.CstResponse;
import com.orionerp.modules.fiscal.service.CstService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fiscal/csts")
@RequiredArgsConstructor
public class CstController {

    private final CstService cstService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CstResponse>>> listar(
            @RequestParam(required = false) String tipoImposto) {
        return ResponseEntity.ok(ApiResponse.ok(cstService.listar(tipoImposto)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CstResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(cstService.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CstResponse>> criar(
            @Valid @RequestBody CstRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(cstService.criar(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<CstResponse>> atualizar(
            @PathVariable Long id, @Valid @RequestBody CstRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(cstService.atualizar(id, request)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> alterarStatus(@PathVariable Long id) {
        cstService.alterarStatus(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
