package com.sigaseguros.controller;

import com.sigaseguros.dto.ApoliceDTO;
import com.sigaseguros.dto.ApiResponse;
import com.sigaseguros.enums.StatusApolice;
import com.sigaseguros.service.ApoliceService;
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
@RequestMapping("/api/apolices")
@RequiredArgsConstructor
@Tag(name = "Apólices")
public class ApoliceController {

    private final ApoliceService apoliceService;

    @GetMapping
    @Operation(summary = "Listar apólices")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','COMERCIAL','OPERADOR')")
    public ResponseEntity<ApiResponse<Page<ApoliceDTO>>> listar(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) Long seguradoraId,
            @RequestParam(required = false) StatusApolice status,
            @RequestParam(required = false) Long ramoId,
            @PageableDefault(size = 20, sort = "fimVigencia", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(apoliceService.listar(clienteId, seguradoraId, status, ramoId, pageable)));
    }

    @GetMapping("/vencendo")
    @Operation(summary = "Apólices vencendo em 30 dias")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','COMERCIAL','OPERADOR')")
    public ResponseEntity<ApiResponse<List<ApoliceDTO>>> vencendoEm30Dias() {
        return ResponseEntity.ok(ApiResponse.ok(apoliceService.vencendoEm30Dias()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar apólice por ID")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','COMERCIAL','OPERADOR')")
    public ResponseEntity<ApiResponse<ApoliceDTO>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(apoliceService.buscarPorId(id)));
    }

    @PostMapping
    @Operation(summary = "Criar apólice")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','OPERADOR')")
    public ResponseEntity<ApiResponse<ApoliceDTO>> criar(@Valid @RequestBody ApoliceDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(apoliceService.criar(dto), "Apólice criada com sucesso"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar apólice")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','OPERADOR')")
    public ResponseEntity<ApiResponse<ApoliceDTO>> atualizar(@PathVariable Long id, @Valid @RequestBody ApoliceDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok(apoliceService.atualizar(id, dto), "Apólice atualizada com sucesso"));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Alterar status da apólice")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','OPERADOR')")
    public ResponseEntity<ApiResponse<ApoliceDTO>> alterarStatus(@PathVariable Long id, @RequestParam StatusApolice status) {
        return ResponseEntity.ok(ApiResponse.ok(apoliceService.alterarStatus(id, status), "Status alterado com sucesso"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inativar apólice")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<ApiResponse<Void>> inativar(@PathVariable Long id) {
        apoliceService.inativar(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Apólice inativada com sucesso"));
    }
}
