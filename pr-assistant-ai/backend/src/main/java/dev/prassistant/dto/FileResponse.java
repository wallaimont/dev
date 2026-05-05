package dev.prassistant.dto;

public record FileResponse(
        String id,
        String filePath,
        String fileStatus,
        int additions,
        int deletions,
        boolean critical,
        String criticalReason
) {}
