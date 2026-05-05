package com.orionerp.modules.contabilidade.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.contabilidade.dto.CentroResultadoRequest;
import com.orionerp.modules.contabilidade.dto.CentroResultadoResponse;
import com.orionerp.modules.contabilidade.service.CentroResultadoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contabilidade/centros-resultado")
@RequiredArgsConstructor
public class CentroResultadoController {

    private final CentroResultadoService service;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CentroResultadoResponse>>> listar(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        var page = service.listar(empresaId, search, pageable);
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(page)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CentroResultadoResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CentroResultadoResponse>> criar(@Valid @RequestBody CentroResultadoRequest request) {
        return ResponseEntity.ok(ApiResponse.created(service.criar(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<CentroResultadoResponse>> atualizar(@PathVariable Long id,
                                                                            @Valid @RequestBody CentroResultadoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.atualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
