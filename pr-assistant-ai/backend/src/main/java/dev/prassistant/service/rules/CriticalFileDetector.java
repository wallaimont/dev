package dev.prassistant.service.rules;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class CriticalFileDetector {

    private static final Map<Pattern, String> PATTERNS = new LinkedHashMap<>();

    static {
        PATTERNS.put(Pattern.compile("(?i)(docker-compose|Dockerfile)"), "Infrastructure file");
        PATTERNS.put(Pattern.compile("(?i)\\.github/workflows/"), "CI/CD pipeline");
        PATTERNS.put(Pattern.compile("(?i)(application\\.ya?ml|application\\.properties)"), "App configuration");
        PATTERNS.put(Pattern.compile("(?i)\\.env"), "Environment secrets");
        PATTERNS.put(Pattern.compile("(?i)(pom\\.xml|build\\.gradle|package\\.json)"), "Build descriptor");
        PATTERNS.put(Pattern.compile("(?i)(SecurityConfig|WebSecurityConfig|AuthConfig)"), "Security configuration");
        PATTERNS.put(Pattern.compile("(?i)migration|flyway|liquibase|V\\d+__"), "Database migration");
        PATTERNS.put(Pattern.compile("(?i)(schema\\.sql|data\\.sql)"), "Database schema");
        PATTERNS.put(Pattern.compile("(?i)helm/|k8s/|kubernetes/"), "Kubernetes manifest");
        PATTERNS.put(Pattern.compile("(?i)terraform/|\\.tf$"), "Terraform IaC");
    }

    public record CriticalResult(boolean critical, String reason) {}

    public CriticalResult check(String filePath) {
        for (Map.Entry<Pattern, String> entry : PATTERNS.entrySet()) {
            if (entry.getKey().matcher(filePath).find()) {
                return new CriticalResult(true, entry.getValue());
            }
        }
        return new CriticalResult(false, null);
    }
}
