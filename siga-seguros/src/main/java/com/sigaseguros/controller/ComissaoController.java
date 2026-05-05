package com.sigaseguros.controller;

import com.sigaseguros.dto.ApiResponse;
import com.sigaseguros.dto.ComissaoDTO;
import com.sigaseguros.enums.StatusComissao;
import com.sigaseguros.service.ComissaoService;
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

@RestController
@RequestMapping("/api/comissoes")
@RequiredArgsConstructor
@Tag(name = "Comissões")
public class ComissaoController {

    private final ComissaoService comissaoService;

    @GetMapping
    @Operation(summary = "Listar comissões")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','FINANCEIRO')")
    public ResponseEntity<ApiResponse<Page<ComissaoDTO>>> listar(
            @RequestParam(required = false) StatusComissao status,
            @RequestParam(required = false) Long seguradoraId,
            @RequestParam(required = false) Long corretoraId,
            @PageableDefault(size = 20, sort = "dataPrevista", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(comissaoService.listar(status, seguradoraId, corretoraId, pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar comissão por ID")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','FINANCEIRO')")
    public ResponseEntity<ApiResponse<ComissaoDTO>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(comissaoService.buscarPorId(id)));
    }

    @PostMapping
    @Operation(summary = "Criar comissão")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','FINANCEIRO')")
    public ResponseEntity<ApiResponse<ComissaoDTO>> criar(@Valid @RequestBody ComissaoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(comissaoService.criar(dto), "Comissão criada com sucesso"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar comissão")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','FINANCEIRO')")
    public ResponseEntity<ApiResponse<ComissaoDTO>> atualizar(@PathVariable Long id, @Valid @RequestBody ComissaoDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok(comissaoService.atualizar(id, dto), "Comissão atualizada com sucesso"));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Alterar status da comissão")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','FINANCEIRO')")
    public ResponseEntity<ApiResponse<ComissaoDTO>> alterarStatus(@PathVariable Long id, @RequestParam StatusComissao status) {
        return ResponseEntity.ok(ApiResponse.ok(comissaoService.alterarStatus(id, status), "Status alterado com sucesso"));
    }
}
