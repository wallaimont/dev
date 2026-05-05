package dev.prassistant.domain.entity;

import dev.prassistant.domain.enums.RiskLevel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ai_analysis")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AiAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pull_request_id", nullable = false)
    private PullRequest pullRequest;

    @Column(nullable = false)
    private String provider;

    @Column(name = "executive_summary", columnDefinition = "TEXT")
    private String executiveSummary;

    @Column(name = "technical_summary", columnDefinition = "TEXT")
    private String technicalSummary;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level")
    private RiskLevel riskLevel;

    @Column(name = "architectural_concerns", columnDefinition = "TEXT")
    private String architecturalConcerns;

    @Column(columnDefinition = "TEXT")
    private String impacts;

    @Column(name = "suggested_tests", columnDefinition = "TEXT")
    private String suggestedTests;

    @Column(name = "final_comment", columnDefinition = "TEXT")
    private String finalComment;

    @Column(name = "quality_score")
    private int qualityScore;

    @Column(name = "raw_response_json", columnDefinition = "TEXT")
    private String rawResponseJson;

    @Column(name = "analyzed_at")
    private Instant analyzedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
