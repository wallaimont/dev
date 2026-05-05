package com.orionerp.modules.vendas.service;

import com.orionerp.exception.BusinessException;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoVendaServiceTest {

    @Mock private PedidoVendaRepository pedidoVendaRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private ProdutoRepository produtoRepository;
    @Mock private ComissaoRepository comissaoRepository;

    @InjectMocks private PedidoVendaService service;

    private Cliente cliente;
    private Produto produto;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setRazaoSocial("Cliente Teste Ltda");
        cliente.setCodigo("C001");
        cliente.setBloqueioComercial(false);
        cliente.setBloqueioFinanceiro(false);
        cliente.setLimiteCredito(new BigDecimal("50000"));
        cliente.setSaldoDevedor(BigDecimal.ZERO);

        produto = new Produto();
        produto.setId(1L);
        produto.setCodigo("P001");
        produto.setNome("Produto Teste");
    }

    @Test
    void deveCriarPedidoVendaComItens() {
        var itemReq = new PedidoVendaRequest.ItemInput(1L, new BigDecimal("5"),
                new BigDecimal("100.00"), BigDecimal.ZERO, null, null, null);
        var request = new PedidoVendaRequest(1L, 1L, "PV-001", 1L, null, null,
                null, null, null, BigDecimal.ZERO, BigDecimal.ZERO, null, null, List.of(itemReq));

        when(pedidoVendaRepository.existsByEmpresaIdAndFilialIdAndNumeroAndDeletedFalse(1L, 1L, "PV-001")).thenReturn(false);
        when(clienteRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(cliente));
        when(produtoRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(produto));
        when(pedidoVendaRepository.save(any(PedidoVenda.class))).thenAnswer(inv -> {
            PedidoVenda p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        PedidoVendaResponse response = service.create(request, "admin");

        assertNotNull(response);
        assertEquals("PV-001", response.numero());
        assertEquals("PENDENTE", response.status());
        assertEquals(1, response.itens().size());
        assertEquals(0, new BigDecimal("500.00").compareTo(response.valorTotal()));
    }

    @Test
    void deveRejeitarClienteComBloqueioComercial() {
        cliente.setBloqueioComercial(true);

        var itemReq = new PedidoVendaRequest.ItemInput(1L, BigDecimal.ONE, BigDecimal.TEN,
                null, null, null, null);
        var request = new PedidoVendaRequest(1L, 1L, "PV-001", 1L, null, null,
                null, null, null, null, null, null, null, List.of(itemReq));

        when(pedidoVendaRepository.existsByEmpresaIdAndFilialIdAndNumeroAndDeletedFalse(1L, 1L, "PV-001")).thenReturn(false);
        when(clienteRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(cliente));

        assertThrows(BusinessException.class, () -> service.create(request, "admin"));
    }

    @Test
    void deveAprovarPedidoEGerarComissao() {
        PedidoVenda pedido = criarPedidoExistente("PENDENTE");
        pedido.setVendedorId(10L);

        when(pedidoVendaRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(pedido));
        when(pedidoVendaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(comissaoRepository.save(any(Comissao.class))).thenAnswer(inv -> inv.getArgument(0));

        PedidoVendaResponse response = service.aprovar(1L, 99L, new BigDecimal("5"), "admin");

        assertEquals("APROVADO", response.status());

        ArgumentCaptor<Comissao> comCaptor = ArgumentCaptor.forClass(Comissao.class);
        verify(comissaoRepository).save(comCaptor.capture());
        Comissao comissao = comCaptor.getValue();
        assertEquals(10L, comissao.getVendedorId());
        assertEquals(0, new BigDecimal("5").compareTo(comissao.getPercentual()));
        // 500 * 5% = 25.00
        assertEquals(0, new BigDecimal("25.00").compareTo(comissao.getValorComissao()));
    }

    @Test
    void deveRejeitarAprovacaoClienteBloqueioFinanceiro() {
        PedidoVenda pedido = criarPedidoExistente("PENDENTE");
        pedido.getCliente().setBloqueioFinanceiro(true);

        when(pedidoVendaRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(pedido));

        assertThrows(BusinessException.class, () -> service.aprovar(1L, 99L, null, "admin"));
    }

    @Test
    void deveRejeitarAprovacaoExcedeLimiteCredito() {
        PedidoVenda pedido = criarPedidoExistente("PENDENTE");
        pedido.getCliente().setLimiteCredito(new BigDecimal("100"));
        pedido.setValorTotal(new BigDecimal("500"));

        when(pedidoVendaRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(pedido));

        assertThrows(BusinessException.class, () -> service.aprovar(1L, 99L, null, "admin"));
    }

    @Test
    void deveCancelarPedidoECancelarComissoes() {
        PedidoVenda pedido = criarPedidoExistente("PENDENTE");

        Comissao comissao = new Comissao();
        comissao.setId(1L);
        comissao.setStatus("PENDENTE");

        when(pedidoVendaRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(pedido));
        when(pedidoVendaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(comissaoRepository.findByPedidoVendaId(1L)).thenReturn(List.of(comissao));
        when(comissaoRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        PedidoVendaResponse response = service.cancelar(1L, "admin");

        assertEquals("CANCELADO", response.status());
        assertEquals("CANCELADA", comissao.getStatus());
    }

    @Test
    void deveCalcularDescontoNoItem() {
        var itemReq = new PedidoVendaRequest.ItemInput(1L, new BigDecimal("10"),
                new BigDecimal("100.00"), new BigDecimal("10"), null, null, null);
        var request = new PedidoVendaRequest(1L, 1L, "PV-002", 1L, null, null,
                null, null, null, BigDecimal.ZERO, BigDecimal.ZERO, null, null, List.of(itemReq));

        when(pedidoVendaRepository.existsByEmpresaIdAndFilialIdAndNumeroAndDeletedFalse(1L, 1L, "PV-002")).thenReturn(false);
        when(clienteRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(cliente));
        when(produtoRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(produto));
        when(pedidoVendaRepository.save(any(PedidoVenda.class))).thenAnswer(inv -> {
            PedidoVenda p = inv.getArgument(0);
            p.setId(2L);
            return p;
        });

        PedidoVendaResponse response = service.create(request, "admin");

        // 10 x 100 = 1000, 10% desc = 100, total item = 900
        assertEquals(0, new BigDecimal("900.00").compareTo(response.valorTotal()));
        assertEquals(0, new BigDecimal("100.00").compareTo(response.itens().get(0).valorDesconto()));
    }

    @Test
    void deveRejeitarCancelamentoPedidoFaturado() {
        PedidoVenda pedido = criarPedidoExistente("FATURADO");
        when(pedidoVendaRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(pedido));

        assertThrows(BusinessException.class, () -> service.cancelar(1L, "admin"));
    }

    private PedidoVenda criarPedidoExistente(String status) {
        PedidoVenda pedido = new PedidoVenda();
        pedido.setId(1L);
        pedido.setEmpresaId(1L);
        pedido.setFilialId(1L);
        pedido.setNumero("PV-001");
        pedido.setCliente(cliente);
        pedido.setDataPedido(LocalDate.now());
        pedido.setStatus(status);
        pedido.setValorProdutos(new BigDecimal("500.00"));
        pedido.setValorFrete(BigDecimal.ZERO);
        pedido.setValorDesconto(BigDecimal.ZERO);
        pedido.setValorTotal(new BigDecimal("500.00"));

        PedidoVendaItem item = new PedidoVendaItem();
        item.setId(1L);
        item.setProduto(produto);
        item.setQuantidade(new BigDecimal("5"));
        item.setQuantidadeEntregue(BigDecimal.ZERO);
        item.setPrecoUnitario(new BigDecimal("100.00"));
        item.setPercentualDesconto(BigDecimal.ZERO);
        item.setValorDesconto(BigDecimal.ZERO);
        item.setValorTotal(new BigDecimal("500.00"));
        pedido.addItem(item);

        return pedido;
    }
}
