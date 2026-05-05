package com.insuranceflow.master.service;

import com.insuranceflow.common.exception.BusinessException;
import com.insuranceflow.master.dto.*;
import com.insuranceflow.master.gateway.PaymentGatewayFactory;
import com.insuranceflow.master.model.*;
import com.insuranceflow.master.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingService {

    private final AssinaturaEmpresaRepository assinaturaRepo;
    private final PagamentoAssinaturaRepository pagamentoRepo;
    private final PlanoRepository planoRepo;
    private final EmpresaRepository empresaRepo;
    private final HistoricoPlanoRepository historicoPlanoRepo;
    private final EventoBillingRepository eventoBillingRepo;
    private final PaymentGatewayFactory gatewayFactory;

    // ========== DASHBOARD BILLING ==========
    public BillingDashboardResponse getDashboardBilling() {
        BigDecimal mrrMensal = assinaturaRepo.calcularMRR();
        BigDecimal mrrAnual = assinaturaRepo.calcularMRRAnual();
        BigDecimal mrr = mrrMensal.add(mrrAnual);

        return BillingDashboardResponse.builder()
                .assinaturasAtivas(assinaturaRepo.countByStatus("ATIVA"))
                .assinaturasTrial(assinaturaRepo.countByStatus("TRIAL"))
                .assinaturasAtrasadas(assinaturaRepo.countByStatus("ATRASADA"))
                .assinaturasCanceladas(assinaturaRepo.countByStatus("CANCELADA"))
                .mrr(mrr)
                .arr(mrr.multiply(BigDecimal.valueOf(12)))
                .totalRecebido(pagamentoRepo.calcularTotalRecebido())
                .totalPendente(pagamentoRepo.calcularTotalPendente())
                .pagamentosPendentes(pagamentoRepo.countByStatus("PENDENTE"))
                .pagamentosAtrasados(pagamentoRepo.countByStatus("ATRASADO"))
                .build();
    }

    // ========== ATIVAR ASSINATURA (TRIAL → PAGA) ==========
    @Transactional
    public AssinaturaEmpresa ativarAssinatura(AssinaturaRequest req) {
        Empresa empresa = empresaRepo.findById(req.getEmpresaId())
                .orElseThrow(() -> new BusinessException("Empresa não encontrada"));
        Plano plano = planoRepo.findById(req.getPlanoId())
                .orElseThrow(() -> new BusinessException("Plano não encontrado"));

        AssinaturaEmpresa assinatura = assinaturaRepo.findByEmpresaIdAndActiveTrue(req.getEmpresaId())
                .orElseThrow(() -> new BusinessException("Assinatura não encontrada para esta empresa"));

        UUID planoAnteriorId = assinatura.getPlanoId();
        boolean isMensal = "MENSAL".equals(req.getCiclo());
        BigDecimal valor = isMensal ? plano.getPrecoMensal() : plano.getPrecoAnual();

        // Atualizar assinatura
        assinatura.setPlanoId(plano.getId());
        assinatura.setCiclo(req.getCiclo());
        assinatura.setValor(valor);
        assinatura.setStatus("ATIVA");
        assinatura.setDataInicio(LocalDateTime.now());
        assinatura.setDataVencimento(isMensal
                ? LocalDateTime.now().plusMonths(1)
                : LocalDateTime.now().plusYears(1));
        assinatura.setGatewayPagamento(req.getGateway() != null ? req.getGateway() : "MANUAL");
        assinatura.setObservacoes(req.getObservacoes());
        assinaturaRepo.save(assinatura);

        // Atualizar empresa
        empresa.setStatus("ATIVA");
        empresa.setPlanoId(plano.getId());
        empresa.setBloqueada(false);
        empresa.setMotivoBloqueio(null);
        empresaRepo.save(empresa);

        // Registrar histórico
        registrarHistoricoPlano(empresa.getId(), planoAnteriorId, plano.getId(),
                "TRIAL_PARA_PAGO", "Ativação de assinatura " + req.getCiclo());

        // Gerar primeiro pagamento
        gerarPagamento(assinatura, valor);

        log.info("Assinatura ativada: empresa={} plano={} ciclo={}", empresa.getId(), plano.getNome(), req.getCiclo());
        return assinatura;
    }

    // ========== CANCELAR ASSINATURA ==========
    @Transactional
    public AssinaturaEmpresa cancelarAssinatura(UUID assinaturaId, String motivo) {
        AssinaturaEmpresa assinatura = assinaturaRepo.findById(assinaturaId)
                .orElseThrow(() -> new BusinessException("Assinatura não encontrada"));

        assinatura.setStatus("CANCELADA");
        assinatura.setDataCancelamento(LocalDateTime.now());
        assinatura.setObservacoes(motivo);
        assinaturaRepo.save(assinatura);

        // Atualizar empresa
        Empresa empresa = empresaRepo.findById(assinatura.getEmpresaId())
                .orElseThrow(() -> new BusinessException("Empresa não encontrada"));
        empresa.setStatus("CANCELADA");
        empresaRepo.save(empresa);

        registrarHistoricoPlano(assinatura.getEmpresaId(), assinatura.getPlanoId(), null,
                "CANCELAMENTO", motivo);

        log.info("Assinatura cancelada: id={} empresa={}", assinaturaId, assinatura.getEmpresaId());
        return assinatura;
    }

    // ========== UPGRADE / DOWNGRADE ==========
    @Transactional
    public AssinaturaEmpresa upgradeDowngrade(UpgradeDowngradeRequest req) {
        Empresa empresa = empresaRepo.findById(req.getEmpresaId())
                .orElseThrow(() -> new BusinessException("Empresa não encontrada"));
        Plano novoPlano = planoRepo.findById(req.getNovoPlanoId())
                .orElseThrow(() -> new BusinessException("Plano não encontrado"));

        AssinaturaEmpresa assinatura = assinaturaRepo.findByEmpresaIdAndActiveTrue(req.getEmpresaId())
                .orElseThrow(() -> new BusinessException("Assinatura ativa não encontrada"));

        UUID planoAnteriorId = assinatura.getPlanoId();
        Plano planoAnterior = planoRepo.findById(planoAnteriorId).orElse(null);

        boolean isMensal = "MENSAL".equals(req.getCiclo());
        BigDecimal novoValor = isMensal ? novoPlano.getPrecoMensal() : novoPlano.getPrecoAnual();
        BigDecimal valorAnterior = assinatura.getValor();

        String tipoAlteracao = novoValor.compareTo(valorAnterior) > 0 ? "UPGRADE" : "DOWNGRADE";

        assinatura.setPlanoId(novoPlano.getId());
        assinatura.setCiclo(req.getCiclo());
        assinatura.setValor(novoValor);
        assinatura.setDataVencimento(isMensal
                ? LocalDateTime.now().plusMonths(1)
                : LocalDateTime.now().plusYears(1));
        assinaturaRepo.save(assinatura);

        empresa.setPlanoId(novoPlano.getId());
        empresaRepo.save(empresa);

        String obs = String.format("%s: %s → %s (%s)",
                tipoAlteracao,
                planoAnterior != null ? planoAnterior.getNome() : "N/A",
                novoPlano.getNome(),
                req.getObservacoes() != null ? req.getObservacoes() : "");
        registrarHistoricoPlano(empresa.getId(), planoAnteriorId, novoPlano.getId(), tipoAlteracao, obs);

        log.info("{} realizado: empresa={} {} → {}", tipoAlteracao, empresa.getId(),
                planoAnterior != null ? planoAnterior.getNome() : "N/A", novoPlano.getNome());
        return assinatura;
    }

    // ========== REATIVAR ASSINATURA ==========
    @Transactional
    public AssinaturaEmpresa reativarAssinatura(UUID assinaturaId) {
        AssinaturaEmpresa assinatura = assinaturaRepo.findById(assinaturaId)
                .orElseThrow(() -> new BusinessException("Assinatura não encontrada"));

        if (!"CANCELADA".equals(assinatura.getStatus()) && !"SUSPENSA".equals(assinatura.getStatus())) {
            throw new BusinessException("Apenas assinaturas canceladas ou suspensas podem ser reativadas");
        }

        boolean isMensal = "MENSAL".equals(assinatura.getCiclo());
        assinatura.setStatus("ATIVA");
        assinatura.setDataCancelamento(null);
        assinatura.setDataInicio(LocalDateTime.now());
        assinatura.setDataVencimento(isMensal
                ? LocalDateTime.now().plusMonths(1)
                : LocalDateTime.now().plusYears(1));
        assinaturaRepo.save(assinatura);

        Empresa empresa = empresaRepo.findById(assinatura.getEmpresaId())
                .orElseThrow(() -> new BusinessException("Empresa não encontrada"));
        empresa.setStatus("ATIVA");
        empresa.setBloqueada(false);
        empresa.setMotivoBloqueio(null);
        empresaRepo.save(empresa);

        registrarHistoricoPlano(empresa.getId(), assinatura.getPlanoId(), assinatura.getPlanoId(),
                "REATIVACAO", "Reativação de assinatura");

        log.info("Assinatura reativada: id={} empresa={}", assinaturaId, assinatura.getEmpresaId());
        return assinatura;
    }

    // ========== REGISTRAR PAGAMENTO MANUAL ==========
    @Transactional
    public PagamentoAssinatura registrarPagamentoManual(PagamentoManualRequest req) {
        AssinaturaEmpresa assinatura = assinaturaRepo.findById(req.getAssinaturaId())
                .orElseThrow(() -> new BusinessException("Assinatura não encontrada"));

        PagamentoAssinatura pagamento = new PagamentoAssinatura();
        pagamento.setAssinaturaId(req.getAssinaturaId());
        pagamento.setEmpresaId(req.getEmpresaId());
        pagamento.setValor(req.getValor());
        pagamento.setDataVencimento(LocalDate.now());
        pagamento.setDataPagamento(LocalDate.now());
        pagamento.setStatus("PAGO");
        pagamento.setMetodoPagamento(req.getMetodoPagamento());
        pagamento.setObservacoes(req.getObservacoes());
        pagamentoRepo.save(pagamento);

        // Se assinatura estava atrasada, reativar
        if ("ATRASADA".equals(assinatura.getStatus()) || "PENDENTE".equals(assinatura.getStatus())) {
            assinatura.setStatus("ATIVA");
            boolean isMensal = "MENSAL".equals(assinatura.getCiclo());
            assinatura.setDataVencimento(isMensal
                    ? LocalDateTime.now().plusMonths(1)
                    : LocalDateTime.now().plusYears(1));
            assinaturaRepo.save(assinatura);

            // Desbloquear empresa se estava bloqueada
            Empresa empresa = empresaRepo.findById(assinatura.getEmpresaId()).orElse(null);
            if (empresa != null && Boolean.TRUE.equals(empresa.getBloqueada())) {
                empresa.setBloqueada(false);
                empresa.setMotivoBloqueio(null);
                empresa.setStatus("ATIVA");
                empresaRepo.save(empresa);
            }
        }

        log.info("Pagamento manual registrado: assinatura={} valor={}", req.getAssinaturaId(), req.getValor());
        return pagamento;
    }

    // ========== ESTORNAR PAGAMENTO ==========
    @Transactional
    public PagamentoAssinatura estornarPagamento(UUID pagamentoId, String motivo) {
        PagamentoAssinatura pagamento = pagamentoRepo.findById(pagamentoId)
                .orElseThrow(() -> new BusinessException("Pagamento não encontrado"));

        if (!"PAGO".equals(pagamento.getStatus())) {
            throw new BusinessException("Apenas pagamentos com status PAGO podem ser estornados");
        }

        pagamento.setStatus("ESTORNADO");
        pagamento.setObservacoes(motivo);
        pagamentoRepo.save(pagamento);

        log.info("Pagamento estornado: id={} valor={}", pagamentoId, pagamento.getValor());
        return pagamento;
    }

    // ========== VERIFICAR TRIALS EXPIRADOS ==========
    @Transactional
    public int verificarTrialsExpirados() {
        var expirados = assinaturaRepo.findTrialsExpirados(LocalDateTime.now());
        int count = 0;
        for (AssinaturaEmpresa assinatura : expirados) {
            assinatura.setStatus("EXPIRADA");
            assinaturaRepo.save(assinatura);

            Empresa empresa = empresaRepo.findById(assinatura.getEmpresaId()).orElse(null);
            if (empresa != null) {
                empresa.setStatus("PENDENTE");
                empresa.setBloqueada(true);
                empresa.setMotivoBloqueio("Trial expirado - aguardando contratação de plano");
                empresaRepo.save(empresa);
            }
            count++;
        }
        if (count > 0) {
            log.info("Trials expirados processados: {}", count);
        }
        return count;
    }

    // ========== VERIFICAR INADIMPLÊNCIA ==========
    @Transactional
    public int verificarInadimplencia() {
        // Pagamentos pendentes vencidos há mais de 5 dias → ATRASADO
        var pendentesVencidos = pagamentoRepo.findByStatusAndDataVencimentoBeforeAndActiveTrue(
                "PENDENTE", LocalDate.now().minusDays(5));
        int count = 0;
        for (PagamentoAssinatura pagamento : pendentesVencidos) {
            pagamento.setStatus("ATRASADO");
            pagamentoRepo.save(pagamento);

            AssinaturaEmpresa assinatura = assinaturaRepo.findById(pagamento.getAssinaturaId()).orElse(null);
            if (assinatura != null && "ATIVA".equals(assinatura.getStatus())) {
                assinatura.setStatus("ATRASADA");
                assinaturaRepo.save(assinatura);
            }
            count++;
        }
        if (count > 0) {
            log.info("Pagamentos marcados como atrasados: {}", count);
        }
        return count;
    }

    // ========== BLOQUEAR POR INADIMPLÊNCIA ==========
    @Transactional
    public int bloquearInadimplentes() {
        // Pagamentos atrasados há mais de 15 dias → bloquear
        var atrasados = pagamentoRepo.findByStatusAndDataVencimentoBeforeAndActiveTrue(
                "ATRASADO", LocalDate.now().minusDays(15));
        int count = 0;
        for (PagamentoAssinatura pagamento : atrasados) {
            Empresa empresa = empresaRepo.findById(pagamento.getEmpresaId()).orElse(null);
            if (empresa != null && !Boolean.TRUE.equals(empresa.getBloqueada())) {
                empresa.setBloqueada(true);
                empresa.setMotivoBloqueio("Bloqueio automático por inadimplência");
                empresa.setStatus("BLOQUEADA");
                empresaRepo.save(empresa);

                AssinaturaEmpresa assinatura = assinaturaRepo.findById(pagamento.getAssinaturaId()).orElse(null);
                if (assinatura != null) {
                    assinatura.setStatus("SUSPENSA");
                    assinaturaRepo.save(assinatura);
                }
                count++;
            }
        }
        if (count > 0) {
            log.info("Empresas bloqueadas por inadimplência: {}", count);
        }
        return count;
    }

    // ========== GERAR COBRANÇAS RECORRENTES ==========
    @Transactional
    public int gerarCobrancasRecorrentes() {
        // Assinaturas ativas vencendo nos próximos 3 dias
        var vencendo = assinaturaRepo.findAtivasVencendo(LocalDateTime.now().plusDays(3));
        int count = 0;
        for (AssinaturaEmpresa assinatura : vencendo) {
            gerarPagamento(assinatura, assinatura.getValor());

            // Renovar vencimento
            boolean isMensal = "MENSAL".equals(assinatura.getCiclo());
            assinatura.setDataVencimento(isMensal
                    ? assinatura.getDataVencimento().plusMonths(1)
                    : assinatura.getDataVencimento().plusYears(1));
            assinaturaRepo.save(assinatura);
            count++;
        }
        if (count > 0) {
            log.info("Cobranças recorrentes geradas: {}", count);
        }
        return count;
    }

    // ========== LISTAR ==========
    public Page<AssinaturaEmpresa> listarAssinaturasPorStatus(String status, Pageable pageable) {
        return assinaturaRepo.findByStatusAndActiveTrue(status, pageable);
    }

    public Page<PagamentoAssinatura> listarPagamentosPorStatus(String status, Pageable pageable) {
        return pagamentoRepo.findByStatusAndActiveTrue(status, pageable);
    }

    public Page<HistoricoPlano> listarHistorico(Pageable pageable) {
        return historicoPlanoRepo.findAllByOrderByCreatedAtDesc(pageable);
    }

    public Page<EventoBilling> listarEventos(Pageable pageable) {
        return eventoBillingRepo.findByActiveTrue(pageable);
    }

    // ========== MÉTODOS AUXILIARES ==========
    private void gerarPagamento(AssinaturaEmpresa assinatura, BigDecimal valor) {
        PagamentoAssinatura pagamento = new PagamentoAssinatura();
        pagamento.setAssinaturaId(assinatura.getId());
        pagamento.setEmpresaId(assinatura.getEmpresaId());
        pagamento.setValor(valor);
        pagamento.setDataVencimento(LocalDate.now().plusDays(
                "MENSAL".equals(assinatura.getCiclo()) ? 30 : 365));
        pagamento.setStatus("PENDENTE");
        pagamento.setObservacoes("Cobrança automática - " + assinatura.getCiclo());
        pagamentoRepo.save(pagamento);
    }

    private void registrarHistoricoPlano(UUID empresaId, UUID planoAnteriorId, UUID planoNovoId,
                                          String tipoAlteracao, String observacoes) {
        HistoricoPlano historico = new HistoricoPlano();
        historico.setEmpresaId(empresaId);
        historico.setPlanoAnteriorId(planoAnteriorId);
        historico.setPlanoNovoId(planoNovoId);
        historico.setTipoAlteracao(tipoAlteracao);
        historico.setObservacoes(observacoes);
        historico.setCreatedAt(LocalDateTime.now());
        historicoPlanoRepo.save(historico);
    }
}
