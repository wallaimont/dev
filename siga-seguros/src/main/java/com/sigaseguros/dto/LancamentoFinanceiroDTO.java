package com.sigaseguros.dto;

import com.sigaseguros.enums.FormaPagamento;
import com.sigaseguros.enums.StatusFinanceiro;
import com.sigaseguros.enums.TipoLancamento;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LancamentoFinanceiroDTO {

    private Long id;

    @NotNull(message = "Tipo de lançamento é obrigatório")
    private TipoLancamento tipoLancamento;

    private String origem;
    private Long clienteId;
    private String clienteNome;
    private Long corretoraId;
    private String corretoraNome;
    private Long seguradoraId;
    private String seguradoraNome;
    private Long propostaId;
    private Long apoliceId;
    private String descricao;

    @NotNull(message = "Valor é obrigatório")
    private BigDecimal valor;

    @NotNull(message = "Vencimento é obrigatório")
    private String vencimento;

    private String dataPagamento;
    private FormaPagamento formaPagamento;
    private StatusFinanceiro status;
    private String observacoes;
    private Integer numeroParcela;
    private Integer totalParcelas;
    private Boolean active;
    private String createdAt;
}
