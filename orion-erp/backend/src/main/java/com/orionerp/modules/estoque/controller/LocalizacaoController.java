package com.orionerp.modules.estoque.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.modules.estoque.dto.LocalizacaoRequest;
import com.orionerp.modules.estoque.dto.LocalizacaoResponse;
import com.orionerp.modules.estoque.service.LocalizacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/estoque/localizacoes")
@RequiredArgsConstructor
public class LocalizacaoController {

    private final LocalizacaoService localizacaoService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<LocalizacaoResponse>>> listarPorArmazem(
            @RequestParam Long armazemId) {
        return ResponseEntity.ok(ApiResponse.ok(localizacaoService.listarPorArmazem(armazemId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LocalizacaoResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(localizacaoService.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LocalizacaoResponse>> criar(
            @Valid @RequestBody LocalizacaoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(localizacaoService.criar(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<LocalizacaoResponse>> atualizar(
            @PathVariable Long id, @Valid @RequestBody LocalizacaoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(localizacaoService.atualizar(id, request)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> alterarStatus(@PathVariable Long id) {
        localizacaoService.alterarStatus(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
