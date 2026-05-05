package com.orionerp.modules.fiscal.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.modules.fiscal.dto.CfopRequest;
import com.orionerp.modules.fiscal.dto.CfopResponse;
import com.orionerp.modules.fiscal.service.CfopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fiscal/cfops")
@RequiredArgsConstructor
public class CfopController {

    private final CfopService cfopService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CfopResponse>>> listar(
            @RequestParam(required = false) String tipo) {
        return ResponseEntity.ok(ApiResponse.ok(cfopService.listar(tipo)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CfopResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(cfopService.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CfopResponse>> criar(
            @Valid @RequestBody CfopRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(cfopService.criar(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<CfopResponse>> atualizar(
            @PathVariable Long id, @Valid @RequestBody CfopRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(cfopService.atualizar(id, request)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> alterarStatus(@PathVariable Long id) {
        cfopService.alterarStatus(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
