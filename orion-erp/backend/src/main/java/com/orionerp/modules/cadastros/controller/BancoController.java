package com.orionerp.modules.cadastros.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.cadastros.dto.BancoRequest;
import com.orionerp.modules.cadastros.dto.BancoResponse;
import com.orionerp.modules.cadastros.service.BancoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cadastros/bancos")
@RequiredArgsConstructor
public class BancoController {

    private final BancoService bancoService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<BancoResponse>>> list(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(bancoService.list(pageable))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BancoResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(bancoService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BancoResponse>> create(@RequestBody @Valid BancoRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(bancoService.create(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<BancoResponse>> update(@PathVariable Long id,
                                                             @RequestBody @Valid BancoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(bancoService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        bancoService.delete(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
