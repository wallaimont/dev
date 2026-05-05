package com.orionerp.modules.estoque.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.estoque.dto.MovimentacaoRequest;
import com.orionerp.modules.estoque.dto.MovimentacaoResponse;
import com.orionerp.modules.estoque.dto.SaldoEstoqueResponse;
import com.orionerp.modules.estoque.service.MovimentacaoEstoqueService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/estoque/movimentacoes")
@RequiredArgsConstructor
@Tag(name = "Estoque - Movimentacoes")
public class MovimentacaoEstoqueController {

    private final MovimentacaoEstoqueService movimentacaoService;

    @GetMapping
    @PreAuthorize("hasAuthority('estoque:listar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<MovimentacaoResponse>>> list(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) Long filialId,
            @RequestParam(required = false) Long armazemId,
            @RequestParam(required = false) Long produtoId,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(
                movimentacaoService.list(empresaId, filialId, armazemId, produtoId, tipo, dataInicio, dataFim, page, size)));
    }

    @GetMapping("/saldos")
    @PreAuthorize("hasAuthority('estoque:listar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<SaldoEstoqueResponse>>> listSaldos(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) Long filialId,
            @RequestParam(required = false) Long armazemId,
            @RequestParam(required = false) Long produtoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(
                movimentacaoService.listSaldos(empresaId, filialId, armazemId, produtoId, page, size)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('estoque:movimentar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<MovimentacaoResponse>> movimentar(
            @Valid @RequestBody MovimentacaoRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.created(
                movimentacaoService.movimentar(request, userDetails.getUsername())));
    }
}
