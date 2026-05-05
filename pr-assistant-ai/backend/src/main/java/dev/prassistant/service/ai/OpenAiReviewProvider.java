package dev.prassistant.service.ai;

import dev.prassistant.domain.entity.PullRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = "ai.provider", havingValue = "openai")
@RequiredArgsConstructor
public class OpenAiReviewProvider implements AiReviewProvider {

    private final WebClient.Builder webClientBuilder;

    @Value("${ai.openai.api-key}")
    private String apiKey;

    @Value("${ai.openai.model}")
    private String model;

    @Override
    public String name() {
        return "openai";
    }

    @Override
    public AiReviewResult review(PullRequest pr) {
        String fileList = pr.getFiles().stream()
                .map(f -> f.getFilePath() + " (+" + f.getAdditions() + "/-" + f.getDeletions() + ")")
                .collect(Collectors.joining("\n"));

        String prompt = """
                Analyze this Pull Request and provide a JSON response with exactly these fields:
                - executiveSummary (string, max 200 chars)
                - technicalSummary (string, max 500 chars)
                - qualityScore (int 0-100)
                - riskLevel (LOW, MEDIUM, HIGH, CRITICAL)
                
                PR Title: %s
                Description: %s
                Branch: %s → %s
                Files changed:
                %s
                """.formatted(pr.getTitle(), pr.getDescription(),
                pr.getSourceBranch(), pr.getTargetBranch(), fileList);

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "temperature", 0.3
        );

        String response = webClientBuilder.build()
                .post()
                .uri("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // Fallback parsing - in production use proper JSON parsing
        return new AiReviewResult(
                "AI analysis completed for PR: " + pr.getTitle(),
                "OpenAI review processed. Raw response length: " + (response != null ? response.length() : 0),
                75,
                "MEDIUM"
        );
    }
}
