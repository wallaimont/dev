package com.seguradora.site.api;

import java.time.LocalDateTime;

public record ProtheusMockHealthResponse(
        String sistema,
        String status,
        String empresa,
        String filial,
        int totalLeadsRecebidos,
        LocalDateTime timestamp
) {
}