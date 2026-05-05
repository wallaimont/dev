package com.sigaseguros.controller;

import com.sigaseguros.dto.ApiResponse;
import com.sigaseguros.entity.AuditoriaLog;
import com.sigaseguros.repository.AuditoriaLogRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auditoria")
@RequiredArgsConstructor
@Tag(name = "Auditoria")
public class AuditoriaController {

    private final AuditoriaLogRepository auditoriaLogRepository;

    @GetMapping
    @Operation(summary = "Listar logs de auditoria")
    @PreAuthorize("hasAnyRole('ADMIN','AUDITOR')")
    public ResponseEntity<ApiResponse<Page<AuditoriaLog>>> listar(
            @RequestParam(required = false) String entidade,
            @RequestParam(required = false) Long entidadeId,
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String acao,
            @PageableDefault(size = 50, sort = "dataHora", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AuditoriaLog> page;
        if (entidade != null && entidadeId != null) {
            page = auditoriaLogRepository.findByEntidadeAndEntidadeIdOrderByDataHoraDesc(entidade, entidadeId, pageable);
        } else {
            page = auditoriaLogRepository.findAllWithFilters(entidade, usuario, acao, null, null, pageable);
        }
        return ResponseEntity.ok(ApiResponse.ok(page));
    }
}
