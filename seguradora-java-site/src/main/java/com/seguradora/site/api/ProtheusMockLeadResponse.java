package com.seguradora.site.api;

import java.time.LocalDateTime;

public record ProtheusMockLeadResponse(
        Long id,
        String protocolo,
        String sistema,
        String empresa,
        String filial,
        String status,
        String origem,
        String nome,
        String email,
        String telefone,
        String tipoSeguro,
        String mensagem,
        LocalDateTime recebidoEm
) {
}