package dev.prassistant.service.rules;

import dev.prassistant.service.rules.CriticalFileDetector.CriticalResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class CriticalFileDetectorTest {

    private CriticalFileDetector detector;

    @BeforeEach
    void setUp() {
        detector = new CriticalFileDetector();
    }

    @ParameterizedTest
    @ValueSource(strings = {"Dockerfile", "docker-compose.yml", "docker-compose.prod.yml"})
    @DisplayName("detects infrastructure files as critical")
    void infrastructure(String file) {
        CriticalResult result = detector.check(file);
        assertThat(result.critical()).isTrue();
        assertThat(result.reason()).isEqualTo("Infrastructure file");
    }

    @ParameterizedTest
    @ValueSource(strings = {".github/workflows/ci.yml", ".github/workflows/deploy.yml"})
    @DisplayName("detects CI/CD pipelines as critical")
    void cicd(String file) {
        CriticalResult result = detector.check(file);
        assertThat(result.critical()).isTrue();
        assertThat(result.reason()).isEqualTo("CI/CD pipeline");
    }

    @ParameterizedTest
    @ValueSource(strings = {"application.yml", "application.yaml", "application.properties"})
    @DisplayName("detects app config as critical")
    void appConfig(String file) {
        CriticalResult result = detector.check(file);
        assertThat(result.critical()).isTrue();
        assertThat(result.reason()).isEqualTo("App configuration");
    }

    @Test
    @DisplayName("detects .env as critical")
    void envFile() {
        CriticalResult result = detector.check(".env");
        assertThat(result.critical()).isTrue();
        assertThat(result.reason()).isEqualTo("Environment secrets");
    }

    @ParameterizedTest
    @ValueSource(strings = {"pom.xml", "build.gradle", "package.json"})
    @DisplayName("detects build descriptors as critical")
    void buildDescriptor(String file) {
        CriticalResult result = detector.check(file);
        assertThat(result.critical()).isTrue();
        assertThat(result.reason()).isEqualTo("Build descriptor");
    }

    @Test
    @DisplayName("detects SecurityConfig as critical")
    void securityConfig() {
        CriticalResult result = detector.check("src/main/java/config/SecurityConfig.java");
        assertThat(result.critical()).isTrue();
        assertThat(result.reason()).isEqualTo("Security configuration");
    }

    @ParameterizedTest
    @ValueSource(strings = {"db/migration/V1__init.sql", "flyway/V2__users.sql", "liquibase/changelog.xml"})
    @DisplayName("detects database migrations as critical")
    void dbMigration(String file) {
        CriticalResult result = detector.check(file);
        assertThat(result.critical()).isTrue();
        assertThat(result.reason()).isEqualTo("Database migration");
    }

    @ParameterizedTest
    @ValueSource(strings = {"helm/values.yaml", "k8s/deployment.yml", "kubernetes/service.yml"})
    @DisplayName("detects K8s manifests as critical")
    void k8sManifest(String file) {
        CriticalResult result = detector.check(file);
        assertThat(result.critical()).isTrue();
        assertThat(result.reason()).isEqualTo("Kubernetes manifest");
    }

    @ParameterizedTest
    @ValueSource(strings = {"terraform/main.tf", "infra/variables.tf"})
    @DisplayName("detects Terraform files as critical")
    void terraform(String file) {
        CriticalResult result = detector.check(file);
        assertThat(result.critical()).isTrue();
        assertThat(result.reason()).isEqualTo("Terraform IaC");
    }

    @ParameterizedTest
    @ValueSource(strings = {"src/main/java/Service.java", "README.md", "src/test/TestFoo.java"})
    @DisplayName("returns not critical for regular files")
    void regularFiles(String file) {
        CriticalResult result = detector.check(file);
        assertThat(result.critical()).isFalse();
        assertThat(result.reason()).isNull();
    }
}
