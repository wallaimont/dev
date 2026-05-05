package com.supportdesk.health;

import com.supportdesk.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class StorageHealthIndicator implements HealthIndicator {

    private final AppProperties appProperties;

    @Override
    public Health health() {
        try {
            Path uploadDir = Path.of(appProperties.getStorage().getUploadDir());
            Files.createDirectories(uploadDir);
            boolean writable = Files.isWritable(uploadDir);
            return writable
                    ? Health.up().withDetail("uploadDir", uploadDir.toAbsolutePath().toString()).build()
                    : Health.down().withDetail("uploadDir", uploadDir.toAbsolutePath().toString()).build();
        } catch (Exception ex) {
            return Health.down(ex).build();
        }
    }
}
