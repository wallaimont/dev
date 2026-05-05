package com.insuranceflow.master.controller;

import com.insuranceflow.common.dto.*;
import com.insuranceflow.master.dto.*;
import com.insuranceflow.master.model.*;
import com.insuranceflow.master.service.BillingService;
import com.insuranceflow.master.service.MasterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/master")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN_MASTER')")
@Tag(name = "Painel Master", description = "Endpoints exclusivos do administrador da plataforma SaaS")
public class MasterController {

    private final MasterService masterService;
    private final BillingService billingService;

    // ========== DASHBOARD ==========
    @GetMapping("/dashboard")
    @Operation(summary = "Dashboard global do SaaS")
    public ResponseEntity<ApiResponse<DashboardMasterResponse>> dashboard() {
        return ResponseEntity.ok(ApiResponse.ok(masterService.getDashboard()));
    }

    // ========== PLANOS ==========
    @GetMapping("/planos")
    @Operation(summary = "Listar planos")
    public ResponseEntity<ApiResponse<Page<Plano>>> listarPlanos(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(masterService.listarPlanos(pageable)));
    }

    @GetMapping("/planos/{id}")
    @Operation(summary = "Buscar plano por ID")
    public ResponseEntity<ApiResponse<Plano>> buscarPlano(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(masterService.buscarPlano(id)));
    }

    @PostMapping("/planos")
    @Operation(summary = "Criar plano")
    public ResponseEntity<ApiResponse<Plano>> criarPlano(@Valid @RequestBody PlanoRequest request) {
        return ResponseEntity.ok(ApiResponse.created(masterService.criarPlano(request), "Plano criado com sucesso"));
    }

    @PutMapping("/planos/{id}")
    @Operation(summary = "Atualizar plano")
    public ResponseEntity<ApiResponse<Plano>> atualizarPlano(@PathVariable UUID id, @Valid @RequestBody PlanoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(masterService.atualizarPlano(id, request), "Plano atualizado"));
    }

    // ========== EMPRESAS ==========
    @GetMapping("/empresas")
    @Operation(summary = "Listar empresas assinantes")
    public ResponseEntity<ApiResponse<Page<Empresa>>> listarEmpresas(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(masterService.listarEmpresas(pageable)));
    }

    @GetMapping("/empresas/{id}")
    @Operation(summary = "Buscar empresa por ID")
    public ResponseEntity<ApiResponse<Empresa>> buscarEmpresa(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(masterService.buscarEmpresa(id)));
    }

    @PostMapping("/empresas")
    @Operation(summary = "Criar nova empresa")
    public ResponseEntity<ApiResponse<Empresa>> criarEmpresa(@Valid @RequestBody EmpresaRequest request) {
        return ResponseEntity.ok(ApiResponse.created(masterService.criarEmpresa(request), "Empresa criada"));
    }

    @PostMapping("/empresas/{id}/bloquear")
    @Operation(summary = "Bloquear empresa")
    public ResponseEntity<ApiResponse<Void>> bloquearEmpresa(@PathVariable UUID id, @RequestBody java.util.Map<String, String> body) {
        masterService.bloquearEmpresa(id, body.get("motivo"));
        return ResponseEntity.ok(ApiResponse.ok(null, "Empresa bloqueada"));
    }

    @PostMapping("/empresas/{id}/desbloquear")
    @Operation(summary = "Desbloquear empresa")
    public ResponseEntity<ApiResponse<Void>> desbloquearEmpresa(@PathVariable UUID id) {
        masterService.desbloquearEmpresa(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Empresa desbloqueada"));
    }

    // ========== ASSINATURAS ==========
    @GetMapping("/assinaturas")
    @Operation(summary = "Listar assinaturas")
    public ResponseEntity<ApiResponse<Page<AssinaturaEmpresa>>> listarAssinaturas(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(masterService.listarAssinaturas(pageable)));
    }

    // ========== PAGAMENTOS ==========
    @GetMapping("/pagamentos")
    @Operation(summary = "Listar pagamentos")
    public ResponseEntity<ApiResponse<Page<PagamentoAssinatura>>> listarPagamentos(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(masterService.listarPagamentos(pageable)));
    }

    // ========== LEADS ==========
    @GetMapping("/leads")
    @Operation(summary = "Listar leads SaaS")
    public ResponseEntity<ApiResponse<Page<LeadSaas>>> listarLeads(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(masterService.listarLeads(pageable)));
    }

    // ========== TICKETS ==========
    @GetMapping("/tickets")
    @Operation(summary = "Listar tickets de suporte")
    public ResponseEntity<ApiResponse<Page<TicketSuporte>>> listarTickets(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(masterService.listarTickets(pageable)));
    }

    // ========== BILLING ==========
    @GetMapping("/billing/dashboard")
    @Operation(summary = "Dashboard financeiro do billing")
    public ResponseEntity<ApiResponse<BillingDashboardResponse>> dashboardBilling() {
        return ResponseEntity.ok(ApiResponse.ok(billingService.getDashboardBilling()));
    }

    @PostMapping("/billing/assinaturas/ativar")
    @Operation(summary = "Ativar assinatura (trial → paga)")
    public ResponseEntity<ApiResponse<AssinaturaEmpresa>> ativarAssinatura(@Valid @RequestBody AssinaturaRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(billingService.ativarAssinatura(request), "Assinatura ativada com sucesso"));
    }

    @PostMapping("/billing/assinaturas/{id}/cancelar")
    @Operation(summary = "Cancelar assinatura")
    public ResponseEntity<ApiResponse<AssinaturaEmpresa>> cancelarAssinatura(@PathVariable UUID id, @RequestBody java.util.Map<String, String> body) {
        return ResponseEntity.ok(ApiResponse.ok(billingService.cancelarAssinatura(id, body.get("motivo")), "Assinatura cancelada"));
    }

    @PostMapping("/billing/assinaturas/{id}/reativar")
    @Operation(summary = "Reativar assinatura cancelada/suspensa")
    public ResponseEntity<ApiResponse<AssinaturaEmpresa>> reativarAssinatura(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(billingService.reativarAssinatura(id), "Assinatura reativada"));
    }

    @PostMapping("/billing/assinaturas/upgrade-downgrade")
    @Operation(summary = "Upgrade ou downgrade de plano")
    public ResponseEntity<ApiResponse<AssinaturaEmpresa>> upgradeDowngrade(@Valid @RequestBody UpgradeDowngradeRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(billingService.upgradeDowngrade(request), "Plano alterado com sucesso"));
    }

    @GetMapping("/billing/assinaturas")
    @Operation(summary = "Listar assinaturas por status")
    public ResponseEntity<ApiResponse<Page<AssinaturaEmpresa>>> listarAssinaturasBilling(
            @RequestParam(required = false) String status, Pageable pageable) {
        if (status != null && !status.isBlank()) {
            return ResponseEntity.ok(ApiResponse.ok(billingService.listarAssinaturasPorStatus(status, pageable)));
        }
        return ResponseEntity.ok(ApiResponse.ok(masterService.listarAssinaturas(pageable)));
    }

    @PostMapping("/billing/pagamentos/registrar")
    @Operation(summary = "Registrar pagamento manual")
    public ResponseEntity<ApiResponse<PagamentoAssinatura>> registrarPagamento(@Valid @RequestBody PagamentoManualRequest request) {
        return ResponseEntity.ok(ApiResponse.created(billingService.registrarPagamentoManual(request), "Pagamento registrado"));
    }

    @PostMapping("/billing/pagamentos/{id}/estornar")
    @Operation(summary = "Estornar pagamento")
    public ResponseEntity<ApiResponse<PagamentoAssinatura>> estornarPagamento(@PathVariable UUID id, @RequestBody java.util.Map<String, String> body) {
        return ResponseEntity.ok(ApiResponse.ok(billingService.estornarPagamento(id, body.get("motivo")), "Pagamento estornado"));
    }

    @GetMapping("/billing/pagamentos")
    @Operation(summary = "Listar pagamentos por status")
    public ResponseEntity<ApiResponse<Page<PagamentoAssinatura>>> listarPagamentosBilling(
            @RequestParam(required = false) String status, Pageable pageable) {
        if (status != null && !status.isBlank()) {
            return ResponseEntity.ok(ApiResponse.ok(billingService.listarPagamentosPorStatus(status, pageable)));
        }
        return ResponseEntity.ok(ApiResponse.ok(masterService.listarPagamentos(pageable)));
    }

    @GetMapping("/billing/historico")
    @Operation(summary = "Histórico de alterações de plano")
    public ResponseEntity<ApiResponse<Page<HistoricoPlano>>> listarHistorico(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(billingService.listarHistorico(pageable)));
    }

    @GetMapping("/billing/eventos")
    @Operation(summary = "Eventos de billing (webhooks)")
    public ResponseEntity<ApiResponse<Page<EventoBilling>>> listarEventos(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(billingService.listarEventos(pageable)));
    }
}
