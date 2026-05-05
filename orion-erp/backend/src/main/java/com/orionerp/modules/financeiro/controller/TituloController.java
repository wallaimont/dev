package com.orionerp.modules.financeiro.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.financeiro.dto.BaixaRequest;
import com.orionerp.modules.financeiro.dto.BaixaResponse;
import com.orionerp.modules.financeiro.dto.TituloRequest;
import com.orionerp.modules.financeiro.dto.TituloResponse;
import com.orionerp.modules.financeiro.service.TituloService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/financeiro/titulos")
@RequiredArgsConstructor
@Tag(name = "Financeiro - Titulos")
public class TituloController {

    private final TituloService tituloService;

    @GetMapping
    @PreAuthorize("hasAuthority('contas_pagar:listar') or hasAuthority('contas_receber:listar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<TituloResponse>>> list(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) Long filialId,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) Long fornecedorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) String term,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(
                tituloService.list(empresaId, filialId, tipo, status, clienteId, fornecedorId,
                        dataInicio, dataFim, term, page, size)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('contas_pagar:listar') or hasAuthority('contas_receber:listar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<TituloResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(tituloService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('contas_pagar:criar') or hasAuthority('contas_receber:criar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<TituloResponse>> create(@Valid @RequestBody TituloRequest request) {
        return ResponseEntity.ok(ApiResponse.created(tituloService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('financeiro:editar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<TituloResponse>> update(@PathVariable Long id,
                                                              @Valid @RequestBody TituloRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(tituloService.update(id, request), "Titulo atualizado com sucesso"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('financeiro:excluir') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        tituloService.delete(id);
        return ResponseEntity.ok(ApiResponse.deleted());
    }

    @PostMapping("/{id}/parcelas/{parcelaId}/baixas")
    @PreAuthorize("hasAuthority('contas_pagar:baixar') or hasAuthority('contas_receber:baixar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<BaixaResponse>> baixar(
            @PathVariable Long id,
            @PathVariable Long parcelaId,
            @Valid @RequestBody BaixaRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.created(
                tituloService.baixar(id, parcelaId, request, userDetails.getUsername())));
    }

    @PostMapping("/{id}/parcelas/{parcelaId}/baixas/{baixaId}/estorno")
    @PreAuthorize("hasAuthority('financeiro:estornar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<BaixaResponse>> estornar(
            @PathVariable Long id,
            @PathVariable Long parcelaId,
            @PathVariable Long baixaId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok(
                tituloService.estornar(id, parcelaId, baixaId, userDetails.getUsername()),
                "Baixa estornada com sucesso"));
    }

    @GetMapping("/{id}/parcelas/{parcelaId}/baixas")
    @PreAuthorize("hasAuthority('contas_pagar:listar') or hasAuthority('contas_receber:listar') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<BaixaResponse>>> listarBaixas(
            @PathVariable Long id,
            @PathVariable Long parcelaId) {
        return ResponseEntity.ok(ApiResponse.ok(tituloService.listarBaixas(id, parcelaId)));
    }
}
