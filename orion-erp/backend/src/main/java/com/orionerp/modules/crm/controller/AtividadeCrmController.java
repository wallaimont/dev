package com.orionerp.modules.crm.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.modules.crm.dto.AtividadeCrmRequest;
import com.orionerp.modules.crm.dto.AtividadeCrmResponse;
import com.orionerp.modules.crm.service.AtividadeCrmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/crm/atividades")
@RequiredArgsConstructor
public class AtividadeCrmController {

    private final AtividadeCrmService atividadeCrmService;

    @GetMapping("/lead/{leadId}")
    public ResponseEntity<ApiResponse<List<AtividadeCrmResponse>>> listarPorLead(
            @PathVariable Long leadId, @RequestParam Long empresaId) {
        return ResponseEntity.ok(ApiResponse.ok(atividadeCrmService.listarPorLead(empresaId, leadId)));
    }

    @GetMapping("/oportunidade/{oportunidadeId}")
    public ResponseEntity<ApiResponse<List<AtividadeCrmResponse>>> listarPorOportunidade(
            @PathVariable Long oportunidadeId, @RequestParam Long empresaId) {
        return ResponseEntity.ok(ApiResponse.ok(atividadeCrmService.listarPorOportunidade(empresaId, oportunidadeId)));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<ApiResponse<List<AtividadeCrmResponse>>> listarPorCliente(
            @PathVariable Long clienteId, @RequestParam Long empresaId) {
        return ResponseEntity.ok(ApiResponse.ok(atividadeCrmService.listarPorCliente(empresaId, clienteId)));
    }

    @GetMapping("/pendentes")
    public ResponseEntity<ApiResponse<List<AtividadeCrmResponse>>> listarPendentes(
            @RequestParam Long empresaId, @RequestParam Long responsavelId) {
        return ResponseEntity.ok(ApiResponse.ok(atividadeCrmService.listarPendentes(empresaId, responsavelId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AtividadeCrmResponse>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(atividadeCrmService.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AtividadeCrmResponse>> criar(
            @Valid @RequestBody AtividadeCrmRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(atividadeCrmService.criar(request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<AtividadeCrmResponse>> atualizar(
            @PathVariable Long id, @Valid @RequestBody AtividadeCrmRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(atividadeCrmService.atualizar(id, request)));
    }

    @PatchMapping("/{id}/concluir")
    public ResponseEntity<ApiResponse<Void>> concluir(@PathVariable Long id) {
        atividadeCrmService.concluir(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
