package com.orionerp.modules.cadastros.dto;

import com.orionerp.modules.cadastros.domain.Produto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProdutoResponse(
        Long id,
        Long empresaId,
        String codigo,
        String nome,
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
        String observacao,
        Boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ProdutoResponse from(Produto e) {
        return new ProdutoResponse(
                e.getId(),
                e.getEmpresaId(),
                e.getCodigo(),
                e.getNome(),
                e.getDescricao(),
                e.getGrupo() != null ? e.getGrupo().getId() : null,
                e.getSubgrupo() != null ? e.getSubgrupo().getId() : null,
                e.getMarca() != null ? e.getMarca().getId() : null,
                e.getCategoria() != null ? e.getCategoria().getId() : null,
                e.getUnidadeMedida() != null ? e.getUnidadeMedida().getId() : null,
                e.getCodigoBarras(),
                e.getNcm(),
                e.getPesoBruto(),
                e.getPesoLiquido(),
                e.getPrecoCusto(),
                e.getPrecoVenda(),
                e.getEstoqueMinimo(),
                e.getEstoqueMaximo(),
                e.getControlaEstoque(),
                e.getControlaLote(),
                e.getControlaValidade(),
                e.getTipo(),
                e.getObservacao(),
                e.getAtivo(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}
