package com.orionerp.modules.administration.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.administration.dto.EmpresaRequest;
import com.orionerp.modules.administration.dto.EmpresaResponse;
import com.orionerp.modules.administration.service.EmpresaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/administracao/empresas")
@RequiredArgsConstructor
@Tag(name = "Administracao - Empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    @GetMapping
    @PreAuthorize("hasAuthority('empresas:listar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<EmpresaResponse>>> list(
            @RequestParam(required = false) String term,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(empresaService.list(term, page, size)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('empresas:listar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<EmpresaResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(empresaService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('empresas:criar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<EmpresaResponse>> create(@Valid @RequestBody EmpresaRequest request) {
        return ResponseEntity.ok(ApiResponse.created(empresaService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('empresas:editar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<EmpresaResponse>> update(@PathVariable Long id,
                                                               @Valid @RequestBody EmpresaRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(empresaService.update(id, request), "Empresa atualizada com sucesso"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('empresas:excluir') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        empresaService.delete(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
