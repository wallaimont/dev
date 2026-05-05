package dev.prassistant.domain.entity;

import dev.prassistant.domain.enums.FileStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "pull_request_files")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PullRequestFile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pull_request_id", nullable = false)
    private PullRequest pullRequest;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_status", nullable = false)
    private FileStatus fileStatus;

    @Column(nullable = false)
    private int additions;

    @Column(nullable = false)
    private int deletions;

    @Column(name = "is_critical")
    private boolean critical;

    @Column(name = "critical_reason")
    private String criticalReason;
}
