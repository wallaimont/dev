package com.orionerp.modules.compras.service;

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
import com.orionerp.modules.compras.repository.PedidoCompraRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoCompraServiceTest {

    @Mock private PedidoCompraRepository pedidoCompraRepository;
    @Mock private FornecedorRepository fornecedorRepository;
    @Mock private ProdutoRepository produtoRepository;

    @InjectMocks private PedidoCompraService service;

    private Fornecedor fornecedor;
    private Produto produto;

    @BeforeEach
    void setUp() {
        fornecedor = new Fornecedor();
        fornecedor.setId(1L);
        fornecedor.setRazaoSocial("Fornecedor Teste");
        fornecedor.setCodigo("F001");

        produto = new Produto();
        produto.setId(1L);
        produto.setCodigo("P001");
        produto.setNome("Produto Teste");
    }

    @Test
    void deveCriarPedidoCompraComItens() {
        var itemReq = new PedidoCompraRequest.ItemInput(1L, new BigDecimal("10"), new BigDecimal("25.50"), null);
        var request = new PedidoCompraRequest(1L, 1L, "PC-001", 1L, null, null,
                BigDecimal.ZERO, BigDecimal.ZERO, null, List.of(itemReq));

        when(pedidoCompraRepository.existsByEmpresaIdAndFilialIdAndNumeroAndDeletedFalse(1L, 1L, "PC-001")).thenReturn(false);
        when(fornecedorRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(fornecedor));
        when(produtoRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(produto));
        when(pedidoCompraRepository.save(any(PedidoCompra.class))).thenAnswer(inv -> {
            PedidoCompra p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        PedidoCompraResponse response = service.create(request, "admin");

        assertNotNull(response);
        assertEquals("PC-001", response.numero());
        assertEquals("PENDENTE", response.status());
        assertEquals(1, response.itens().size());
        assertEquals(0, new BigDecimal("255.00").compareTo(response.valorTotal()));
    }

    @Test
    void deveRejeitarNumeroDuplicado() {
        var itemReq = new PedidoCompraRequest.ItemInput(1L, BigDecimal.ONE, BigDecimal.TEN, null);
        var request = new PedidoCompraRequest(1L, 1L, "PC-001", 1L, null, null,
                null, null, null, List.of(itemReq));

        when(pedidoCompraRepository.existsByEmpresaIdAndFilialIdAndNumeroAndDeletedFalse(1L, 1L, "PC-001")).thenReturn(true);

        assertThrows(BusinessException.class, () -> service.create(request, "admin"));
    }

    @Test
    void deveAprovarPedidoPendente() {
        PedidoCompra pedido = criarPedidoExistente("PENDENTE");
        when(pedidoCompraRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(pedido));
        when(pedidoCompraRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PedidoCompraResponse response = service.aprovar(1L, "admin");

        assertEquals("APROVADO", response.status());
    }

    @Test
    void deveRejeitarAprovacaoPedidoNaoPendente() {
        PedidoCompra pedido = criarPedidoExistente("APROVADO");
        when(pedidoCompraRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(pedido));

        assertThrows(BusinessException.class, () -> service.aprovar(1L, "admin"));
    }

    @Test
    void deveCancelarPedidoPendente() {
        PedidoCompra pedido = criarPedidoExistente("PENDENTE");
        when(pedidoCompraRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(pedido));
        when(pedidoCompraRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PedidoCompraResponse response = service.cancelar(1L, "admin");

        assertEquals("CANCELADO", response.status());
    }

    @Test
    void deveRejeitarCancelamentoPedidoRecebido() {
        PedidoCompra pedido = criarPedidoExistente("RECEBIDO");
        when(pedidoCompraRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(pedido));

        assertThrows(BusinessException.class, () -> service.cancelar(1L, "admin"));
    }

    @Test
    void deveAtualizarStatusParaParcialQuandoRecebidoParcial() {
        PedidoCompra pedido = criarPedidoExistente("APROVADO");
        pedido.getItens().get(0).setQuantidadeRecebida(new BigDecimal("5"));

        service.atualizarStatusRecebimento(pedido);

        assertEquals("PARCIAL", pedido.getStatus());
    }

    @Test
    void deveAtualizarStatusParaRecebidoQuandoTudoRecebido() {
        PedidoCompra pedido = criarPedidoExistente("APROVADO");
        pedido.getItens().get(0).setQuantidadeRecebida(new BigDecimal("10"));

        service.atualizarStatusRecebimento(pedido);

        assertEquals("RECEBIDO", pedido.getStatus());
    }

    private PedidoCompra criarPedidoExistente(String status) {
        PedidoCompra pedido = new PedidoCompra();
        pedido.setId(1L);
        pedido.setEmpresaId(1L);
        pedido.setFilialId(1L);
        pedido.setNumero("PC-001");
        pedido.setFornecedor(fornecedor);
        pedido.setDataPedido(LocalDate.now());
        pedido.setStatus(status);
        pedido.setValorTotal(new BigDecimal("255.00"));
        pedido.setValorFrete(BigDecimal.ZERO);
        pedido.setValorDesconto(BigDecimal.ZERO);

        PedidoCompraItem item = new PedidoCompraItem();
        item.setId(1L);
        item.setProduto(produto);
        item.setQuantidade(new BigDecimal("10"));
        item.setQuantidadeRecebida(BigDecimal.ZERO);
        item.setPrecoUnitario(new BigDecimal("25.50"));
        item.setValorTotal(new BigDecimal("255.00"));
        pedido.addItem(item);

        return pedido;
    }
}
