package com.orionerp.modules.rh.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.rh.dto.FuncionarioRequest;
import com.orionerp.modules.rh.dto.FuncionarioResponse;
import com.orionerp.modules.rh.service.FuncionarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/rh/funcionarios")
@RequiredArgsConstructor
public class FuncionarioController {

    private final FuncionarioService funcionarioService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<FuncionarioResponse>>> list(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) Long filialId,
            @RequestParam(required = false) Long departamentoId,
            @RequestParam(required = false) String situacao,
            @RequestParam(required = false) String term,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                funcionarioService.list(empresaId, filialId, departamentoId, situacao, term, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FuncionarioResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(funcionarioService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FuncionarioResponse>> create(
            @Valid @RequestBody FuncionarioRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(funcionarioService.create(request)));
    }

    @PostMapping("/{id}/demitir")
    public ResponseEntity<ApiResponse<FuncionarioResponse>> demitir(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataDemissao) {
        return ResponseEntity.ok(ApiResponse.ok(funcionarioService.demitir(id, dataDemissao)));
    }

    @PatchMapping("/{id}/situacao")
    public ResponseEntity<ApiResponse<FuncionarioResponse>> alterarSituacao(
            @PathVariable Long id, @RequestParam String situacao) {
        return ResponseEntity.ok(ApiResponse.ok(funcionarioService.alterarSituacao(id, situacao)));
    }
}
