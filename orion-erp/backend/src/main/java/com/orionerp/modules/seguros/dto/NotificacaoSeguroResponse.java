package com.orionerp.modules.seguros.dto;

import java.time.LocalDateTime;

public record NotificacaoSeguroResponse(
        Long id,
        Long empresaId,
        String tipo,
        String titulo,
        String descricao,
        String destinatario,
        String tabelaOrigem,
        Long registroId,
        Boolean lida,
        LocalDateTime dataLeitura,
        LocalDateTime createdAt
) {}
