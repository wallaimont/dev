package com.orionerp.modules.compras.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.compras.dto.PedidoCompraRequest;
import com.orionerp.modules.compras.dto.PedidoCompraResponse;
import com.orionerp.modules.compras.service.PedidoCompraService;
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

@RestController
@RequestMapping("/api/v1/compras/pedidos")
@RequiredArgsConstructor
public class PedidoCompraController {

    private final PedidoCompraService pedidoCompraService;

    @GetMapping
    @PreAuthorize("hasAuthority('pedidos_compra:listar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<PedidoCompraResponse>>> list(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) Long filialId,
            @RequestParam(required = false) Long fornecedorId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String term,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(
                pedidoCompraService.list(empresaId, filialId, fornecedorId, status, term, page, size)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('pedidos_compra:listar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PedidoCompraResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(pedidoCompraService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('pedidos_compra:criar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PedidoCompraResponse>> create(
            @Valid @RequestBody PedidoCompraRequest request, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                pedidoCompraService.create(request, auth.getName())));
    }

    @PostMapping("/{id}/aprovar")
    @PreAuthorize("hasAuthority('pedidos_compra:aprovar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PedidoCompraResponse>> aprovar(
            @PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                pedidoCompraService.aprovar(id, auth.getName())));
    }

    @PostMapping("/{id}/reprovar")
    @PreAuthorize("hasAuthority('pedidos_compra:aprovar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PedidoCompraResponse>> reprovar(
            @PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                pedidoCompraService.reprovar(id, auth.getName())));
    }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAuthority('pedidos_compra:cancelar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PedidoCompraResponse>> cancelar(
            @PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                pedidoCompraService.cancelar(id, auth.getName())));
    }
}
