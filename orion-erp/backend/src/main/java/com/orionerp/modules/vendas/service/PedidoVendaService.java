package com.orionerp.modules.vendas.service;

import com.orionerp.common.PageResponse;
import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.cadastros.domain.Cliente;
import com.orionerp.modules.cadastros.domain.Produto;
import com.orionerp.modules.cadastros.repository.ClienteRepository;
import com.orionerp.modules.cadastros.repository.ProdutoRepository;
import com.orionerp.modules.vendas.domain.Comissao;
import com.orionerp.modules.vendas.domain.PedidoVenda;
import com.orionerp.modules.vendas.domain.PedidoVendaItem;
import com.orionerp.modules.vendas.dto.PedidoVendaRequest;
import com.orionerp.modules.vendas.dto.PedidoVendaResponse;
import com.orionerp.modules.vendas.repository.ComissaoRepository;
import com.orionerp.modules.vendas.repository.PedidoVendaRepository;
import com.orionerp.modules.vendas.repository.VendasSpecifications;
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
public class PedidoVendaService {

    private final PedidoVendaRepository pedidoVendaRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final ComissaoRepository comissaoRepository;

    @Transactional(readOnly = true)
    public PageResponse<PedidoVendaResponse> list(Long empresaId, Long filialId, Long clienteId,
                                                   Long vendedorId, String status, String term,
                                                   int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataPedido"));
        var spec = VendasSpecifications.pedidoVendaFilter(empresaId, filialId, clienteId, vendedorId, status, term);
        var result = pedidoVendaRepository.findAll(spec, pageable).map(this::toResponse);
        return PageResponse.from(result);
    }

