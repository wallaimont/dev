package dev.prassistant.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WebhookPayload(
        String action,
        @JsonProperty("pull_request") PrData pullRequest,
        Repository repository
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PrData(
            long id,
            long number,
            String title,
            String body,
            String state,
            User user,
            Head head,
            Base base,
            @JsonProperty("changed_files") int changedFiles,
            int additions,
            int deletions
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record User(String login) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Head(String ref) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Base(String ref) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Repository(
            long id,
            String name,
            @JsonProperty("full_name") String fullName,
            Owner owner
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Owner(String login) {}
}
