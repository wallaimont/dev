package com.orionerp.modules.fiscal.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.modules.fiscal.dto.NcmRequest;
import com.orionerp.modules.fiscal.dto.NcmResponse;
import com.orionerp.modules.fiscal.service.NcmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fiscal/ncms")
@RequiredArgsConstructor
public class NcmController {

    private final NcmService ncmService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NcmResponse>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok(ncmService.listar()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NcmResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(ncmService.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NcmResponse>> criar(
            @Valid @RequestBody NcmRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(ncmService.criar(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<NcmResponse>> atualizar(
            @PathVariable Long id, @Valid @RequestBody NcmRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(ncmService.atualizar(id, request)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> alterarStatus(@PathVariable Long id) {
        ncmService.alterarStatus(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
