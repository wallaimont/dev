package com.orionerp.modules.seguros.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ApoliceResponse(
        Long id,
        Long empresaId,
        Long filialId,
        String numero,
        Long propostaId,
        String propostaNumero,
        Long clienteId,
        String clienteNome,
        Long seguradoraId,
        String seguradoraNome,
        Long corretoraId,
        String corretoraNome,
        String ramo,
        LocalDate vigenciaInicio,
        LocalDate vigenciaFim,
        BigDecimal premioTotal,
        BigDecimal importanciaSegurada,
        BigDecimal franquia,
        BigDecimal percentualComissao,
        String certificadoInclusao,
        String observacao,
        String status
) {}
