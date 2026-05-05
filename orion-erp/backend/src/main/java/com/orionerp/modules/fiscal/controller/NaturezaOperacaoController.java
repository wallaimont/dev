package com.orionerp.modules.fiscal.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.fiscal.dto.NaturezaOperacaoRequest;
import com.orionerp.modules.fiscal.dto.NaturezaOperacaoResponse;
import com.orionerp.modules.fiscal.service.NaturezaOperacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fiscal/naturezas-operacao")
@RequiredArgsConstructor
public class NaturezaOperacaoController {

    private final NaturezaOperacaoService naturezaOperacaoService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<NaturezaOperacaoResponse>>> list(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String term,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(naturezaOperacaoService.list(empresaId, tipo, term, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NaturezaOperacaoResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(naturezaOperacaoService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NaturezaOperacaoResponse>> create(
            @Valid @RequestBody NaturezaOperacaoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(naturezaOperacaoService.create(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        naturezaOperacaoService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
