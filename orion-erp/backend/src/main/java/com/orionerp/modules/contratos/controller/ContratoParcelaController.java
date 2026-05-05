package com.orionerp.modules.contratos.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.modules.contratos.dto.ContratoParcelaRequest;
import com.orionerp.modules.contratos.dto.ContratoParcelaResponse;
import com.orionerp.modules.contratos.service.ContratoParcelaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contrato-parcelas")
@RequiredArgsConstructor
public class ContratoParcelaController {

    private final ContratoParcelaService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ContratoParcelaResponse>>> listar(
            @RequestParam Long contratoId) {
        return ResponseEntity.ok(ApiResponse.ok(service.listarPorContrato(contratoId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContratoParcelaResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ContratoParcelaResponse>> criar(
            @Valid @RequestBody ContratoParcelaRequest request) {
        return ResponseEntity.ok(ApiResponse.created(service.criar(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ContratoParcelaResponse>> atualizar(
            @PathVariable Long id, @Valid @RequestBody ContratoParcelaRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.atualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
