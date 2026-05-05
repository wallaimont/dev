package com.orionerp.modules.seguros.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.seguros.dto.NotificacaoSeguroResponse;
import com.orionerp.modules.seguros.service.NotificacaoSeguroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/seguros/notificacoes")
@RequiredArgsConstructor
public class NotificacaoSeguroController {

    private final NotificacaoSeguroService service;

    @GetMapping
    @PreAuthorize("hasAuthority('notificacoes_seguro:listar')")
    public ResponseEntity<ApiResponse<PageResponse<NotificacaoSeguroResponse>>> list(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) Boolean lida,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(service.list(empresaId, lida, page, size)));
    }

    @PatchMapping("/{id}/lida")
    @PreAuthorize("hasAuthority('notificacoes_seguro:editar')")
    public ResponseEntity<ApiResponse<Void>> marcarComoLida(@PathVariable Long id) {
        service.marcarComoLida(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Notificação marcada como lida"));
    }
}
