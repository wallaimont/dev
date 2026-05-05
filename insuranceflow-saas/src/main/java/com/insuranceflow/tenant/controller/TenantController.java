package com.insuranceflow.tenant.controller;

import com.insuranceflow.common.dto.ApiResponse;
import com.insuranceflow.master.model.ConfiguracaoWhiteLabel;
import com.insuranceflow.security.UserPrincipal;
import com.insuranceflow.tenant.dto.*;
import com.insuranceflow.tenant.model.*;
import com.insuranceflow.tenant.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/tenant")
@RequiredArgsConstructor
@Tag(name = "Painel da Administradora", description = "Endpoints operacionais da administradora assinante")
public class TenantController {

    private final TenantService tenantService;

    // ========== DASHBOARD ==========
    @GetMapping("/dashboard")
    @Operation(summary = "Dashboard da administradora")
    public ResponseEntity<ApiResponse<DashboardTenantResponse>> dashboard() {
        return ResponseEntity.ok(ApiResponse.ok(tenantService.getDashboard()));
    }

    // ========== CLIENTES ==========
    @GetMapping("/clientes")
    @Operation(summary = "Listar clientes")
    public ResponseEntity<ApiResponse<Page<Cliente>>> listarClientes(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(tenantService.listarClientes(pageable)));
    }

    @GetMapping("/clientes/{id}")
    @Operation(summary = "Buscar cliente por ID")
    public ResponseEntity<ApiResponse<Cliente>> buscarCliente(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(tenantService.buscarCliente(id)));
    }

    @PostMapping("/clientes")
    @Operation(summary = "Criar cliente")
    public ResponseEntity<ApiResponse<Cliente>> criarCliente(@Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.ok(ApiResponse.created(tenantService.criarCliente(request), "Cliente criado com sucesso"));
    }

    @PutMapping("/clientes/{id}")
    @Operation(summary = "Atualizar cliente")
    public ResponseEntity<ApiResponse<Cliente>> atualizarCliente(@PathVariable UUID id, @Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(tenantService.atualizarCliente(id, request), "Cliente atualizado"));
    }

    // ========== SEGURADORAS ==========
    @GetMapping("/seguradoras")
    @Operation(summary = "Listar seguradoras")
    public ResponseEntity<ApiResponse<Page<Seguradora>>> listarSeguradoras(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(tenantService.listarSeguradoras(pageable)));
    }

    // ========== CORRETORAS ==========
    @GetMapping("/corretoras")
    @Operation(summary = "Listar corretoras")
    public ResponseEntity<ApiResponse<Page<Corretora>>> listarCorretoras(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(tenantService.listarCorretoras(pageable)));
    }

    // ========== PROPOSTAS ==========
    @GetMapping("/propostas")
    @Operation(summary = "Listar propostas")
    public ResponseEntity<ApiResponse<Page<Proposta>>> listarPropostas(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(tenantService.listarPropostas(pageable)));
    }

    @GetMapping("/propostas/{id}")
    @Operation(summary = "Buscar proposta por ID")
    public ResponseEntity<ApiResponse<Proposta>> buscarProposta(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(tenantService.buscarProposta(id)));
    }

    @PostMapping("/propostas")
    @Operation(summary = "Criar proposta")
    public ResponseEntity<ApiResponse<Proposta>> criarProposta(@Valid @RequestBody PropostaRequest request) {
        return ResponseEntity.ok(ApiResponse.created(tenantService.criarProposta(request), "Proposta criada"));
    }

    // ========== APÓLICES ==========
    @GetMapping("/apolices")
    @Operation(summary = "Listar apólices")
    public ResponseEntity<ApiResponse<Page<Apolice>>> listarApolices(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(tenantService.listarApolices(pageable)));
    }

    @GetMapping("/apolices/{id}")
    @Operation(summary = "Buscar apólice por ID")
    public ResponseEntity<ApiResponse<Apolice>> buscarApolice(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(tenantService.buscarApolice(id)));
    }

    // ========== SINISTROS ==========
    @GetMapping("/sinistros")
    @Operation(summary = "Listar sinistros")
    public ResponseEntity<ApiResponse<Page<Sinistro>>> listarSinistros(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(tenantService.listarSinistros(pageable)));
    }

    @PostMapping("/sinistros")
    @Operation(summary = "Criar sinistro")
    public ResponseEntity<ApiResponse<Sinistro>> criarSinistro(@Valid @RequestBody SinistroRequest request) {
        return ResponseEntity.ok(ApiResponse.created(tenantService.criarSinistro(request), "Sinistro registrado com sucesso"));
    }

    // ========== RENOVAÇÕES ==========
    @GetMapping("/renovacoes")
    @Operation(summary = "Listar renovações")
    public ResponseEntity<ApiResponse<Page<Renovacao>>> listarRenovacoes(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(tenantService.listarRenovacoes(pageable)));
    }

    // ========== FINANCEIRO ==========
    @GetMapping("/financeiro")
    @Operation(summary = "Listar lançamentos financeiros")
    public ResponseEntity<ApiResponse<Page<LancamentoFinanceiro>>> listarFinanceiro(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(tenantService.listarFinanceiro(pageable)));
    }

    @PostMapping("/financeiro")
    @Operation(summary = "Criar lançamento financeiro")
    public ResponseEntity<ApiResponse<LancamentoFinanceiro>> criarFinanceiro(@Valid @RequestBody LancamentoFinanceiroRequest request) {
        return ResponseEntity.ok(ApiResponse.created(tenantService.criarLancamento(request), "Lançamento criado com sucesso"));
    }

    // ========== BOLETOS ==========
    @GetMapping("/boletos")
    @Operation(summary = "Listar boletos")
    public ResponseEntity<ApiResponse<Page<Boleto>>> listarBoletos(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(tenantService.listarBoletos(pageable)));
    }

    @PostMapping("/boletos")
    @Operation(summary = "Criar boleto")
    public ResponseEntity<ApiResponse<Boleto>> criarBoleto(@Valid @RequestBody BoletoRequest request) {
        return ResponseEntity.ok(ApiResponse.created(tenantService.criarBoleto(request), "Boleto criado com sucesso"));
    }

    // ========== COMISSÕES ==========
    @GetMapping("/comissoes")
    @Operation(summary = "Listar comissões")
    public ResponseEntity<ApiResponse<Page<Comissao>>> listarComissoes(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(tenantService.listarComissoes(pageable)));
    }

    @PostMapping("/comissoes")
    @Operation(summary = "Criar comissão")
    public ResponseEntity<ApiResponse<Comissao>> criarComissao(@Valid @RequestBody ComissaoRequest request) {
        return ResponseEntity.ok(ApiResponse.created(tenantService.criarComissao(request), "Comissão registrada com sucesso"));
    }

    // ========== AUDITORIA ==========
    @GetMapping("/auditoria")
    @Operation(summary = "Listar auditoria")
    public ResponseEntity<ApiResponse<Page<Auditoria>>> listarAuditoria(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(tenantService.listarAuditoria(pageable)));
    }

    // ========== WHITE-LABEL ==========
    @GetMapping("/white-label")
    @Operation(summary = "Obter configuração white-label")
    public ResponseEntity<ApiResponse<ConfiguracaoWhiteLabel>> getWhiteLabel() {
        return ResponseEntity.ok(ApiResponse.ok(tenantService.getWhiteLabel()));
    }

    @PutMapping("/white-label")
    @Operation(summary = "Atualizar configuração white-label")
    public ResponseEntity<ApiResponse<ConfiguracaoWhiteLabel>> atualizarWhiteLabel(@Valid @RequestBody WhiteLabelRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(tenantService.atualizarWhiteLabel(request), "Personalização atualizada com sucesso"));
    }

    // ========== NOTIFICAÇÕES ==========
    @GetMapping("/notificacoes")
    @Operation(summary = "Listar notificações do usuário")
    public ResponseEntity<ApiResponse<Page<Notificacao>>> listarNotificacoes(@AuthenticationPrincipal UserPrincipal principal, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(tenantService.listarNotificacoes(principal.getId(), pageable)));
    }

    @GetMapping("/notificacoes/count")
    @Operation(summary = "Contar notificações não lidas")
    public ResponseEntity<ApiResponse<Map<String, Long>>> contarNotificacoes(@AuthenticationPrincipal UserPrincipal principal) {
        long count = tenantService.contarNotificacoesNaoLidas(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(Map.of("naoLidas", count)));
    }

    @PostMapping("/notificacoes")
    @Operation(summary = "Criar notificação")
    public ResponseEntity<ApiResponse<Notificacao>> criarNotificacao(@Valid @RequestBody NotificacaoRequest request) {
        return ResponseEntity.ok(ApiResponse.created(tenantService.criarNotificacao(request), "Notificação criada"));
    }

    @PutMapping("/notificacoes/{id}/lida")
    @Operation(summary = "Marcar notificação como lida")
    public ResponseEntity<ApiResponse<Notificacao>> marcarLida(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(tenantService.marcarNotificacaoLida(id), "Notificação marcada como lida"));
    }

    // ========== WHATSAPP ==========
    @GetMapping("/whatsapp")
    @Operation(summary = "Listar mensagens WhatsApp")
    public ResponseEntity<ApiResponse<Page<MensagemWhatsapp>>> listarWhatsapp(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(tenantService.listarWhatsapp(pageable)));
    }

    @PostMapping("/whatsapp")
    @Operation(summary = "Enviar mensagem WhatsApp")
    public ResponseEntity<ApiResponse<MensagemWhatsapp>> enviarWhatsapp(@Valid @RequestBody WhatsappRequest request) {
        return ResponseEntity.ok(ApiResponse.created(tenantService.enviarWhatsapp(request), "Mensagem enviada com sucesso"));
    }

    // ========== ASSINATURA DIGITAL ==========
    @GetMapping("/assinaturas-digitais")
    @Operation(summary = "Listar assinaturas digitais")
    public ResponseEntity<ApiResponse<Page<AssinaturaDigital>>> listarAssinaturas(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(tenantService.listarAssinaturas(pageable)));
    }

    @PostMapping("/assinaturas-digitais")
    @Operation(summary = "Solicitar assinatura digital")
    public ResponseEntity<ApiResponse<AssinaturaDigital>> solicitarAssinatura(@Valid @RequestBody AssinaturaDigitalRequest request) {
        return ResponseEntity.ok(ApiResponse.created(tenantService.solicitarAssinatura(request), "Assinatura solicitada com sucesso"));
    }

    @PutMapping("/assinaturas-digitais/{id}/assinar")
    @Operation(summary = "Assinar documento digitalmente")
    public ResponseEntity<ApiResponse<AssinaturaDigital>> assinarDocumento(@PathVariable UUID id, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        return ResponseEntity.ok(ApiResponse.ok(tenantService.assinarDocumento(id, ip), "Documento assinado com sucesso"));
    }
}
