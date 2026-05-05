package com.orionerp.modules.fiscal.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.fiscal.dto.RegraFiscalRequest;
import com.orionerp.modules.fiscal.dto.RegraFiscalResponse;
import com.orionerp.modules.fiscal.service.RegraFiscalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fiscal/regras-fiscais")
@RequiredArgsConstructor
public class RegraFiscalController {

    private final RegraFiscalService regraFiscalService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<RegraFiscalResponse>>> list(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) String ufOrigem,
            @RequestParam(required = false) String ufDestino,
            @RequestParam(required = false) Long ncmId,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                regraFiscalService.list(empresaId, ufOrigem, ufDestino, ncmId, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RegraFiscalResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(regraFiscalService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RegraFiscalResponse>> create(
            @Valid @RequestBody RegraFiscalRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(regraFiscalService.create(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable Long id) {
        regraFiscalService.deactivate(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @GetMapping("/aplicavel")
    public ResponseEntity<ApiResponse<RegraFiscalResponse>> findRegraAplicavel(
            @RequestParam Long empresaId,
            @RequestParam String ufOrigem,
            @RequestParam String ufDestino,
            @RequestParam(required = false) Long ncmId) {
        return ResponseEntity.ok(ApiResponse.ok(
                regraFiscalService.findRegraAplicavel(empresaId, ufOrigem, ufDestino, ncmId)
                        .orElse(null)));
    }
}
