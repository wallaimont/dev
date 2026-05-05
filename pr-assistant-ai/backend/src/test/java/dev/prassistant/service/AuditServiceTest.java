package dev.prassistant.service;

import dev.prassistant.domain.entity.AuditLog;
import dev.prassistant.repository.AuditLogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditService auditService;

    @Test
    @DisplayName("log - saves audit log with all fields populated")
    void log_savesAuditLog() {
        auditService.log("PR_ANALYZED", "PullRequest", "uuid-123", "system", "PR #42 analyzed");

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());

        AuditLog saved = captor.getValue();
        assertThat(saved.getAction()).isEqualTo("PR_ANALYZED");
        assertThat(saved.getActor()).isEqualTo("system");
        assertThat(saved.getEntity()).isEqualTo("PullRequest");
        assertThat(saved.getEntityId()).isEqualTo("uuid-123");
        assertThat(saved.getPerformedBy()).isEqualTo("system");
        assertThat(saved.getDetails()).isEqualTo("PR #42 analyzed");
    }

    @Test
    @DisplayName("log - handles null details gracefully")
    void log_nullDetails() {
        auditService.log("USER_LOGIN", "User", "user-1", "admin@test.com", null);

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());

        AuditLog saved = captor.getValue();
        assertThat(saved.getAction()).isEqualTo("USER_LOGIN");
        assertThat(saved.getDetails()).isNull();
    }
}