    @Transactional(readOnly = true)
    public PedidoVendaResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public PedidoVendaResponse create(PedidoVendaRequest request, String username) {
        validateUniqueNumero(request.empresaId(), request.filialId(), request.numero());

        Cliente cliente = clienteRepository.findByIdAndDeletedFalse(request.clienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente nao encontrado"));

        if (cliente.getBloqueioComercial()) {
            throw new BusinessException("Cliente com bloqueio comercial: " + cliente.getRazaoSocial());
        }

        PedidoVenda pedido = new PedidoVenda();
        pedido.setEmpresaId(request.empresaId());
        pedido.setFilialId(request.filialId());
        pedido.setNumero(request.numero().trim().toUpperCase());
        pedido.setCliente(cliente);
        pedido.setVendedorId(request.vendedorId());
        pedido.setDataPedido(LocalDate.now());
        pedido.setDataPrevisaoEntrega(request.dataPrevisaoEntrega());
        pedido.setValorFrete(request.valorFrete() != null ? request.valorFrete() : BigDecimal.ZERO);
        pedido.setValorDesconto(request.valorDesconto() != null ? request.valorDesconto() : BigDecimal.ZERO);
        pedido.setObservacao(request.observacao());
        pedido.setCreatedBy(username);
        pedido.setUpdatedBy(username);

        for (PedidoVendaRequest.ItemInput itemReq : request.itens()) {
            Produto produto = produtoRepository.findByIdAndDeletedFalse(itemReq.produtoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto nao encontrado: " + itemReq.produtoId()));

            PedidoVendaItem item = new PedidoVendaItem();
            item.setProduto(produto);
            item.setQuantidade(itemReq.quantidade());
            item.setPrecoUnitario(itemReq.precoUnitario());
            BigDecimal percDesc = itemReq.percentualDesconto() != null ? itemReq.percentualDesconto() : BigDecimal.ZERO;
            item.setPercentualDesconto(percDesc);
            BigDecimal subtotal = itemReq.quantidade().multiply(itemReq.precoUnitario());
            BigDecimal descItem = subtotal.multiply(percDesc).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            item.setValorDesconto(descItem);
            item.setValorTotal(subtotal.subtract(descItem).setScale(2, RoundingMode.HALF_UP));
            item.setUnidadeMedidaId(itemReq.unidadeMedidaId());
            item.setArmazemId(itemReq.armazemId());
            item.setObservacao(itemReq.observacao());
            pedido.addItem(item);
        }

        pedido.recalcularTotais();
        return toResponse(pedidoVendaRepository.save(pedido));
    }

    @Transactional
    public PedidoVendaResponse aprovar(Long id, Long aprovadorId, BigDecimal percentualComissao, String username) {
        PedidoVenda pedido = findById(id);
        if (!"PENDENTE".equals(pedido.getStatus())) {
            throw new BusinessException("Pedido deve estar PENDENTE para ser aprovado. Status atual: " + pedido.getStatus());
        }

        // Verificar limite de crédito
        Cliente cliente = pedido.getCliente();
        if (cliente.getBloqueioFinanceiro()) {
            throw new BusinessException("Cliente com bloqueio financeiro: " + cliente.getRazaoSocial());
        }

        BigDecimal saldoDisponivel = cliente.getLimiteCredito().subtract(cliente.getSaldoDevedor());
        if (pedido.getValorTotal().compareTo(saldoDisponivel) > 0 && cliente.getLimiteCredito().compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessException("Valor do pedido excede o limite de credito disponivel do cliente");
        }

        pedido.setStatus("APROVADO");
        pedido.setAprovadoPor(aprovadorId);
        pedido.setAprovadoEm(LocalDateTime.now());
        pedido.setUpdatedBy(username);
        pedido.setUpdatedAt(LocalDateTime.now());

        // Gerar comissão se vendedor e percentual informados
        if (pedido.getVendedorId() != null && percentualComissao != null && percentualComissao.compareTo(BigDecimal.ZERO) > 0) {
            Comissao comissao = new Comissao();
            comissao.setEmpresaId(pedido.getEmpresaId());
            comissao.setFilialId(pedido.getFilialId());
            comissao.setVendedorId(pedido.getVendedorId());
            comissao.setPedidoVendaId(pedido.getId());
            comissao.setPercentual(percentualComissao);
            comissao.setValorBase(pedido.getValorTotal());
            comissao.setValorComissao(pedido.getValorTotal()
                    .multiply(percentualComissao)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
            comissaoRepository.save(comissao);
        }

        return toResponse(pedidoVendaRepository.save(pedido));
    }

    @Transactional
    public PedidoVendaResponse reprovar(Long id, String motivo, String username) {
        PedidoVenda pedido = findById(id);
        if (!"PENDENTE".equals(pedido.getStatus())) {
            throw new BusinessException("Pedido deve estar PENDENTE para ser reprovado. Status atual: " + pedido.getStatus());
        }
        pedido.setStatus("REPROVADO");
        pedido.setBloqueioMotivo(motivo);
        pedido.setUpdatedBy(username);
        pedido.setUpdatedAt(LocalDateTime.now());
        return toResponse(pedidoVendaRepository.save(pedido));
    }

    @Transactional
    public PedidoVendaResponse cancelar(Long id, String username) {
        PedidoVenda pedido = findById(id);
        if ("CANCELADO".equals(pedido.getStatus()) || "FATURADO".equals(pedido.getStatus())) {
            throw new BusinessException("Pedido nao pode ser cancelado no status: " + pedido.getStatus());
        }
        pedido.setStatus("CANCELADO");
        pedido.setUpdatedBy(username);
        pedido.setUpdatedAt(LocalDateTime.now());

        // Cancelar comissões pendentes
        List<Comissao> comissoes = comissaoRepository.findByPedidoVendaId(pedido.getId());
        for (Comissao c : comissoes) {
            if ("PENDENTE".equals(c.getStatus())) {
                c.setStatus("CANCELADA");
                c.setUpdatedAt(LocalDateTime.now());
            }
        }
        comissaoRepository.saveAll(comissoes);

        return toResponse(pedidoVendaRepository.save(pedido));
    }

    PedidoVenda findById(Long id) {
        return pedidoVendaRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido de venda nao encontrado"));
    }

    private void validateUniqueNumero(Long empresaId, Long filialId, String numero) {
        if (pedidoVendaRepository.existsByEmpresaIdAndFilialIdAndNumeroAndDeletedFalse(empresaId, filialId, numero)) {
            throw new BusinessException("Ja existe um pedido de venda com o numero " + numero);
        }
    }

    private PedidoVendaResponse toResponse(PedidoVenda p) {
        List<PedidoVendaResponse.ItemResponse> itens = p.getItens().stream()
                .map(i -> new PedidoVendaResponse.ItemResponse(
                        i.getId(),
                        i.getProduto().getId(),
                        i.getProduto().getCodigo(),
                        i.getProduto().getNome(),
                        i.getQuantidade(),
                        i.getQuantidadeEntregue(),
                        i.getPrecoUnitario(),
                        i.getPercentualDesconto(),
                        i.getValorDesconto(),
                        i.getValorTotal()
                )).toList();

        return new PedidoVendaResponse(
                p.getId(),
                p.getNumero(),
                p.getEmpresaId(),
                p.getFilialId(),
                p.getCliente().getId(),
                p.getCliente().getRazaoSocial(),
                p.getVendedorId(),
                p.getTransportadora() != null ? p.getTransportadora().getId() : null,
                p.getDataPedido(),
                p.getDataPrevisaoEntrega(),
                p.getValorProdutos(),
                p.getValorDesconto(),
                p.getValorFrete(),
                p.getValorTotal(),
                p.getStatus(),
                p.getAprovadoPor(),
                p.getAprovadoEm(),
                p.getObservacao(),
                itens
        );
    }
}
