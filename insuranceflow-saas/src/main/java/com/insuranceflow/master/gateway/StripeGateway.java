package com.insuranceflow.master.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Component
public class StripeGateway implements PaymentGateway {

    @Override
    public String criarAssinatura(String emailCliente, String nomeCliente, BigDecimal valor, String ciclo, Map<String, String> metadata) {
        log.info("[STRIPE] Criando assinatura para {} - valor: {} ciclo: {}", emailCliente, valor, ciclo);
        // TODO: Implementar integração real com Stripe API
        // Stripe.apiKey = stripeSecretKey;
        // Customer customer = Customer.create(params);
        // Subscription subscription = Subscription.create(subParams);
        throw new UnsupportedOperationException("Integração Stripe ainda não implementada. Configure a API key e implemente.");
    }

    @Override
    public void cancelarAssinatura(String idAssinaturaExterna) {
        log.info("[STRIPE] Cancelando assinatura: {}", idAssinaturaExterna);
        throw new UnsupportedOperationException("Integração Stripe ainda não implementada.");
    }

    @Override
    public Map<String, String> criarCobranca(String emailCliente, BigDecimal valor, String descricao, String metodoPagamento) {
        log.info("[STRIPE] Criando cobrança para {} - valor: {}", emailCliente, valor);
        throw new UnsupportedOperationException("Integração Stripe ainda não implementada.");
    }

    @Override
    public String consultarPagamento(String idTransacaoExterna) {
        log.info("[STRIPE] Consultando pagamento: {}", idTransacaoExterna);
        throw new UnsupportedOperationException("Integração Stripe ainda não implementada.");
    }

    @Override
    public Map<String, String> processarWebhook(String payload, Map<String, String> headers) {
        log.info("[STRIPE] Processando webhook");
        throw new UnsupportedOperationException("Integração Stripe ainda não implementada.");
    }

    @Override
    public String getNomeGateway() { return "STRIPE"; }
}
