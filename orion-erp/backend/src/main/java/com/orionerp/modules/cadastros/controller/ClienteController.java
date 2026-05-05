package com.orionerp.modules.cadastros.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.cadastros.dto.ClienteRequest;
import com.orionerp.modules.cadastros.dto.ClienteResponse;
import com.orionerp.modules.cadastros.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cadastros/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ClienteResponse>>> list(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(clienteService.list(empresaId, search, pageable))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(clienteService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ClienteResponse>> create(@RequestBody @Valid ClienteRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(clienteService.create(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteResponse>> update(@PathVariable Long id,
                                                               @RequestBody @Valid ClienteRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(clienteService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        clienteService.delete(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
