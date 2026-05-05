package com.insuranceflow.portal.controller;

import com.insuranceflow.common.dto.ApiResponse;
import com.insuranceflow.master.model.ConfiguracaoWhiteLabel;
import com.insuranceflow.master.repository.ConfiguracaoWhiteLabelRepository;
import com.insuranceflow.security.UserPrincipal;
import com.insuranceflow.tenant.model.*;
import com.insuranceflow.tenant.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/portal")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CLIENTE')")
@Tag(name = "Portal do Cliente", description = "Endpoints para o cliente final consultar suas informações")
public class PortalClienteController {

    private final ApoliceRepository apoliceRepo;
    private final SinistroRepository sinistroRepo;
    private final BoletoRepository boletoRepo;
    private final ClienteRepository clienteRepo;
    private final PropostaRepository propostaRepo;
    private final NotificacaoRepository notificacaoRepo;
    private final ConfiguracaoWhiteLabelRepository whiteLabelRepo;
    private final AssinaturaDigitalRepository assinaturaRepo;

    private Optional<Cliente> findClienteByPrincipal(UserPrincipal principal) {
        return clienteRepo.findByEmpresaIdAndActiveTrue(principal.getEmpresaId(), org.springframework.data.domain.Pageable.unpaged())
                .getContent().stream()
                .filter(c -> principal.getEmail().equals(c.getEmail()))
                .findFirst();
    }

    @GetMapping("/apolices")
    @Operation(summary = "Minhas apólices")
    public ResponseEntity<ApiResponse<List<Apolice>>> minhasApolices(@AuthenticationPrincipal UserPrincipal principal) {
        var cliente = findClienteByPrincipal(principal);
        if (cliente.isEmpty()) return ResponseEntity.ok(ApiResponse.ok(List.of()));
        return ResponseEntity.ok(ApiResponse.ok(
                apoliceRepo.findByClienteIdAndEmpresaIdAndActiveTrue(cliente.get().getId(), principal.getEmpresaId())));
    }

    @GetMapping("/sinistros")
    @Operation(summary = "Meus sinistros")
    public ResponseEntity<ApiResponse<List<Sinistro>>> meusSinistros(@AuthenticationPrincipal UserPrincipal principal) {
        var cliente = findClienteByPrincipal(principal);
        if (cliente.isEmpty()) return ResponseEntity.ok(ApiResponse.ok(List.of()));
        return ResponseEntity.ok(ApiResponse.ok(
                sinistroRepo.findByClienteIdAndEmpresaIdAndActiveTrue(cliente.get().getId(), principal.getEmpresaId())));
    }

    @GetMapping("/boletos")
    @Operation(summary = "Meus boletos")
    public ResponseEntity<ApiResponse<List<Boleto>>> meusBoletos(@AuthenticationPrincipal UserPrincipal principal) {
        var cliente = findClienteByPrincipal(principal);
        if (cliente.isEmpty()) return ResponseEntity.ok(ApiResponse.ok(List.of()));
        return ResponseEntity.ok(ApiResponse.ok(
                boletoRepo.findByClienteIdAndEmpresaIdAndActiveTrue(cliente.get().getId(), principal.getEmpresaId())));
    }

    @GetMapping("/propostas")
    @Operation(summary = "Minhas propostas")
    public ResponseEntity<ApiResponse<List<Proposta>>> minhasPropostas(@AuthenticationPrincipal UserPrincipal principal) {
        var cliente = findClienteByPrincipal(principal);
        if (cliente.isEmpty()) return ResponseEntity.ok(ApiResponse.ok(List.of()));
        return ResponseEntity.ok(ApiResponse.ok(
                propostaRepo.findByClienteIdAndEmpresaIdAndActiveTrue(cliente.get().getId(), principal.getEmpresaId())));
    }

    @GetMapping("/perfil")
    @Operation(summary = "Meu perfil")
    public ResponseEntity<ApiResponse<Map<String, Object>>> meuPerfil(@AuthenticationPrincipal UserPrincipal principal) {
        var cliente = findClienteByPrincipal(principal);
        Map<String, Object> perfil = Map.of(
                "nome", principal.getNome(),
                "email", principal.getEmail(),
                "cliente", cliente.orElse(null) != null ? cliente.get() : Map.of()
        );
        return ResponseEntity.ok(ApiResponse.ok(perfil));
    }

    @GetMapping("/notificacoes")
    @Operation(summary = "Minhas notificações")
    public ResponseEntity<ApiResponse<Page<Notificacao>>> minhasNotificacoes(@AuthenticationPrincipal UserPrincipal principal, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(notificacaoRepo.findByUsuarioIdAndActiveTrue(principal.getId(), pageable)));
    }

    @GetMapping("/notificacoes/count")
    @Operation(summary = "Contar notificações não lidas")
    public ResponseEntity<ApiResponse<Map<String, Long>>> contarNotificacoes(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok(Map.of("naoLidas", notificacaoRepo.countByUsuarioIdAndLidaFalse(principal.getId()))));
    }

    @GetMapping("/white-label")
    @Operation(summary = "Configuração visual da empresa")
    public ResponseEntity<ApiResponse<ConfiguracaoWhiteLabel>> getWhiteLabel(@AuthenticationPrincipal UserPrincipal principal) {
        var wl = whiteLabelRepo.findByEmpresaId(principal.getEmpresaId()).orElse(null);
        return ResponseEntity.ok(ApiResponse.ok(wl));
    }

    @GetMapping("/assinaturas")
    @Operation(summary = "Minhas assinaturas pendentes")
    public ResponseEntity<ApiResponse<List<AssinaturaDigital>>> minhasAssinaturas(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok(
                assinaturaRepo.findBySignatarioEmailAndEmpresaIdAndActiveTrue(principal.getEmail(), principal.getEmpresaId())));
    }
}
