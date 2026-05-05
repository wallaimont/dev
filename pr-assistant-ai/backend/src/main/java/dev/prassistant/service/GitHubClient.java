package dev.prassistant.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.prassistant.domain.entity.PullRequestFile;
import dev.prassistant.domain.enums.FileStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GitHubClient {

    private static final Logger log = LoggerFactory.getLogger(GitHubClient.class);

    private final WebClient.Builder webClientBuilder;

    @Value("${app.github.token}")
    private String githubToken;

    @Value("${app.github.webhook-secret}")
    private String webhookSecret;

    public boolean verifySignature(String payload, String signature) {
        if (signature == null || !signature.startsWith("sha256=")) return false;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            String expected = "sha256=" + HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
            return MessageDigest.isEqual(expected.getBytes(), signature.getBytes());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Signature verification failed", e);
            return false;
        }
    }

    public List<PullRequestFile> fetchPrFiles(String owner, String repo, long prNumber) {
        List<GhFile> ghFiles = webClientBuilder.build()
                .get()
                .uri("https://api.github.com/repos/{owner}/{repo}/pulls/{number}/files", owner, repo, prNumber)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<GhFile>>() {})
                .block();

        if (ghFiles == null) return List.of();

        return ghFiles.stream().map(f -> {
            PullRequestFile file = new PullRequestFile();
            file.setFilePath(f.filename());
            file.setFileStatus(mapStatus(f.status()));
            file.setAdditions(f.additions());
            file.setDeletions(f.deletions());
            return file;
        }).toList();
    }

    public void postComment(String owner, String repo, long prNumber, String body) {
        webClientBuilder.build()
                .post()
                .uri("https://api.github.com/repos/{owner}/{repo}/issues/{number}/comments", owner, repo, prNumber)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .bodyValue(java.util.Map.of("body", body))
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    private FileStatus mapStatus(String status) {
        return switch (status) {
            case "added" -> FileStatus.ADDED;
            case "removed" -> FileStatus.REMOVED;
            case "renamed" -> FileStatus.RENAMED;
            default -> FileStatus.MODIFIED;
        };
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record GhFile(
            String filename,
            String status,
            int additions,
            int deletions,
            @JsonProperty("patch") String patch
    ) {}
}
