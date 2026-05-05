package com.orionerp.modules.cadastros.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.cadastros.dto.ContaBancariaRequest;
import com.orionerp.modules.cadastros.dto.ContaBancariaResponse;
import com.orionerp.modules.cadastros.service.ContaBancariaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cadastros/contas-bancarias")
@RequiredArgsConstructor
public class ContaBancariaController {

    private final ContaBancariaService contaBancariaService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ContaBancariaResponse>>> list(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(contaBancariaService.list(empresaId, search, pageable))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContaBancariaResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(contaBancariaService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ContaBancariaResponse>> create(@RequestBody @Valid ContaBancariaRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(contaBancariaService.create(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ContaBancariaResponse>> update(@PathVariable Long id,
                                                                    @RequestBody @Valid ContaBancariaRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(contaBancariaService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        contaBancariaService.delete(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
