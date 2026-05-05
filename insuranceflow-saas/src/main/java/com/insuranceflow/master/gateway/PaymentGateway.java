package com.insuranceflow.master.gateway;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Abstração para integração com gateways de pagamento.
 * Cada implementação encapsula a lógica de um gateway específico.
 */
public interface PaymentGateway {

    /**
     * Cria uma assinatura recorrente no gateway.
     * @return ID externo da assinatura criada
     */
    String criarAssinatura(String emailCliente, String nomeCliente, BigDecimal valor, String ciclo, Map<String, String> metadata);

    /**
     * Cancela uma assinatura no gateway.
     */
    void cancelarAssinatura(String idAssinaturaExterna);

    /**
     * Gera uma cobrança avulsa (boleto, pix, cartão).
     * @return mapa com idTransacao e linkPagamento
     */
    Map<String, String> criarCobranca(String emailCliente, BigDecimal valor, String descricao, String metodoPagamento);

    /**
     * Consulta o status de um pagamento no gateway.
     * @return status conforme o gateway (mapeado pelo BillingService)
     */
    String consultarPagamento(String idTransacaoExterna);

    /**
     * Processa webhook recebido do gateway.
     * @return mapa com tipo de evento e dados relevantes
     */
    Map<String, String> processarWebhook(String payload, Map<String, String> headers);

    /**
     * Retorna o nome do gateway (ex: STRIPE, ASAAS, etc.)
     */
    String getNomeGateway();
}
