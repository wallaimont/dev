package com.sigaseguros.controller;

import com.sigaseguros.dto.ApiResponse;
import com.sigaseguros.dto.LancamentoFinanceiroDTO;
import com.sigaseguros.enums.StatusFinanceiro;
import com.sigaseguros.enums.TipoLancamento;
import com.sigaseguros.service.LancamentoFinanceiroService;
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

@RestController
@RequestMapping("/api/financeiro")
@RequiredArgsConstructor
@Tag(name = "Financeiro")
public class LancamentoFinanceiroController {

    private final LancamentoFinanceiroService lancamentoFinanceiroService;

    @GetMapping
    @Operation(summary = "Listar lançamentos financeiros")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','FINANCEIRO')")
    public ResponseEntity<ApiResponse<Page<LancamentoFinanceiroDTO>>> listar(
            @RequestParam(required = false) TipoLancamento tipo,
            @RequestParam(required = false) StatusFinanceiro status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dataFim,
            @PageableDefault(size = 20, sort = "vencimento", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(lancamentoFinanceiroService.listar(tipo, status, dataInicio, dataFim, pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar lançamento por ID")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','FINANCEIRO')")
    public ResponseEntity<ApiResponse<LancamentoFinanceiroDTO>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(lancamentoFinanceiroService.buscarPorId(id)));
    }

    @PostMapping
    @Operation(summary = "Criar lançamento financeiro")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','FINANCEIRO')")
    public ResponseEntity<ApiResponse<LancamentoFinanceiroDTO>> criar(@Valid @RequestBody LancamentoFinanceiroDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(lancamentoFinanceiroService.criar(dto), "Lançamento criado com sucesso"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar lançamento financeiro")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','FINANCEIRO')")
    public ResponseEntity<ApiResponse<LancamentoFinanceiroDTO>> atualizar(@PathVariable Long id, @Valid @RequestBody LancamentoFinanceiroDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok(lancamentoFinanceiroService.atualizar(id, dto), "Lançamento atualizado com sucesso"));
    }

    @PatchMapping("/{id}/baixa")
    @Operation(summary = "Registrar baixa (pagamento)")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','FINANCEIRO')")
    public ResponseEntity<ApiResponse<LancamentoFinanceiroDTO>> registrarBaixa(
            @PathVariable Long id,
            @RequestParam String dataPagamento) {
        return ResponseEntity.ok(ApiResponse.ok(lancamentoFinanceiroService.registrarBaixa(id, dataPagamento), "Baixa registrada com sucesso"));
    }
}
