package com.orionerp.modules.cadastros.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProdutoRequest(
        @NotNull Long empresaId,
        @NotBlank String codigo,
        @NotBlank String nome,
        String descricao,
        Long grupoId,
        Long subgrupoId,
        Long marcaId,
        Long categoriaId,
        Long unidadeMedidaId,
        String codigoBarras,
        String ncm,
        BigDecimal pesoBruto,
        BigDecimal pesoLiquido,
        BigDecimal precoCusto,
        BigDecimal precoVenda,
        BigDecimal estoqueMinimo,
        BigDecimal estoqueMaximo,
        Boolean controlaEstoque,
        Boolean controlaLote,
        Boolean controlaValidade,
        String tipo,
        String observacao
) {}
