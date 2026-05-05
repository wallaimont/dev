package com.sigaseguros.controller;

import com.sigaseguros.dto.ApiResponse;
import com.sigaseguros.dto.SinistroDTO;
import com.sigaseguros.enums.StatusSinistro;
import com.sigaseguros.service.SinistroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sinistros")
@RequiredArgsConstructor
@Tag(name = "Sinistros")
public class SinistroController {

    private final SinistroService sinistroService;

    @GetMapping
    @Operation(summary = "Listar sinistros")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','COMERCIAL','OPERADOR')")
    public ResponseEntity<ApiResponse<Page<SinistroDTO>>> listar(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) Long seguradoraId,
            @RequestParam(required = false) StatusSinistro status,
            @PageableDefault(size = 20, sort = "dataAviso", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(sinistroService.listar(clienteId, seguradoraId, status, pageable)));
    }

    @GetMapping("/abertos")
    @Operation(summary = "Sinistros em aberto")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','OPERADOR')")
    public ResponseEntity<ApiResponse<List<SinistroDTO>>> sinistrosEmAberto() {
        return ResponseEntity.ok(ApiResponse.ok(sinistroService.sinistrosEmAberto()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar sinistro por ID")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','COMERCIAL','OPERADOR')")
    public ResponseEntity<ApiResponse<SinistroDTO>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(sinistroService.buscarPorId(id)));
    }

    @PostMapping
    @Operation(summary = "Criar sinistro")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','OPERADOR')")
    public ResponseEntity<ApiResponse<SinistroDTO>> criar(@Valid @RequestBody SinistroDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(sinistroService.criar(dto), "Sinistro registrado com sucesso"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar sinistro")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','OPERADOR')")
    public ResponseEntity<ApiResponse<SinistroDTO>> atualizar(@PathVariable Long id, @Valid @RequestBody SinistroDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok(sinistroService.atualizar(id, dto), "Sinistro atualizado com sucesso"));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Alterar status do sinistro")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','OPERADOR')")
    public ResponseEntity<ApiResponse<SinistroDTO>> alterarStatus(@PathVariable Long id, @RequestParam StatusSinistro status) {
        return ResponseEntity.ok(ApiResponse.ok(sinistroService.alterarStatus(id, status), "Status alterado com sucesso"));
    }
}
