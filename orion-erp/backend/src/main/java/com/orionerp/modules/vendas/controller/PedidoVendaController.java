package com.orionerp.modules.vendas.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.vendas.dto.PedidoVendaRequest;
import com.orionerp.modules.vendas.dto.PedidoVendaResponse;
import com.orionerp.modules.vendas.service.PedidoVendaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/vendas/pedidos")
@RequiredArgsConstructor
public class PedidoVendaController {

    private final PedidoVendaService pedidoVendaService;

    @GetMapping
    @PreAuthorize("hasAuthority('pedidos_venda:listar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<PedidoVendaResponse>>> list(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) Long filialId,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) Long vendedorId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String term,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(
                pedidoVendaService.list(empresaId, filialId, clienteId, vendedorId, status, term, page, size)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('pedidos_venda:listar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PedidoVendaResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(pedidoVendaService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('pedidos_venda:criar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PedidoVendaResponse>> create(
            @Valid @RequestBody PedidoVendaRequest request, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                pedidoVendaService.create(request, auth.getName())));
    }

    @PostMapping("/{id}/aprovar")
    @PreAuthorize("hasAuthority('pedidos_venda:aprovar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PedidoVendaResponse>> aprovar(
            @PathVariable Long id,
            @RequestParam(required = false) BigDecimal percentualComissao,
            Authentication auth) {
        // aprovadorId obtido do token — aqui simplificado
        return ResponseEntity.ok(ApiResponse.ok(
                pedidoVendaService.aprovar(id, null, percentualComissao, auth.getName())));
    }

    @PostMapping("/{id}/reprovar")
    @PreAuthorize("hasAuthority('pedidos_venda:aprovar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PedidoVendaResponse>> reprovar(
            @PathVariable Long id,
            @RequestParam(required = false) String motivo,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                pedidoVendaService.reprovar(id, motivo, auth.getName())));
    }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAuthority('pedidos_venda:cancelar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PedidoVendaResponse>> cancelar(
            @PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                pedidoVendaService.cancelar(id, auth.getName())));
    }
}
