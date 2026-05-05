package com.orionerp.modules.seguros.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PropostaSeguroResponse(
        Long id,
        Long empresaId,
        Long filialId,
        String numero,
        Long clienteId,
        String clienteNome,
        Long seguradoraId,
        String seguradoraNome,
        Long corretoraId,
        String corretoraNome,
        String ramo,
        LocalDate vigenciaInicio,
        LocalDate vigenciaFim,
        BigDecimal premioLiquido,
        BigDecimal premioTotal,
        BigDecimal percentualComissao,
        String responsavel,
        String observacao,
        String status
) {}
