package com.seguradora.site.api;

import com.seguradora.site.model.LeadRequest;
import com.seguradora.site.service.ProtheusMockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mock/protheus")
@Tag(name = "Protheus Mock", description = "Mock local para testes de integração com Protheus")
@SecurityRequirement(name = "bearerAuth")
public class ProtheusMockController {

    private final ProtheusMockService protheusMockService;

    public ProtheusMockController(ProtheusMockService protheusMockService) {
        this.protheusMockService = protheusMockService;
    }

    @GetMapping("/health")
    @Operation(summary = "Health check do mock", description = "Retorna o status do mock local do Protheus.")
    public ProtheusMockHealthResponse health() {
        return protheusMockService.health();
    }

    @PostMapping("/leads")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Enviar lead ao mock", description = "Recebe um lead de teste e simula o aceite pelo Protheus.")
    public ProtheusMockLeadResponse sendLead(@Valid @RequestBody LeadRequest leadRequest) {
        if (leadRequest.getOrigem() == null || leadRequest.getOrigem().isBlank()) {
            leadRequest.setOrigem("integracao-teste");
        }
        return protheusMockService.receiveLead(leadRequest);
    }

    @GetMapping("/leads")
    @Operation(summary = "Listar leads recebidos", description = "Lista os leads recebidos pelo mock local do Protheus.")
    public List<ProtheusMockLeadResponse> listLeads() {
        return protheusMockService.listReceivedLeads();
    }

    @DeleteMapping("/leads")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Limpar leads do mock", description = "Remove todos os leads de teste recebidos pelo mock local do Protheus.")
    public void clearLeads() {
        protheusMockService.clear();
    }
}