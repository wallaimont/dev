package dev.prassistant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.prassistant.config.SecurityConfig;
import dev.prassistant.security.CustomUserDetailsService;
import dev.prassistant.security.JwtProvider;
import dev.prassistant.service.GitHubClient;
import dev.prassistant.service.PullRequestAnalysisService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WebhookController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class WebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GitHubClient gitHubClient;

    @MockBean
    private PullRequestAnalysisService analysisService;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private static final String VALID_PAYLOAD = """
            {
              "action": "opened",
              "pull_request": {
                "number": 42,
                "title": "Add feature X",
                "body": "Implements feature X",
                "state": "open",
                "user": { "login": "dev-user" },
                "head": { "ref": "feature/x" },
                "base": { "ref": "main" },
                "changed_files": 3,
                "additions": 50,
                "deletions": 10
              },
              "repository": {
                "id": 12345,
                "name": "test-repo",
                "full_name": "org/test-repo",
                "owner": { "login": "org" }
              }
            }
            """;

    @Test
    @DisplayName("POST /api/webhooks/github - valid PR opened webhook triggers analysis")
    void handleWebhook_validPrOpened() throws Exception {
        when(gitHubClient.verifySignature(anyString(), anyString())).thenReturn(true);

        mockMvc.perform(post("/api/webhooks/github")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PAYLOAD)
                        .header("X-Hub-Signature-256", "sha256=abc")
                        .header("X-GitHub-Event", "pull_request"))
                .andExpect(status().isAccepted());

        verify(analysisService).processWebhook(
                eq("org"), eq("test-repo"), eq(42L),
                eq("Add feature X"), eq("Implements feature X"),
                eq("dev-user"), eq("feature/x"), eq("main")
        );
    }

    @Test
    @DisplayName("POST /api/webhooks/github - invalid signature returns error")
    void handleWebhook_invalidSignature() throws Exception {
        when(gitHubClient.verifySignature(anyString(), anyString())).thenReturn(false);

        mockMvc.perform(post("/api/webhooks/github")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PAYLOAD)
                        .header("X-Hub-Signature-256", "sha256=invalid")
                        .header("X-GitHub-Event", "pull_request"))
                .andExpect(status().is4xxClientError());

        verify(analysisService, never()).processWebhook(any(), any(), anyLong(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("POST /api/webhooks/github - non-PR event returns 200 without processing")
    void handleWebhook_nonPrEvent() throws Exception {
        when(gitHubClient.verifySignature(anyString(), anyString())).thenReturn(true);

        mockMvc.perform(post("/api/webhooks/github")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PAYLOAD)
                        .header("X-Hub-Signature-256", "sha256=abc")
                        .header("X-GitHub-Event", "push"))
                .andExpect(status().isOk());

        verify(analysisService, never()).processWebhook(any(), any(), anyLong(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("POST /api/webhooks/github - PR closed action ignores processing")
    void handleWebhook_prClosedAction() throws Exception {
        when(gitHubClient.verifySignature(anyString(), anyString())).thenReturn(true);

        String closedPayload = VALID_PAYLOAD.replace("\"opened\"", "\"closed\"");

        mockMvc.perform(post("/api/webhooks/github")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(closedPayload)
                        .header("X-Hub-Signature-256", "sha256=abc")
                        .header("X-GitHub-Event", "pull_request"))
                .andExpect(status().isOk());

        verify(analysisService, never()).processWebhook(any(), any(), anyLong(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("POST /api/webhooks/github - synchronize action triggers analysis")
    void handleWebhook_synchronizeAction() throws Exception {
        when(gitHubClient.verifySignature(anyString(), anyString())).thenReturn(true);

        String syncPayload = VALID_PAYLOAD.replace("\"opened\"", "\"synchronize\"");

        mockMvc.perform(post("/api/webhooks/github")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(syncPayload)
                        .header("X-Hub-Signature-256", "sha256=abc")
                        .header("X-GitHub-Event", "pull_request"))
                .andExpect(status().isAccepted());

        verify(analysisService).processWebhook(
                eq("org"), eq("test-repo"), eq(42L),
                eq("Add feature X"), eq("Implements feature X"),
                eq("dev-user"), eq("feature/x"), eq("main")
        );
    }
}
