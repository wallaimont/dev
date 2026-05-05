package com.supportdesk.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Jwt jwt = new Jwt();
    private Storage storage = new Storage();
    private Cors cors = new Cors();
    private BootstrapAdmin bootstrapAdmin = new BootstrapAdmin();
    private Sla sla = new Sla();
    private Outbox outbox = new Outbox();

    @Data
    public static class Jwt {
        private String privateKeyPath = "classpath:keys/private-key.pem";
        private String publicKeyPath = "classpath:keys/public-key.pem";
        private long accessTokenExpirationMs = 900_000L;
        private long refreshTokenExpirationMs = 604_800_000L;
        private String issuer = "supportdesk-pro-x";
    }

    @Data
    public static class Storage {
        private String type = "local";
        private String uploadDir = "./uploads";
        private long maxFileSizeBytes = 10_485_760L; // 10 MB
        private List<String> allowedMimeTypes = List.of(
                "image/jpeg", "image/png", "image/gif",
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "text/plain");
    }

    @Data
    public static class Cors {
        private List<String> allowedOrigins = List.of("http://localhost:4200");
    }

    @Data
    public static class BootstrapAdmin {
        private boolean enabled = true;
        private String name = "System Administrator";
        private String email = "admin@supportdesk.com";
        private String password = "Admin@2024!";
        private boolean forcePasswordResetOnStartup = false;
    }

    @Data
    public static class Sla {
        /** Default SLA in hours per priority: LOW, MEDIUM, HIGH, CRITICAL */
        private int lowHours = 72;
        private int mediumHours = 48;
        private int highHours = 24;
        private int criticalHours = 4;
    }

    @Data
    public static class Outbox {
        private int maxRetryAttempts = 3;
        private long pollingIntervalMs = 5000L;
    }
}
