package com.orionerp.modules.administration.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.administration.dto.FilialRequest;
import com.orionerp.modules.administration.dto.FilialResponse;
import com.orionerp.modules.administration.service.FilialService;
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
@RequestMapping("/api/v1/administracao/filiais")
@RequiredArgsConstructor
@Tag(name = "Administracao - Filiais")
public class FilialController {

    private final FilialService filialService;

    @GetMapping
    @PreAuthorize("hasAuthority('filiais:listar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<FilialResponse>>> list(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) String term,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(filialService.list(empresaId, term, page, size)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('filiais:listar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<FilialResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(filialService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('filiais:criar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<FilialResponse>> create(@Valid @RequestBody FilialRequest request) {
        return ResponseEntity.ok(ApiResponse.created(filialService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('filiais:editar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<FilialResponse>> update(@PathVariable Long id,
                                                              @Valid @RequestBody FilialRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(filialService.update(id, request), "Filial atualizada com sucesso"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('filiais:excluir') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        filialService.delete(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
