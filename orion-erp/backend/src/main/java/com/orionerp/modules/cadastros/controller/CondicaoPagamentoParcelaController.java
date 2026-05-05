package com.orionerp.modules.cadastros.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.cadastros.domain.CondicaoPagamento;
import com.orionerp.modules.cadastros.dto.CondicaoPagamentoParcelaResponse;
import com.orionerp.modules.cadastros.repository.CondicaoPagamentoRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cadastros/condicao-pagamento-parcelas")
@RequiredArgsConstructor
@Validated
public class CondicaoPagamentoParcelaController {

    private final CondicaoPagamentoRepository condicaoPagamentoRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CondicaoPagamentoParcelaResponse>>> list(
            @RequestParam @NotNull Long condicaoPagamentoId) {
        CondicaoPagamento condicao = condicaoPagamentoRepository.findByIdAndDeletedFalse(condicaoPagamentoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Condição de pagamento não encontrada: " + condicaoPagamentoId));

        List<CondicaoPagamentoParcelaResponse> parcelas = condicao.getParcelas().stream()
                .map(CondicaoPagamentoParcelaResponse::from)
                .toList();

        return ResponseEntity.ok(ApiResponse.ok(parcelas));
    }
}
