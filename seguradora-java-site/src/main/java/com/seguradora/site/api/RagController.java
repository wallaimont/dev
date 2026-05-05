package com.seguradora.site.api;

import com.seguradora.site.rag.LangChain4jRagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rag")
@Tag(name = "RAG", description = "Exemplo completo de RAG com LangChain4j")
public class RagController {

    private final LangChain4jRagService ragService;

    public RagController(LangChain4jRagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping("/ingest")
    @Operation(summary = "Ingerir documento", description = "Recebe um texto, quebra em segmentos e salva embeddings em memória.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Documento ingerido com sucesso", content = @Content(schema = @Schema(implementation = LangChain4jRagService.IngestaoResultado.class))),
            @ApiResponse(responseCode = "400", description = "Payload inválido"),
            @ApiResponse(responseCode = "503", description = "OPENAI_API_KEY ausente")
    })
    public LangChain4jRagService.IngestaoResultado ingest(@Valid @RequestBody IngestRequest request) {
        return ragService.ingerirDocumento(request.titulo(), request.conteudo());
    }

    @PostMapping("/ask")
    @Operation(summary = "Perguntar ao RAG", description = "Busca contexto por similaridade e gera resposta final com LLM.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resposta gerada com sucesso", content = @Content(schema = @Schema(implementation = LangChain4jRagService.ConsultaResultado.class))),
            @ApiResponse(responseCode = "400", description = "Payload inválido"),
            @ApiResponse(responseCode = "503", description = "OPENAI_API_KEY ausente")
    })
    public LangChain4jRagService.ConsultaResultado ask(@Valid @RequestBody AskRequest request) {
        int maxResultados = request.maxResultados() == null ? 4 : request.maxResultados();
        double scoreMinimo = request.scoreMinimo() == null ? 0.55d : request.scoreMinimo();
        return ragService.perguntar(request.pergunta(), maxResultados, scoreMinimo);
    }

    @GetMapping("/status")
    @Operation(summary = "Status do RAG", description = "Retorna quantos documentos e segmentos estão carregados na base em memória.")
    public LangChain4jRagService.StatusRag status() {
        return ragService.status();
    }

    public record IngestRequest(
            @NotBlank(message = "Titulo é obrigatório")
            String titulo,
            @NotBlank(message = "Conteudo é obrigatório")
            String conteudo
    ) {
    }

    public record AskRequest(
            @NotBlank(message = "Pergunta é obrigatória")
            String pergunta,
            @Min(value = 1, message = "maxResultados deve ser maior ou igual a 1")
            @Max(value = 10, message = "maxResultados deve ser menor ou igual a 10")
            Integer maxResultados,
            @DecimalMin(value = "0.0", message = "scoreMinimo deve estar entre 0.0 e 1.0")
            @DecimalMax(value = "1.0", message = "scoreMinimo deve estar entre 0.0 e 1.0")
            Double scoreMinimo
    ) {
    }
}