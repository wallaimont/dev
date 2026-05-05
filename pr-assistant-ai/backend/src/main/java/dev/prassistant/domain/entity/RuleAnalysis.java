package dev.prassistant.domain.entity;

import dev.prassistant.domain.enums.RiskLevel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "rule_analysis")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RuleAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pull_request_id", nullable = false)
    private PullRequest pullRequest;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false)
    private RiskLevel riskLevel;

    @Column(name = "critical_files_count", nullable = false)
    private int criticalFilesCount;

    @ElementCollection
    @CollectionTable(name = "rule_analysis_critical_files", joinColumns = @JoinColumn(name = "rule_analysis_id"))
    @Column(name = "file_path")
    @Builder.Default
    private List<String> criticalFiles = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "rule_analysis_violations", joinColumns = @JoinColumn(name = "rule_analysis_id"))
    @Column(name = "violation")
    @Builder.Default
    private List<String> violations = new ArrayList<>();

    @Column(name = "analyzed_at")
    private Instant analyzedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
