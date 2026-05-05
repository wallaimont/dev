package dev.prassistant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.prassistant.dto.WebhookPayload;
import dev.prassistant.exception.WebhookSignatureException;
import dev.prassistant.service.GitHubClient;
import dev.prassistant.service.PullRequestAnalysisService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);

    private final GitHubClient gitHubClient;
    private final PullRequestAnalysisService analysisService;
    private final ObjectMapper objectMapper;

    @PostMapping("/github")
    public ResponseEntity<Void> handleGitHubWebhook(
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature,
            @RequestHeader(value = "X-GitHub-Event", required = false) String event,
            @RequestBody String rawPayload) throws Exception {

        if (!gitHubClient.verifySignature(rawPayload, signature)) {
            throw new WebhookSignatureException("Invalid webhook signature");
        }

        if (!"pull_request".equals(event)) {
            log.info("Ignoring non-PR event: {}", event);
            return ResponseEntity.ok().build();
        }

        WebhookPayload payload = objectMapper.readValue(rawPayload, WebhookPayload.class);

        if (!"opened".equals(payload.action()) && !"synchronize".equals(payload.action())) {
            log.info("Ignoring PR action: {}", payload.action());
            return ResponseEntity.ok().build();
        }

        analysisService.processWebhook(
                payload.repository().owner().login(),
                payload.repository().name(),
                payload.pullRequest().number(),
                payload.pullRequest().title(),
                payload.pullRequest().body(),
                payload.pullRequest().user().login(),
                payload.pullRequest().head().ref(),
                payload.pullRequest().base().ref()
        );

        return ResponseEntity.accepted().build();
    }
}
