package com.insuranceflow.master.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Component
public class EfiGateway implements PaymentGateway {

    @Override
    public String criarAssinatura(String emailCliente, String nomeCliente, BigDecimal valor, String ciclo, Map<String, String> metadata) {
        log.info("[EFI] Criando assinatura para {} - valor: {} ciclo: {}", emailCliente, valor, ciclo);
        throw new UnsupportedOperationException("Integração Efí ainda não implementada.");
    }

    @Override
    public void cancelarAssinatura(String idAssinaturaExterna) {
        log.info("[EFI] Cancelando assinatura: {}", idAssinaturaExterna);
        throw new UnsupportedOperationException("Integração Efí ainda não implementada.");
    }

    @Override
    public Map<String, String> criarCobranca(String emailCliente, BigDecimal valor, String descricao, String metodoPagamento) {
        log.info("[EFI] Criando cobrança para {} - valor: {}", emailCliente, valor);
        throw new UnsupportedOperationException("Integração Efí ainda não implementada.");
    }

    @Override
    public String consultarPagamento(String idTransacaoExterna) {
        log.info("[EFI] Consultando pagamento: {}", idTransacaoExterna);
        throw new UnsupportedOperationException("Integração Efí ainda não implementada.");
    }

    @Override
    public Map<String, String> processarWebhook(String payload, Map<String, String> headers) {
        log.info("[EFI] Processando webhook");
        throw new UnsupportedOperationException("Integração Efí ainda não implementada.");
    }

    @Override
    public String getNomeGateway() { return "EFI"; }
}
