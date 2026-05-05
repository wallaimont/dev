package com.seguradora.site.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ia")
@Tag(name = "IA", description = "Operações de chat com Spring AI")
public class AiChatController {

    private static final Logger log = LoggerFactory.getLogger(AiChatController.class);

    private final ChatClient chatClient;

    public AiChatController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @PostMapping("/chat")
    @Operation(summary = "Conversar com IA", description = "Recebe um prompt e retorna a resposta do modelo configurado no Spring AI.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resposta gerada com sucesso", content = @Content(schema = @Schema(implementation = AiChatResponse.class))),
            @ApiResponse(responseCode = "400", description = "Prompt inválido"),
            @ApiResponse(responseCode = "502", description = "Falha ao consultar o provedor de IA")
    })
    public AiChatResponse chat(@Valid @RequestBody AiChatRequest request) {
        try {
            String resposta = chatClient.prompt()
                    .user(request.prompt())
                    .call()
                    .content();

            return new AiChatResponse(resposta);
        } catch (Exception ex) {
            log.error("Erro ao consultar IA: {}", ex.getMessage(), ex);
            throw new AiProviderException("Falha ao consultar provedor de IA. Verifique OPENAI_API_KEY e tente novamente.");
        }
    }

    public record AiChatRequest(
            @NotBlank(message = "Prompt é obrigatório")
            String prompt
    ) {
    }

    public record AiChatResponse(String resposta) {
    }

    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    private static class AiProviderException extends RuntimeException {
        private AiProviderException(String message) {
            super(message);
        }
    }
}
