package com.sigaseguros.controller;

import com.sigaseguros.dto.ApiResponse;
import com.sigaseguros.dto.CorretoraDTO;
import com.sigaseguros.service.CorretoraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/corretoras")
@RequiredArgsConstructor
@Tag(name = "Corretoras")
public class CorretoraController {

    private final CorretoraService corretoraService;

    @GetMapping
    @Operation(summary = "Listar corretoras paginado")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','COMERCIAL','OPERADOR')")
    public ResponseEntity<ApiResponse<Page<CorretoraDTO>>> listar(
            @RequestParam(required = false) String nome,
            @PageableDefault(size = 20, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(corretoraService.listar(nome, pageable)));
    }

    @GetMapping("/todas")
    @Operation(summary = "Listar todas as corretoras (para combos)")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<CorretoraDTO>>> listarTodas() {
        return ResponseEntity.ok(ApiResponse.ok(corretoraService.listarTodas()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar corretora por ID")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','COMERCIAL','OPERADOR')")
    public ResponseEntity<ApiResponse<CorretoraDTO>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(corretoraService.buscarPorId(id)));
    }

    @PostMapping
    @Operation(summary = "Criar corretora")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<ApiResponse<CorretoraDTO>> criar(@Valid @RequestBody CorretoraDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(corretoraService.criar(dto), "Corretora criada com sucesso"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar corretora")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<ApiResponse<CorretoraDTO>> atualizar(@PathVariable Long id, @Valid @RequestBody CorretoraDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok(corretoraService.atualizar(id, dto), "Corretora atualizada com sucesso"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inativar corretora")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> inativar(@PathVariable Long id) {
        corretoraService.inativar(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Corretora inativada com sucesso"));
    }
}
