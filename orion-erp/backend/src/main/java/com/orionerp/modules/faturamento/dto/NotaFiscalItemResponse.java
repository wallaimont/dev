package com.orionerp.modules.faturamento.dto;

import com.orionerp.modules.faturamento.domain.NotaFiscalItem;

import java.math.BigDecimal;
import java.util.UUID;

public record NotaFiscalItemResponse(
        Long id,
        UUID uuid,
        Long notaFiscalId,
        Integer numeroItem,
        Long produtoId,
        String descricao,
        String ncm,
        String cfop,
        BigDecimal quantidade,
        BigDecimal valorUnitario,
        BigDecimal valorTotal,
        BigDecimal valorDesconto,
        String icmsCst,
        BigDecimal icmsBase,
        BigDecimal icmsAliquota,
        BigDecimal icmsValor,
        BigDecimal icmsStBase,
        BigDecimal icmsStAliquota,
        BigDecimal icmsStValor,
        String ipiCst,
        BigDecimal ipiBase,
        BigDecimal ipiAliquota,
        BigDecimal ipiValor,
        String pisCst,
        BigDecimal pisBase,
        BigDecimal pisAliquota,
        BigDecimal pisValor,
        String cofinsCst,
        BigDecimal cofinsBase,
        BigDecimal cofinsAliquota,
        BigDecimal cofinsValor
) {
    public static NotaFiscalItemResponse from(NotaFiscalItem e) {
        return new NotaFiscalItemResponse(
                e.getId(), e.getUuid(), e.getNotaFiscalId(), e.getNumeroItem(),
                e.getProdutoId(), e.getDescricao(), e.getNcm(), e.getCfop(),
                e.getQuantidade(), e.getValorUnitario(), e.getValorTotal(), e.getValorDesconto(),
                e.getIcmsCst(), e.getIcmsBase(), e.getIcmsAliquota(), e.getIcmsValor(),
                e.getIcmsStBase(), e.getIcmsStAliquota(), e.getIcmsStValor(),
                e.getIpiCst(), e.getIpiBase(), e.getIpiAliquota(), e.getIpiValor(),
                e.getPisCst(), e.getPisBase(), e.getPisAliquota(), e.getPisValor(),
                e.getCofinsCst(), e.getCofinsBase(), e.getCofinsAliquota(), e.getCofinsValor()
        );
    }
}
