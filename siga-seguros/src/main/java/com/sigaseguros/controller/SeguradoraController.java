package com.sigaseguros.controller;

import com.sigaseguros.dto.ApiResponse;
import com.sigaseguros.dto.SeguradoraDTO;
import com.sigaseguros.service.SeguradoraService;
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
@RequestMapping("/api/seguradoras")
@RequiredArgsConstructor
@Tag(name = "Seguradoras")
public class SeguradoraController {

    private final SeguradoraService seguradoraService;

    @GetMapping
    @Operation(summary = "Listar seguradoras paginado")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','COMERCIAL','OPERADOR')")
    public ResponseEntity<ApiResponse<Page<SeguradoraDTO>>> listar(
            @RequestParam(required = false) String nome,
            @PageableDefault(size = 20, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(seguradoraService.listar(nome, pageable)));
    }

    @GetMapping("/todas")
    @Operation(summary = "Listar todas as seguradoras (para combos)")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<SeguradoraDTO>>> listarTodas() {
        return ResponseEntity.ok(ApiResponse.ok(seguradoraService.listarTodas()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar seguradora por ID")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','COMERCIAL','OPERADOR')")
    public ResponseEntity<ApiResponse<SeguradoraDTO>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(seguradoraService.buscarPorId(id)));
    }

    @PostMapping
    @Operation(summary = "Criar seguradora")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<ApiResponse<SeguradoraDTO>> criar(@Valid @RequestBody SeguradoraDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(seguradoraService.criar(dto), "Seguradora criada com sucesso"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar seguradora")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<ApiResponse<SeguradoraDTO>> atualizar(@PathVariable Long id, @Valid @RequestBody SeguradoraDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok(seguradoraService.atualizar(id, dto), "Seguradora atualizada com sucesso"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inativar seguradora")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> inativar(@PathVariable Long id) {
        seguradoraService.inativar(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Seguradora inativada com sucesso"));
    }
}
