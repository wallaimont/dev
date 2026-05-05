package dev.prassistant.domain.entity;

import dev.prassistant.domain.enums.PrState;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "pull_requests")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PullRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "external_pr_id", nullable = false)
    private Long externalPrId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repository_id", nullable = false)
    private Repository repository;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String author;

    @Column(name = "source_branch", nullable = false)
    private String sourceBranch;

    @Column(name = "target_branch", nullable = false)
    private String targetBranch;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrState state;

    @OneToMany(mappedBy = "pullRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PullRequestFile> files = new ArrayList<>();

    @OneToMany(mappedBy = "pullRequest", cascade = CascadeType.ALL)
    @Builder.Default
    private List<RuleAnalysis> ruleAnalyses = new ArrayList<>();

    @OneToMany(mappedBy = "pullRequest", cascade = CascadeType.ALL)
    @Builder.Default
    private List<AiAnalysis> aiAnalyses = new ArrayList<>();

    @OneToMany(mappedBy = "pullRequest", cascade = CascadeType.ALL)
    @Builder.Default
    private List<FinalAnalysis> finalAnalyses = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
