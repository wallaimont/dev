package com.orionerp.modules.estoque.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.estoque.dto.ArmazemRequest;
import com.orionerp.modules.estoque.dto.ArmazemResponse;
import com.orionerp.modules.estoque.service.ArmazemService;
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
@RequestMapping("/api/v1/estoque/armazens")
@RequiredArgsConstructor
@Tag(name = "Estoque - Armazens")
public class ArmazemController {

    private final ArmazemService armazemService;

    @GetMapping
    @PreAuthorize("hasAuthority('estoque:listar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<ArmazemResponse>>> list(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) Long filialId,
            @RequestParam(required = false) String term,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(armazemService.list(empresaId, filialId, term, page, size)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('estoque:listar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<ArmazemResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(armazemService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('estoque:criar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<ArmazemResponse>> create(@Valid @RequestBody ArmazemRequest request) {
        return ResponseEntity.ok(ApiResponse.created(armazemService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('estoque:editar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<ArmazemResponse>> update(@PathVariable Long id,
                                                               @Valid @RequestBody ArmazemRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(armazemService.update(id, request), "Armazem atualizado com sucesso"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('estoque:excluir') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        armazemService.delete(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
