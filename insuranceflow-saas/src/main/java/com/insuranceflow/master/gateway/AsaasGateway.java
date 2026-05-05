package com.insuranceflow.master.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Component
public class AsaasGateway implements PaymentGateway {

    @Override
    public String criarAssinatura(String emailCliente, String nomeCliente, BigDecimal valor, String ciclo, Map<String, String> metadata) {
        log.info("[ASAAS] Criando assinatura para {} - valor: {} ciclo: {}", emailCliente, valor, ciclo);
        throw new UnsupportedOperationException("Integração Asaas ainda não implementada. Configure a API key e implemente.");
    }

    @Override
    public void cancelarAssinatura(String idAssinaturaExterna) {
        log.info("[ASAAS] Cancelando assinatura: {}", idAssinaturaExterna);
        throw new UnsupportedOperationException("Integração Asaas ainda não implementada.");
    }

    @Override
    public Map<String, String> criarCobranca(String emailCliente, BigDecimal valor, String descricao, String metodoPagamento) {
        log.info("[ASAAS] Criando cobrança para {} - valor: {}", emailCliente, valor);
        throw new UnsupportedOperationException("Integração Asaas ainda não implementada.");
    }

    @Override
    public String consultarPagamento(String idTransacaoExterna) {
        log.info("[ASAAS] Consultando pagamento: {}", idTransacaoExterna);
        throw new UnsupportedOperationException("Integração Asaas ainda não implementada.");
    }

    @Override
    public Map<String, String> processarWebhook(String payload, Map<String, String> headers) {
        log.info("[ASAAS] Processando webhook");
        throw new UnsupportedOperationException("Integração Asaas ainda não implementada.");
    }

    @Override
    public String getNomeGateway() { return "ASAAS"; }
}
