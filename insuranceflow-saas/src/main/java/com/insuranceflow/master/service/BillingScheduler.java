package com.insuranceflow.master.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BillingScheduler {

    private final BillingService billingService;

    /**
     * Verifica trials expirados - executa diariamente às 06:00
     */
    @Scheduled(cron = "0 0 6 * * *")
    public void verificarTrials() {
        log.info("[SCHEDULER] Iniciando verificação de trials expirados...");
        int count = billingService.verificarTrialsExpirados();
        log.info("[SCHEDULER] Trials expirados processados: {}", count);
    }

    /**
     * Verifica pagamentos pendentes vencidos → marca como ATRASADO
     * Executa diariamente às 07:00
     */
    @Scheduled(cron = "0 0 7 * * *")
    public void verificarInadimplencia() {
        log.info("[SCHEDULER] Iniciando verificação de inadimplência...");
        int count = billingService.verificarInadimplencia();
        log.info("[SCHEDULER] Pagamentos atrasados detectados: {}", count);
    }

    /**
     * Bloqueia empresas com pagamentos atrasados há mais de 15 dias
     * Executa diariamente às 08:00
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void bloquearInadimplentes() {
        log.info("[SCHEDULER] Iniciando bloqueio por inadimplência...");
        int count = billingService.bloquearInadimplentes();
        log.info("[SCHEDULER] Empresas bloqueadas: {}", count);
    }

    /**
     * Gera cobranças recorrentes para assinaturas prestes a vencer
     * Executa diariamente às 05:00
     */
    @Scheduled(cron = "0 0 5 * * *")
    public void gerarCobrancasRecorrentes() {
        log.info("[SCHEDULER] Iniciando geração de cobranças recorrentes...");
        int count = billingService.gerarCobrancasRecorrentes();
        log.info("[SCHEDULER] Cobranças geradas: {}", count);
    }
}
