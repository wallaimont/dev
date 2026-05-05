package com.orionerp.modules.administration.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.administration.dto.PermissaoResponse;
import com.orionerp.modules.administration.service.PermissaoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/administracao/permissoes")
@RequiredArgsConstructor
@Tag(name = "Administracao - Permissoes")
public class PermissaoController {

    private final PermissaoService permissaoService;

    @GetMapping
    @PreAuthorize("hasAuthority('perfis:listar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<PermissaoResponse>>> list(
            @RequestParam(required = false) String modulo,
            @RequestParam(required = false) String term,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(ApiResponse.ok(permissaoService.list(modulo, term, page, size)));
    }
}
