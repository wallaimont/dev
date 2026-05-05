package com.orionerp.modules.compras.service;

import com.orionerp.common.PageResponse;
import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.cadastros.domain.Fornecedor;
import com.orionerp.modules.cadastros.domain.Produto;
import com.orionerp.modules.cadastros.repository.FornecedorRepository;
import com.orionerp.modules.cadastros.repository.ProdutoRepository;
import com.orionerp.modules.compras.domain.PedidoCompra;
import com.orionerp.modules.compras.domain.PedidoCompraItem;
import com.orionerp.modules.compras.dto.PedidoCompraRequest;
import com.orionerp.modules.compras.dto.PedidoCompraResponse;
import com.orionerp.modules.compras.repository.ComprasSpecifications;
import com.orionerp.modules.compras.repository.PedidoCompraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoCompraService {

    private final PedidoCompraRepository pedidoCompraRepository;
    private final FornecedorRepository fornecedorRepository;
    private final ProdutoRepository produtoRepository;

    @Transactional(readOnly = true)
    public PageResponse<PedidoCompraResponse> list(Long empresaId, Long filialId, Long fornecedorId,
                                                    String status, String term, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataPedido"));
        var spec = ComprasSpecifications.pedidoCompraFilter(empresaId, filialId, fornecedorId, status, term);
        var result = pedidoCompraRepository.findAll(spec, pageable).map(this::toResponse);
        return PageResponse.from(result);
    }

    @Transactional(readOnly = true)
    public PedidoCompraResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public PedidoCompraResponse create(PedidoCompraRequest request, String username) {
        validateUniqueNumero(request.empresaId(), request.filialId(), request.numero());

        Fornecedor fornecedor = fornecedorRepository.findByIdAndDeletedFalse(request.fornecedorId())
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor nao encontrado"));

        PedidoCompra pedido = new PedidoCompra();
        pedido.setEmpresaId(request.empresaId());
        pedido.setFilialId(request.filialId());
        pedido.setNumero(request.numero().trim().toUpperCase());
        pedido.setFornecedor(fornecedor);
        pedido.setDataPedido(LocalDate.now());
        pedido.setDataPrevisaoEntrega(request.dataPrevisaoEntrega());
        pedido.setValorFrete(request.valorFrete() != null ? request.valorFrete() : BigDecimal.ZERO);
        pedido.setValorDesconto(request.valorDesconto() != null ? request.valorDesconto() : BigDecimal.ZERO);
        pedido.setObservacao(request.observacao());
        pedido.setCreatedBy(username);
        pedido.setUpdatedBy(username);

        for (PedidoCompraRequest.ItemInput itemReq : request.itens()) {
            Produto produto = produtoRepository.findByIdAndDeletedFalse(itemReq.produtoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto nao encontrado: " + itemReq.produtoId()));

            PedidoCompraItem item = new PedidoCompraItem();
            item.setProduto(produto);
            item.setQuantidade(itemReq.quantidade());
            item.setPrecoUnitario(itemReq.precoUnitario());
            item.setValorTotal(itemReq.quantidade().multiply(itemReq.precoUnitario()).setScale(2, RoundingMode.HALF_UP));
            item.setUnidadeMedidaId(itemReq.unidadeMedidaId());
            pedido.addItem(item);
        }

        pedido.recalcularTotal();
        return toResponse(pedidoCompraRepository.save(pedido));
    }

    @Transactional
    public PedidoCompraResponse aprovar(Long id, String username) {
        PedidoCompra pedido = findById(id);
        if (!"PENDENTE".equals(pedido.getStatus())) {
            throw new BusinessException("Pedido deve estar PENDENTE para ser aprovado. Status atual: " + pedido.getStatus());
        }
        pedido.setStatus("APROVADO");
        pedido.setUpdatedBy(username);
        pedido.setUpdatedAt(LocalDateTime.now());
        return toResponse(pedidoCompraRepository.save(pedido));
    }

    @Transactional
    public PedidoCompraResponse reprovar(Long id, String username) {
        PedidoCompra pedido = findById(id);
        if (!"PENDENTE".equals(pedido.getStatus())) {
            throw new BusinessException("Pedido deve estar PENDENTE para ser reprovado. Status atual: " + pedido.getStatus());
        }
        pedido.setStatus("REPROVADO");
        pedido.setUpdatedBy(username);
        pedido.setUpdatedAt(LocalDateTime.now());
        return toResponse(pedidoCompraRepository.save(pedido));
    }

    @Transactional
    public PedidoCompraResponse cancelar(Long id, String username) {
        PedidoCompra pedido = findById(id);
        if ("CANCELADO".equals(pedido.getStatus()) || "RECEBIDO".equals(pedido.getStatus())) {
            throw new BusinessException("Pedido nao pode ser cancelado no status: " + pedido.getStatus());
        }
        pedido.setStatus("CANCELADO");
        pedido.setUpdatedBy(username);
        pedido.setUpdatedAt(LocalDateTime.now());
        return toResponse(pedidoCompraRepository.save(pedido));
    }

    PedidoCompra findById(Long id) {
        return pedidoCompraRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido de compra nao encontrado"));
    }

    void atualizarStatusRecebimento(PedidoCompra pedido) {
        boolean todosRecebidos = pedido.getItens().stream()
                .allMatch(i -> i.getQuantidadeRecebida().compareTo(i.getQuantidade()) >= 0);
        boolean algumRecebido = pedido.getItens().stream()
                .anyMatch(i -> i.getQuantidadeRecebida().compareTo(BigDecimal.ZERO) > 0);

        if (todosRecebidos) {
            pedido.setStatus("RECEBIDO");
        } else if (algumRecebido) {
            pedido.setStatus("PARCIAL");
        }
    }

    private void validateUniqueNumero(Long empresaId, Long filialId, String numero) {
        if (pedidoCompraRepository.existsByEmpresaIdAndFilialIdAndNumeroAndDeletedFalse(empresaId, filialId, numero)) {
            throw new BusinessException("Ja existe um pedido de compra com o numero " + numero);
        }
    }

    private PedidoCompraResponse toResponse(PedidoCompra p) {
        List<PedidoCompraResponse.ItemResponse> itens = p.getItens().stream()
                .map(i -> new PedidoCompraResponse.ItemResponse(
                        i.getId(),
                        i.getProduto().getId(),
                        i.getProduto().getCodigo(),
                        i.getProduto().getNome(),
                        i.getQuantidade(),
                        i.getQuantidadeRecebida(),
                        i.getPrecoUnitario(),
                        i.getValorTotal()
                )).toList();

        return new PedidoCompraResponse(
                p.getId(),
                p.getNumero(),
                p.getEmpresaId(),
                p.getFilialId(),
                p.getFornecedor().getId(),
                p.getFornecedor().getRazaoSocial(),
                p.getDataPedido(),
                p.getDataPrevisaoEntrega(),
                p.getValorTotal(),
                p.getValorFrete(),
                p.getValorDesconto(),
                p.getStatus(),
                p.getObservacao(),
                itens
        );
    }
}
