package com.sigaseguros.controller;

import com.sigaseguros.dto.ApiResponse;
import com.sigaseguros.dto.UsuarioDTO;
import com.sigaseguros.enums.Perfil;
import com.sigaseguros.service.UsuarioService;
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

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    @Operation(summary = "Listar usuários")
    public ResponseEntity<ApiResponse<Page<UsuarioDTO>>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Perfil perfil,
            @PageableDefault(size = 20, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(usuarioService.listar(nome, email, perfil, pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID")
    public ResponseEntity<ApiResponse<UsuarioDTO>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(usuarioService.buscarPorId(id)));
    }

    @PostMapping
    @Operation(summary = "Criar usuário")
    public ResponseEntity<ApiResponse<UsuarioDTO>> criar(@Valid @RequestBody UsuarioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(usuarioService.criar(dto), "Usuário criado com sucesso"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário")
    public ResponseEntity<ApiResponse<UsuarioDTO>> atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok(usuarioService.atualizar(id, dto), "Usuário atualizado com sucesso"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inativar usuário")
    public ResponseEntity<ApiResponse<Void>> inativar(@PathVariable Long id) {
        usuarioService.inativar(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Usuário inativado com sucesso"));
    }

    @PatchMapping("/{id}/ativar")
    @Operation(summary = "Ativar usuário")
    public ResponseEntity<ApiResponse<Void>> ativar(@PathVariable Long id) {
        usuarioService.ativar(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Usuário ativado com sucesso"));
    }

    @PatchMapping("/{id}/resetar-senha")
    @Operation(summary = "Resetar senha do usuário")
    public ResponseEntity<ApiResponse<Void>> resetarSenha(@PathVariable Long id) {
        usuarioService.resetarSenha(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Senha resetada com sucesso"));
    }
}
