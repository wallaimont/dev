package com.sigaseguros.controller;

import com.sigaseguros.dto.ApiResponse;
import com.sigaseguros.dto.ClienteDTO;
import com.sigaseguros.enums.TipoPessoa;
import com.sigaseguros.service.ClienteService;
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
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes")
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    @Operation(summary = "Listar clientes")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','COMERCIAL','OPERADOR')")
    public ResponseEntity<ApiResponse<Page<ClienteDTO>>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cpfCnpj,
            @RequestParam(required = false) TipoPessoa tipoPessoa,
            @RequestParam(required = false) String email,
            @PageableDefault(size = 20, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(clienteService.listar(nome, cpfCnpj, tipoPessoa, email, pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','COMERCIAL','OPERADOR')")
    public ResponseEntity<ApiResponse<ClienteDTO>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(clienteService.buscarPorId(id)));
    }

    @PostMapping
    @Operation(summary = "Criar cliente")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','COMERCIAL')")
    public ResponseEntity<ApiResponse<ClienteDTO>> criar(@Valid @RequestBody ClienteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(clienteService.criar(dto), "Cliente criado com sucesso"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cliente")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR','COMERCIAL')")
    public ResponseEntity<ApiResponse<ClienteDTO>> atualizar(@PathVariable Long id, @Valid @RequestBody ClienteDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok(clienteService.atualizar(id, dto), "Cliente atualizado com sucesso"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inativar cliente")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<ApiResponse<Void>> inativar(@PathVariable Long id) {
        clienteService.inativar(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Cliente inativado com sucesso"));
    }
}
