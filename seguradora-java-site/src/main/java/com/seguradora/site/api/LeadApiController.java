package com.seguradora.site.api;

import com.seguradora.site.model.LeadRequest;
import com.seguradora.site.model.QuoteResult;
import com.seguradora.site.service.LeadStorageService;
import com.seguradora.site.service.QuoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Leads", description = "Operações de contato, cotação e consulta de leads da seguradora")
@SecurityRequirement(name = "bearerAuth")
public class LeadApiController {

    private final LeadStorageService leadStorageService;
    private final QuoteService quoteService;

    public LeadApiController(LeadStorageService leadStorageService, QuoteService quoteService) {
        this.leadStorageService = leadStorageService;
        this.quoteService = quoteService;
    }

    @GetMapping("/leads")
    @Operation(summary = "Listar leads", description = "Retorna todos os leads cadastrados no banco H2 em ordem decrescente de criação.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leads retornados com sucesso")
    })
    public List<LeadApiResponse> listLeads() {
        return leadStorageService.findAllRecords().stream()
                .map(LeadApiResponse::from)
                .toList();
    }

    @PostMapping("/contatos")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar contato", description = "Recebe um lead de contato via JSON e persiste no banco.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Contato criado com sucesso", content = @Content(schema = @Schema(implementation = LeadApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Payload inválido")
    })
    public LeadApiResponse createContato(@Valid @RequestBody LeadRequest leadRequest) {
        leadRequest.setOrigem("contato");
        return LeadApiResponse.from(leadStorageService.save(leadRequest));
    }

    @PostMapping("/cotacoes")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Gerar cotação", description = "Calcula uma cotação inicial, salva o lead e retorna os valores estimados.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cotação gerada com sucesso", content = @Content(schema = @Schema(implementation = QuoteApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Payload inválido")
    })
    public QuoteApiResponse createCotacao(@Valid @RequestBody LeadRequest leadRequest) {
        leadRequest.setOrigem("cotacao");
        QuoteResult quoteResult = quoteService.generate(leadRequest);
        leadStorageService.save(leadRequest, quoteResult);
        return QuoteApiResponse.from(quoteResult);
    }
}