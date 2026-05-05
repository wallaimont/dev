package com.orionerp.modules.estoque.service;

import com.orionerp.common.PageResponse;
import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.cadastros.domain.Produto;
import com.orionerp.modules.cadastros.repository.ProdutoRepository;
import com.orionerp.modules.estoque.domain.Armazem;
import com.orionerp.modules.estoque.domain.Localizacao;
import com.orionerp.modules.estoque.domain.MovimentacaoEstoque;
import com.orionerp.modules.estoque.domain.SaldoEstoque;
import com.orionerp.modules.estoque.dto.MovimentacaoRequest;
import com.orionerp.modules.estoque.dto.MovimentacaoResponse;
import com.orionerp.modules.estoque.dto.SaldoEstoqueResponse;
import com.orionerp.modules.estoque.repository.LocalizacaoRepository;
import com.orionerp.modules.estoque.repository.MovimentacaoEstoqueRepository;
import com.orionerp.modules.estoque.repository.SaldoEstoqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MovimentacaoEstoqueService {

    private final MovimentacaoEstoqueRepository movimentacaoRepository;
    private final SaldoEstoqueRepository saldoRepository;
    private final ArmazemService armazemService;
    private final ProdutoRepository produtoRepository;
    private final LocalizacaoRepository localizacaoRepository;

    @Transactional(readOnly = true)
    public PageResponse<MovimentacaoResponse> list(Long empresaId, Long filialId, Long armazemId,
                                                   Long produtoId, String tipo,
                                                   LocalDate dataInicio, LocalDate dataFim,
                                                   int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var spec = EstoqueSpecifications.movimentacaoFilter(empresaId, filialId, armazemId, produtoId,
                tipo, dataInicio, dataFim);
        var result = movimentacaoRepository.findAll(spec, pageable).map(this::toResponse);
        return PageResponse.from(result);
    }

    @Transactional(readOnly = true)
    public PageResponse<SaldoEstoqueResponse> listSaldos(Long empresaId, Long filialId,
                                                         Long armazemId, Long produtoId,
                                                         int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
        var spec = EstoqueSpecifications.saldoFilter(empresaId, filialId, armazemId, produtoId);
        var result = saldoRepository.findAll(spec, pageable).map(this::toSaldoResponse);
        return PageResponse.from(result);
    }

    @Transactional
    public MovimentacaoResponse movimentar(MovimentacaoRequest request, String username) {
        Armazem armazem = armazemService.findById(request.armazemId());
        Produto produto = produtoRepository.findByIdAndDeletedFalse(request.produtoId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto nao encontrado"));

        final Localizacao localizacao = request.localizacaoId() != null
                ? localizacaoRepository.findById(request.localizacaoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Localizacao nao encontrada"))
                : null;

        String lote = request.lote() != null ? request.lote().trim() : null;
        BigDecimal custoUnitario = request.custoUnitario() != null ? request.custoUnitario() : BigDecimal.ZERO;
        BigDecimal quantidade = request.quantidade();

        SaldoEstoque saldo = saldoRepository.findByEmpresaIdAndFilialIdAndArmazemIdAndProdutoIdAndLote(
                        request.empresaId(), request.filialId(), armazem.getId(), produto.getId(), lote)
                .orElseGet(() -> {
                    SaldoEstoque novo = new SaldoEstoque();
                    novo.setEmpresaId(request.empresaId());
                    novo.setFilialId(request.filialId());
                    novo.setArmazem(armazem);
                    novo.setLocalizacao(localizacao);
                    novo.setProduto(produto);
                    novo.setLote(lote);
                    return novo;
                });

        BigDecimal saldoAnterior = saldo.getQuantidade();
        BigDecimal saldoPosterior;
        BigDecimal custoTotal;

        switch (request.tipo().toUpperCase()) {
            case "ENTRADA" -> {
                saldoPosterior = saldoAnterior.add(quantidade);
                custoTotal = custoUnitario.multiply(quantidade);
                // Recalcular custo médio
                BigDecimal valorEstoqueAtual = saldo.getCustoMedio().multiply(saldoAnterior);
                BigDecimal valorTotal = valorEstoqueAtual.add(custoTotal);
                if (saldoPosterior.compareTo(BigDecimal.ZERO) > 0) {
                    saldo.setCustoMedio(valorTotal.divide(saldoPosterior, 4, RoundingMode.HALF_UP));
                }
                saldo.setQuantidade(saldoPosterior);
            }
            case "SAIDA" -> {
                if (saldoAnterior.compareTo(quantidade) < 0) {
                    throw new BusinessException("Saldo insuficiente para o produto " + produto.getCodigo());
                }
                saldoPosterior = saldoAnterior.subtract(quantidade);
                custoTotal = saldo.getCustoMedio().multiply(quantidade);
                saldo.setQuantidade(saldoPosterior);
            }
            case "AJUSTE" -> {
                saldoPosterior = quantidade;
                custoTotal = custoUnitario.multiply(quantidade.subtract(saldoAnterior).abs());
                saldo.setQuantidade(saldoPosterior);
                if (custoUnitario.compareTo(BigDecimal.ZERO) > 0) {
                    saldo.setCustoMedio(custoUnitario);
                }
            }
            default -> throw new BusinessException("Tipo de movimentacao invalido: " + request.tipo());
        }

        saldo.setUpdatedAt(LocalDateTime.now());
        saldoRepository.save(saldo);

        MovimentacaoEstoque mov = new MovimentacaoEstoque();
        mov.setEmpresaId(request.empresaId());
        mov.setFilialId(request.filialId());
        mov.setArmazem(armazem);
        mov.setLocalizacao(localizacao);
        mov.setProduto(produto);
        mov.setTipo(request.tipo().toUpperCase());
        mov.setQuantidade(quantidade);
        mov.setCustoUnitario(custoUnitario);
        mov.setCustoTotal(custoTotal);
        mov.setSaldoAnterior(saldoAnterior);
        mov.setSaldoPosterior(saldoPosterior);
        mov.setLote(lote);
        mov.setValidade(request.validade());
        mov.setDocumentoTipo(request.documentoTipo());
        mov.setDocumentoId(request.documentoId());
        mov.setDocumentoNumero(request.documentoNumero());
        mov.setObservacao(request.observacao());
        mov.setCreatedBy(username);

        return toResponse(movimentacaoRepository.save(mov));
    }

    private MovimentacaoResponse toResponse(MovimentacaoEstoque m) {
        return new MovimentacaoResponse(
                m.getId(), m.getUuid(), m.getEmpresaId(), m.getFilialId(),
                m.getArmazem().getId(), m.getArmazem().getNome(),
                m.getLocalizacao() != null ? m.getLocalizacao().getId() : null,
                m.getProduto().getId(), m.getProduto().getCodigo(), m.getProduto().getNome(),
                m.getTipo(), m.getQuantidade(), m.getCustoUnitario(), m.getCustoTotal(),
                m.getSaldoAnterior(), m.getSaldoPosterior(),
                m.getLote(), m.getValidade(),
                m.getDocumentoTipo(), m.getDocumentoId(), m.getDocumentoNumero(),
                m.getObservacao(), m.getCreatedAt());
    }

    private SaldoEstoqueResponse toSaldoResponse(SaldoEstoque s) {
        return new SaldoEstoqueResponse(
                s.getId(), s.getEmpresaId(), s.getFilialId(),
                s.getArmazem().getId(), s.getArmazem().getNome(),
                s.getLocalizacao() != null ? s.getLocalizacao().getId() : null,
                s.getProduto().getId(), s.getProduto().getCodigo(), s.getProduto().getNome(),
                s.getLote(), s.getValidade(),
                s.getQuantidade(), s.getCustoMedio(), s.getReservado(), s.getDisponivel());
    }
}
