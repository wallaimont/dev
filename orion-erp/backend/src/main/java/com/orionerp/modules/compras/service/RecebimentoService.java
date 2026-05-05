package com.orionerp.modules.compras.service;

import com.orionerp.common.PageResponse;
import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.cadastros.domain.Produto;
import com.orionerp.modules.cadastros.repository.ProdutoRepository;
import com.orionerp.modules.compras.domain.PedidoCompra;
import com.orionerp.modules.compras.domain.PedidoCompraItem;
import com.orionerp.modules.compras.domain.Recebimento;
import com.orionerp.modules.compras.domain.RecebimentoItem;
import com.orionerp.modules.compras.dto.RecebimentoRequest;
import com.orionerp.modules.compras.dto.RecebimentoResponse;
import com.orionerp.modules.compras.repository.ComprasSpecifications;
import com.orionerp.modules.compras.repository.PedidoCompraItemRepository;
import com.orionerp.modules.compras.repository.RecebimentoRepository;
import com.orionerp.modules.estoque.dto.MovimentacaoRequest;
import com.orionerp.modules.estoque.service.MovimentacaoEstoqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecebimentoService {

    private final RecebimentoRepository recebimentoRepository;
    private final PedidoCompraService pedidoCompraService;
    private final PedidoCompraItemRepository pedidoCompraItemRepository;
    private final ProdutoRepository produtoRepository;
    private final MovimentacaoEstoqueService movimentacaoEstoqueService;

    @Transactional(readOnly = true)
    public PageResponse<RecebimentoResponse> list(Long empresaId, Long filialId, Long fornecedorId,
                                                   String status, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataRecebimento"));
        var spec = ComprasSpecifications.recebimentoFilter(empresaId, filialId, fornecedorId, status);
        var result = recebimentoRepository.findAll(spec, pageable).map(this::toResponse);
        return PageResponse.from(result);
    }

    @Transactional(readOnly = true)
    public RecebimentoResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public RecebimentoResponse create(RecebimentoRequest request, String username) {
        PedidoCompra pedido = pedidoCompraService.findById(request.pedidoCompraId());

        if (!"APROVADO".equals(pedido.getStatus()) && !"PARCIAL".equals(pedido.getStatus())) {
            throw new BusinessException("Pedido deve estar APROVADO ou PARCIAL para recebimento. Status atual: " + pedido.getStatus());
        }

        if (recebimentoRepository.existsByEmpresaIdAndFilialIdAndNumeroAndDeletedFalse(
                request.empresaId(), request.filialId(), request.numero())) {
            throw new BusinessException("Ja existe um recebimento com o numero " + request.numero());
        }

        Recebimento recebimento = new Recebimento();
        recebimento.setEmpresaId(request.empresaId());
        recebimento.setFilialId(request.filialId());
        recebimento.setNumero(request.numero().trim().toUpperCase());
        recebimento.setPedidoCompra(pedido);
        recebimento.setFornecedor(pedido.getFornecedor());
        recebimento.setNumeroNf(request.numeroNf());
        recebimento.setSerieNf(request.serieNf());
        recebimento.setChaveNfe(request.chaveNfe());
        recebimento.setObservacao(request.observacao());
        recebimento.setCreatedBy(username);
        recebimento.setUpdatedBy(username);

        for (RecebimentoRequest.ItemInput itemReq : request.itens()) {
            Produto produto = produtoRepository.findByIdAndDeletedFalse(itemReq.produtoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto nao encontrado: " + itemReq.produtoId()));

            RecebimentoItem item = new RecebimentoItem();
            item.setProduto(produto);
            item.setQuantidade(itemReq.quantidade());
            item.setPrecoUnitario(itemReq.precoUnitario());
            item.setValorTotal(itemReq.quantidade().multiply(itemReq.precoUnitario()).setScale(2, RoundingMode.HALF_UP));
            item.setLote(itemReq.lote());
            item.setValidade(itemReq.validade());
            item.setArmazemId(itemReq.armazemId());
            item.setLocalizacaoId(itemReq.localizacaoId());
            item.setPedidoCompraItemId(itemReq.pedidoCompraItemId());
            recebimento.addItem(item);

            // Atualizar quantidade recebida no item do pedido
            if (itemReq.pedidoCompraItemId() != null) {
                PedidoCompraItem pedidoItem = pedidoCompraItemRepository.findById(itemReq.pedidoCompraItemId())
                        .orElseThrow(() -> new ResourceNotFoundException("Item do pedido nao encontrado"));
                pedidoItem.setQuantidadeRecebida(pedidoItem.getQuantidadeRecebida().add(itemReq.quantidade()));
                pedidoItem.setUpdatedAt(LocalDateTime.now());
            }
        }

        recebimento.recalcularTotal();
        Recebimento saved = recebimentoRepository.save(recebimento);

        // Atualizar status do pedido
        pedidoCompraService.atualizarStatusRecebimento(pedido);

        return toResponse(saved);
    }

    @Transactional
    public RecebimentoResponse finalizar(Long id, String username) {
        Recebimento recebimento = findById(id);
        if (!"PENDENTE".equals(recebimento.getStatus()) && !"CONFERIDO".equals(recebimento.getStatus())) {
            throw new BusinessException("Recebimento deve estar PENDENTE ou CONFERIDO para finalizar. Status atual: " + recebimento.getStatus());
        }

        // Gerar movimentações de estoque ENTRADA para cada item
        for (RecebimentoItem item : recebimento.getItens()) {
            if (item.getArmazemId() != null) {
                MovimentacaoRequest movReq = new MovimentacaoRequest(
                        recebimento.getEmpresaId(),
                        recebimento.getFilialId(),
                        item.getArmazemId(),
                        item.getLocalizacaoId(),
                        item.getProduto().getId(),
                        "ENTRADA",
                        item.getQuantidade(),
                        item.getPrecoUnitario(),
                        item.getLote(),
                        item.getValidade(),
                        "RECEBIMENTO",
                        recebimento.getId(),
                        recebimento.getNumero(),
                        null
                );
                movimentacaoEstoqueService.movimentar(movReq, username);
            }
        }

        recebimento.setStatus("FINALIZADO");
        recebimento.setUpdatedBy(username);
        recebimento.setUpdatedAt(LocalDateTime.now());
        return toResponse(recebimentoRepository.save(recebimento));
    }

    private Recebimento findById(Long id) {
        return recebimentoRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recebimento nao encontrado"));
    }

    private RecebimentoResponse toResponse(Recebimento r) {
        List<RecebimentoResponse.ItemResponse> itens = r.getItens().stream()
                .map(i -> new RecebimentoResponse.ItemResponse(
                        i.getId(),
                        i.getProduto().getId(),
                        i.getProduto().getCodigo(),
                        i.getProduto().getNome(),
                        i.getQuantidade(),
                        i.getPrecoUnitario(),
                        i.getValorTotal(),
                        i.getLote(),
                        i.getValidade()
                )).toList();

        return new RecebimentoResponse(
                r.getId(),
                r.getNumero(),
                r.getEmpresaId(),
                r.getFilialId(),
                r.getPedidoCompra().getId(),
                r.getPedidoCompra().getNumero(),
                r.getFornecedor().getId(),
                r.getFornecedor().getRazaoSocial(),
                r.getDataRecebimento(),
                r.getNumeroNf(),
                r.getSerieNf(),
                r.getChaveNfe(),
                r.getValorTotal(),
                r.getStatus(),
                r.getObservacao(),
                itens
        );
    }
}
