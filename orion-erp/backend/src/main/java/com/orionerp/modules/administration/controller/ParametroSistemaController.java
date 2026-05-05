package com.orionerp.modules.administration.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.administration.dto.ParametroSistemaRequest;
import com.orionerp.modules.administration.dto.ParametroSistemaResponse;
import com.orionerp.modules.administration.service.ParametroSistemaService;
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
@RequestMapping("/api/v1/administracao/parametros")
@RequiredArgsConstructor
@Tag(name = "Administracao - Parametros")
public class ParametroSistemaController {

    private final ParametroSistemaService parametroSistemaService;

    @GetMapping
    @PreAuthorize("hasAuthority('parametros:listar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<ParametroSistemaResponse>>> list(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) Long filialId,
            @RequestParam(required = false) String modulo,
            @RequestParam(required = false) String term,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(
                parametroSistemaService.list(empresaId, filialId, modulo, term, page, size)
        ));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('parametros:listar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<ParametroSistemaResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(parametroSistemaService.getById(id)));
    }

    @GetMapping("/resolve")
    @PreAuthorize("hasAuthority('parametros:listar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<ParametroSistemaResponse>> resolve(
            @RequestParam String chave,
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) Long filialId) {
        return ResponseEntity.ok(ApiResponse.ok(parametroSistemaService.resolve(chave, empresaId, filialId)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('parametros:editar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<ParametroSistemaResponse>> create(@Valid @RequestBody ParametroSistemaRequest request) {
        return ResponseEntity.ok(ApiResponse.created(parametroSistemaService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('parametros:editar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<ParametroSistemaResponse>> update(@PathVariable Long id,
                                                                        @Valid @RequestBody ParametroSistemaRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(parametroSistemaService.update(id, request), "Parametro atualizado com sucesso"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('parametros:editar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        parametroSistemaService.delete(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }
}
