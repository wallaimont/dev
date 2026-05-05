package com.sigaseguros.controller;

import com.sigaseguros.dto.ApiResponse;
import com.sigaseguros.dto.PropostaDTO;
import com.sigaseguros.enums.StatusProposta;
import com.sigaseguros.service.PropostaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/propostas")
@RequiredArgsConstructor
@Tag(name = "Propostas")
public class PropostaController {

    private final PropostaService propostaService;

    @GetMapping
    @Operation(summary = "Listar propostas")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','COMERCIAL','OPERADOR')")
    public ResponseEntity<ApiResponse<Page<PropostaDTO>>> listar(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) Long seguradoraId,
            @RequestParam(required = false) StatusProposta status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dataFim,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(propostaService.listar(clienteId, seguradoraId, status, dataInicio, dataFim, pageable)));
    }

    @GetMapping("/ultimas")
    @Operation(summary = "Últimas propostas (para dashboard)")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<PropostaDTO>>> ultimasPropostas() {
        return ResponseEntity.ok(ApiResponse.ok(propostaService.ultimasPropostas()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar proposta por ID")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','COMERCIAL','OPERADOR')")
    public ResponseEntity<ApiResponse<PropostaDTO>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(propostaService.buscarPorId(id)));
    }

    @PostMapping
    @Operation(summary = "Criar proposta")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','COMERCIAL')")
    public ResponseEntity<ApiResponse<PropostaDTO>> criar(@Valid @RequestBody PropostaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(propostaService.criar(dto), "Proposta criada com sucesso"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar proposta")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','COMERCIAL')")
    public ResponseEntity<ApiResponse<PropostaDTO>> atualizar(@PathVariable Long id, @Valid @RequestBody PropostaDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok(propostaService.atualizar(id, dto), "Proposta atualizada com sucesso"));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Alterar status da proposta")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','COMERCIAL','OPERADOR')")
    public ResponseEntity<ApiResponse<PropostaDTO>> alterarStatus(@PathVariable Long id, @RequestParam StatusProposta status) {
        return ResponseEntity.ok(ApiResponse.ok(propostaService.alterarStatus(id, status), "Status alterado com sucesso"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inativar proposta")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<ApiResponse<Void>> inativar(@PathVariable Long id) {
        propostaService.inativar(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Proposta inativada com sucesso"));
    }
}
