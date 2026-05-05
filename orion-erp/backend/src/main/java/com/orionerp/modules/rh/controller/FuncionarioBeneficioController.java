package com.orionerp.modules.rh.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.modules.rh.dto.FuncionarioBeneficioRequest;
import com.orionerp.modules.rh.dto.FuncionarioBeneficioResponse;
import com.orionerp.modules.rh.service.FuncionarioBeneficioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rh/funcionario-beneficios")
@RequiredArgsConstructor
public class FuncionarioBeneficioController {

    private final FuncionarioBeneficioService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<FuncionarioBeneficioResponse>>> listarPorFuncionario(
            @RequestParam Long funcionarioId) {
        return ResponseEntity.ok(ApiResponse.ok(service.listarPorFuncionario(funcionarioId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FuncionarioBeneficioResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FuncionarioBeneficioResponse>> criar(
            @Valid @RequestBody FuncionarioBeneficioRequest request) {
        return ResponseEntity.ok(ApiResponse.created(service.criar(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<FuncionarioBeneficioResponse>> atualizar(
            @PathVariable Long id, @Valid @RequestBody FuncionarioBeneficioRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.atualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
