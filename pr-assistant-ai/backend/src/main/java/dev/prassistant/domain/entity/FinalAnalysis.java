package dev.prassistant.domain.entity;

import dev.prassistant.domain.enums.RiskLevel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "final_analysis")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FinalAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pull_request_id", nullable = false)
    private PullRequest pullRequest;

    @Enumerated(EnumType.STRING)
    @Column(name = "final_risk_level", nullable = false)
    private RiskLevel finalRiskLevel;

    @Column(name = "final_score", nullable = false)
    private int finalScore;

    @Column(name = "decision_reason", columnDefinition = "TEXT")
    private String decisionReason;

    @Column(name = "published_comment_url")
    private String publishedCommentUrl;

    @Column(name = "published_success")
    private boolean publishedSuccess;

    @Column(nullable = false)
    private boolean approved;

    @Column(name = "analyzed_at")
    private Instant analyzedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
