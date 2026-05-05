package com.orionerp.modules.estoque.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.estoque.dto.InventarioRequest;
import com.orionerp.modules.estoque.dto.InventarioResponse;
import com.orionerp.modules.estoque.service.InventarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/estoque/inventarios")
@RequiredArgsConstructor
@Tag(name = "Estoque - Inventarios")
public class InventarioController {

    private final InventarioService inventarioService;

    @GetMapping
    @PreAuthorize("hasAuthority('inventariosrios:listar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<InventarioResponse>>> list(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) Long filialId,
            @RequestParam(required = false) Long armazemId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String term,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(
                inventarioService.list(empresaId, filialId, armazemId, status, term, page, size)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('inventariosrios:listar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<InventarioResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(inventarioService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('inventariosrios:criar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<InventarioResponse>> create(@Valid @RequestBody InventarioRequest request) {
        return ResponseEntity.ok(ApiResponse.created(inventarioService.create(request)));
    }

    @PatchMapping("/{id}/itens/{itemId}/contagem")
    @PreAuthorize("hasAuthority('estoque:editar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<InventarioResponse>> registrarContagem(
            @PathVariable Long id,
            @PathVariable Long itemId,
            @RequestParam BigDecimal quantidadeContada) {
        return ResponseEntity.ok(ApiResponse.ok(
                inventarioService.registrarContagem(id, itemId, quantidadeContada),
                "Contagem registrada com sucesso"));
    }

    @PostMapping("/{id}/finalizar")
    @PreAuthorize("hasAuthority('estoque:finalizar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<InventarioResponse>> finalizar(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok(
                inventarioService.finalizar(id, userDetails.getUsername()),
                "Inventario finalizado com sucesso"));
    }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAuthority('estoque:excluir') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> cancelar(@PathVariable Long id) {
        inventarioService.cancelar(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Inventario cancelado com sucesso"));
    }
}
