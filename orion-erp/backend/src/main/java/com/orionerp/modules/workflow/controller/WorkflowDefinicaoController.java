package com.orionerp.modules.workflow.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.modules.workflow.dto.WorkflowDefinicaoRequest;
import com.orionerp.modules.workflow.dto.WorkflowDefinicaoResponse;
import com.orionerp.modules.workflow.service.WorkflowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workflow/definicoes")
@RequiredArgsConstructor
public class WorkflowDefinicaoController {

    private final WorkflowService workflowService;

    @GetMapping
    public ApiResponse<List<WorkflowDefinicaoResponse>> listar(@RequestParam Long empresaId) {
        return ApiResponse.ok(workflowService.listDefinicoes(empresaId));
    }

    @GetMapping("/{id}")
    public ApiResponse<WorkflowDefinicaoResponse> buscar(@PathVariable Long id) {
        return ApiResponse.ok(workflowService.buscarDefinicaoPorId(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<WorkflowDefinicaoResponse> criar(@Valid @RequestBody WorkflowDefinicaoRequest request) {
        return ApiResponse.created(workflowService.criarDefinicao(request));
    }

    @PatchMapping("/{id}")
    public ApiResponse<WorkflowDefinicaoResponse> atualizar(@PathVariable Long id,
                                                            @Valid @RequestBody WorkflowDefinicaoRequest request) {
        return ApiResponse.ok(workflowService.atualizarDefinicao(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> desativar(@PathVariable Long id) {
        workflowService.desativarDefinicao(id);
        return ApiResponse.deleted();
    }
}
