package com.orionerp.modules.workflow.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.modules.workflow.dto.WorkflowAprovacaoResponse;
import com.orionerp.modules.workflow.dto.WorkflowDecisaoRequest;
import com.orionerp.modules.workflow.service.WorkflowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workflow/aprovacoes")
@RequiredArgsConstructor
public class WorkflowAprovacaoController {

    private final WorkflowService workflowService;

    @GetMapping("/pendentes")
    public ApiResponse<List<WorkflowAprovacaoResponse>> pendentes(@RequestParam Long aprovadorId) {
        return ApiResponse.ok(workflowService.listarPendentes(aprovadorId));
    }

    @GetMapping("/entidade")
    public ApiResponse<List<WorkflowAprovacaoResponse>> porEntidade(@RequestParam String entidade,
                                                                    @RequestParam Long entidadeId) {
        return ApiResponse.ok(workflowService.listarPorEntidade(entidade, entidadeId));
    }

    @PostMapping("/{id}/decidir")
    public ApiResponse<WorkflowAprovacaoResponse> decidir(@PathVariable Long id,
                                                          @Valid @RequestBody WorkflowDecisaoRequest request) {
        return ApiResponse.ok(workflowService.decidir(id, request.aprovadorId(),
                request.decisao(), request.justificativa()));
    }
}
