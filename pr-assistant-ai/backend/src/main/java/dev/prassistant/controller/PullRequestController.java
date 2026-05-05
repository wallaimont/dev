package dev.prassistant.controller;

import dev.prassistant.dto.*;
import dev.prassistant.domain.entity.*;
import dev.prassistant.domain.enums.RiskLevel;
import dev.prassistant.repository.PullRequestRepository;
import dev.prassistant.repository.FinalAnalysisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pull-requests")
@RequiredArgsConstructor
public class PullRequestController {

    private final PullRequestRepository prRepository;
    private final FinalAnalysisRepository finalAnalysisRepository;

    @GetMapping
    public Page<PullRequestResponse> list(Pageable pageable) {
        return prRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::toResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PullRequestResponse> get(@PathVariable UUID id) {
        return prRepository.findById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/analyses")
    public List<FinalAnalysisResponse> getAnalyses(@PathVariable UUID id) {
        return finalAnalysisRepository.findByPullRequestIdOrderByCreatedAtDesc(id)
                .stream()
                .map(this::toAnalysisResponse)
                .toList();
    }

    private PullRequestResponse toResponse(PullRequest pr) {
        List<FileResponse> files = pr.getFiles() == null ? List.of() :
                pr.getFiles().stream().map(f -> new FileResponse(
                        f.getId().toString(), f.getFilePath(), f.getFileStatus().name(),
                        f.getAdditions(), f.getDeletions(), f.isCritical(), f.getCriticalReason()
                )).toList();

        List<FinalAnalysis> analyses = finalAnalysisRepository
                .findByPullRequestIdOrderByCreatedAtDesc(pr.getId());
        FinalAnalysisResponse latestFinal = analyses.isEmpty() ? null : toAnalysisResponse(analyses.get(0));

        return new PullRequestResponse(
                pr.getId().toString(),
                pr.getExternalPrId(),
                pr.getRepository().getName(),
                pr.getRepository().getOwner(),
                pr.getTitle(),
                pr.getDescription(),
                pr.getAuthor(),
                pr.getSourceBranch(),
                pr.getTargetBranch(),
                pr.getState().name(),
                files,
                null,
                null,
                latestFinal,
                pr.getCreatedAt(),
                pr.getUpdatedAt()
        );
    }

    private FinalAnalysisResponse toAnalysisResponse(FinalAnalysis fa) {
        return new FinalAnalysisResponse(
                fa.getId().toString(),
                fa.getFinalRiskLevel(),
                fa.getFinalScore(),
                fa.getDecisionReason(),
                fa.getCreatedAt()
        );
    }
}
