package com.sigaseguros.controller;

import com.sigaseguros.dto.ApiResponse;
import com.sigaseguros.dto.RenovacaoDTO;
import com.sigaseguros.enums.StatusRenovacao;
import com.sigaseguros.service.RenovacaoService;
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
@RequestMapping("/api/renovacoes")
@RequiredArgsConstructor
@Tag(name = "Renovações")
public class RenovacaoController {

    private final RenovacaoService renovacaoService;

    @GetMapping
    @Operation(summary = "Listar renovações")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','COMERCIAL','OPERADOR')")
    public ResponseEntity<ApiResponse<Page<RenovacaoDTO>>> listar(
            @RequestParam(required = false) StatusRenovacao status,
            @RequestParam(required = false) Long clienteId,
            @PageableDefault(size = 20, sort = "dataVencimento", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(renovacaoService.listar(status, clienteId, pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar renovação por ID")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','COMERCIAL','OPERADOR')")
    public ResponseEntity<ApiResponse<RenovacaoDTO>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(renovacaoService.buscarPorId(id)));
    }

    @PostMapping
    @Operation(summary = "Criar renovação")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','COMERCIAL','OPERADOR')")
    public ResponseEntity<ApiResponse<RenovacaoDTO>> criar(@Valid @RequestBody RenovacaoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(renovacaoService.criar(dto), "Renovação criada com sucesso"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar renovação")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','COMERCIAL','OPERADOR')")
    public ResponseEntity<ApiResponse<RenovacaoDTO>> atualizar(@PathVariable Long id, @Valid @RequestBody RenovacaoDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok(renovacaoService.atualizar(id, dto), "Renovação atualizada com sucesso"));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Alterar status da renovação")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','COMERCIAL','OPERADOR')")
    public ResponseEntity<ApiResponse<RenovacaoDTO>> alterarStatus(@PathVariable Long id, @RequestParam StatusRenovacao status) {
        return ResponseEntity.ok(ApiResponse.ok(renovacaoService.alterarStatus(id, status), "Status alterado com sucesso"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inativar renovação")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<ApiResponse<Void>> inativar(@PathVariable Long id) {
        renovacaoService.inativar(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Renovação inativada com sucesso"));
    }
}
