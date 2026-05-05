package com.orionerp.modules.crm.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.crm.dto.OportunidadeRequest;
import com.orionerp.modules.crm.dto.OportunidadeResponse;
import com.orionerp.modules.crm.service.OportunidadeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/crm/oportunidades")
@RequiredArgsConstructor
public class OportunidadeController {

    private final OportunidadeService oportunidadeService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OportunidadeResponse>>> list(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) String etapaFunil,
            @RequestParam(required = false) Long responsavelId,
            @RequestParam(required = false) String term,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                oportunidadeService.list(empresaId, etapaFunil, responsavelId, term, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OportunidadeResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(oportunidadeService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OportunidadeResponse>> create(
            @Valid @RequestBody OportunidadeRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(oportunidadeService.create(request)));
    }

    @PostMapping("/{id}/avancar")
    public ResponseEntity<ApiResponse<OportunidadeResponse>> avancarEtapa(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(oportunidadeService.avancarEtapa(id)));
    }

    @PostMapping("/{id}/ganhar")
    public ResponseEntity<ApiResponse<OportunidadeResponse>> ganhar(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(oportunidadeService.ganhar(id)));
    }

    @PostMapping("/{id}/perder")
    public ResponseEntity<ApiResponse<OportunidadeResponse>> perder(
            @PathVariable Long id, @RequestParam(required = false) String motivo) {
        return ResponseEntity.ok(ApiResponse.ok(oportunidadeService.perder(id, motivo)));
    }
}
