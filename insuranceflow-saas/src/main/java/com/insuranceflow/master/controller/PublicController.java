package com.insuranceflow.master.controller;

import com.insuranceflow.common.dto.ApiResponse;
import com.insuranceflow.master.dto.*;
import com.insuranceflow.master.model.*;
import com.insuranceflow.master.repository.PlanoRepository;
import com.insuranceflow.master.service.MasterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@Tag(name = "Público", description = "Endpoints públicos da plataforma")
public class PublicController {

    private final PlanoRepository planoRepo;
    private final MasterService masterService;

    @GetMapping("/planos")
    @Operation(summary = "Listar planos disponíveis (público)")
    public ResponseEntity<ApiResponse<List<Plano>>> listarPlanos() {
        return ResponseEntity.ok(ApiResponse.ok(planoRepo.findByActiveTrue()));
    }

    @PostMapping("/leads")
    @Operation(summary = "Capturar lead da landing page")
    public ResponseEntity<ApiResponse<LeadSaas>> criarLead(@Valid @RequestBody LeadSaasRequest request) {
        return ResponseEntity.ok(ApiResponse.created(masterService.criarLead(request), "Obrigado pelo interesse! Entraremos em contato em breve."));
    }

    @PostMapping("/onboarding")
    @Operation(summary = "Cadastro de nova empresa (teste grátis)")
    public ResponseEntity<ApiResponse<Empresa>> onboarding(@Valid @RequestBody OnboardingRequest request) {
        return ResponseEntity.ok(ApiResponse.created(masterService.onboarding(request), "Empresa cadastrada com sucesso! Bem-vindo ao InsuranceFlow."));
    }
}
