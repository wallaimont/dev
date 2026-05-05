package com.orionerp.modules.estoque.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.cadastros.domain.Produto;
import com.orionerp.modules.cadastros.repository.ProdutoRepository;
import com.orionerp.modules.estoque.domain.Armazem;
import com.orionerp.modules.estoque.domain.MovimentacaoEstoque;
import com.orionerp.modules.estoque.domain.SaldoEstoque;
import com.orionerp.modules.estoque.dto.MovimentacaoRequest;
import com.orionerp.modules.estoque.dto.MovimentacaoResponse;
import com.orionerp.modules.estoque.repository.LocalizacaoRepository;
import com.orionerp.modules.estoque.repository.MovimentacaoEstoqueRepository;
import com.orionerp.modules.estoque.repository.SaldoEstoqueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovimentacaoEstoqueServiceTest {

    @Mock private MovimentacaoEstoqueRepository movimentacaoRepository;
    @Mock private SaldoEstoqueRepository saldoRepository;
    @Mock private ArmazemService armazemService;
    @Mock private ProdutoRepository produtoRepository;
    @Mock private LocalizacaoRepository localizacaoRepository;

    private MovimentacaoEstoqueService service;

    private Armazem armazem;
    private Produto produto;

    @BeforeEach
    void setUp() {
        service = new MovimentacaoEstoqueService(
                movimentacaoRepository, saldoRepository, armazemService,
                produtoRepository, localizacaoRepository);

        armazem = new Armazem();
        armazem.setId(1L);
        armazem.setNome("Principal");
        armazem.setCodigo("ARM-01");

        produto = new Produto();
        produto.setId(10L);
        produto.setCodigo("PROD-01");
        produto.setNome("Produto Teste");
    }

    @Test
    void entradaShouldCreateSaldoWhenNotExists() {
        MovimentacaoRequest request = new MovimentacaoRequest(
                1L, 1L, 1L, null, 10L, "ENTRADA",
                new BigDecimal("100"), new BigDecimal("10.00"),
                null, null, null, null, null, null);

        when(armazemService.findById(1L)).thenReturn(armazem);
        when(produtoRepository.findByIdAndDeletedFalse(10L)).thenReturn(Optional.of(produto));
        when(saldoRepository.findByEmpresaIdAndFilialIdAndArmazemIdAndProdutoIdAndLote(1L, 1L, 1L, 10L, null))
                .thenReturn(Optional.empty());
        when(saldoRepository.save(any(SaldoEstoque.class))).thenAnswer(inv -> inv.getArgument(0));
        when(movimentacaoRepository.save(any(MovimentacaoEstoque.class))).thenAnswer(inv -> {
            MovimentacaoEstoque m = inv.getArgument(0);
            m.setId(1L);
            return m;
        });

        MovimentacaoResponse response = service.movimentar(request, "admin");

        assertNotNull(response);
        assertEquals("ENTRADA", response.tipo());
        assertEquals(0, new BigDecimal("100").compareTo(response.quantidade()));
        assertEquals(0, BigDecimal.ZERO.compareTo(response.saldoAnterior()));
        assertEquals(0, new BigDecimal("100").compareTo(response.saldoPosterior()));
        verify(saldoRepository).save(any(SaldoEstoque.class));
    }

    @Test
    void entradaShouldUpdateExistingSaldoAndCustoMedio() {
        SaldoEstoque saldo = new SaldoEstoque();
        saldo.setId(1L);
        saldo.setQuantidade(new BigDecimal("50"));
        saldo.setCustoMedio(new BigDecimal("8.0000"));
        saldo.setArmazem(armazem);
        saldo.setProduto(produto);

        MovimentacaoRequest request = new MovimentacaoRequest(
                1L, 1L, 1L, null, 10L, "ENTRADA",
                new BigDecimal("50"), new BigDecimal("12.0000"),
                null, null, null, null, null, null);

        when(armazemService.findById(1L)).thenReturn(armazem);
        when(produtoRepository.findByIdAndDeletedFalse(10L)).thenReturn(Optional.of(produto));
        when(saldoRepository.findByEmpresaIdAndFilialIdAndArmazemIdAndProdutoIdAndLote(1L, 1L, 1L, 10L, null))
                .thenReturn(Optional.of(saldo));
        when(saldoRepository.save(any(SaldoEstoque.class))).thenAnswer(inv -> inv.getArgument(0));
        when(movimentacaoRepository.save(any(MovimentacaoEstoque.class))).thenAnswer(inv -> {
            MovimentacaoEstoque m = inv.getArgument(0);
            m.setId(2L);
            return m;
        });

        MovimentacaoResponse response = service.movimentar(request, "admin");

        assertEquals(0, new BigDecimal("100").compareTo(saldo.getQuantidade()));
        // Custo medio: (50*8 + 50*12) / 100 = 1000/100 = 10
        assertEquals(0, new BigDecimal("10.0000").compareTo(saldo.getCustoMedio()));
    }

    @Test
    void saidaShouldReduceSaldo() {
        SaldoEstoque saldo = new SaldoEstoque();
        saldo.setId(1L);
        saldo.setQuantidade(new BigDecimal("100"));
        saldo.setCustoMedio(new BigDecimal("10.0000"));
        saldo.setArmazem(armazem);
        saldo.setProduto(produto);

        MovimentacaoRequest request = new MovimentacaoRequest(
                1L, 1L, 1L, null, 10L, "SAIDA",
                new BigDecimal("30"), null,
                null, null, null, null, null, null);

        when(armazemService.findById(1L)).thenReturn(armazem);
        when(produtoRepository.findByIdAndDeletedFalse(10L)).thenReturn(Optional.of(produto));
        when(saldoRepository.findByEmpresaIdAndFilialIdAndArmazemIdAndProdutoIdAndLote(1L, 1L, 1L, 10L, null))
                .thenReturn(Optional.of(saldo));
        when(saldoRepository.save(any(SaldoEstoque.class))).thenAnswer(inv -> inv.getArgument(0));
        when(movimentacaoRepository.save(any(MovimentacaoEstoque.class))).thenAnswer(inv -> {
            MovimentacaoEstoque m = inv.getArgument(0);
            m.setId(3L);
            return m;
        });

        MovimentacaoResponse response = service.movimentar(request, "admin");

        assertEquals(0, new BigDecimal("70").compareTo(saldo.getQuantidade()));
        assertEquals(0, new BigDecimal("100").compareTo(response.saldoAnterior()));
        assertEquals(0, new BigDecimal("70").compareTo(response.saldoPosterior()));
    }

    @Test
    void saidaShouldRejectInsufficientStock() {
        SaldoEstoque saldo = new SaldoEstoque();
        saldo.setId(1L);
        saldo.setQuantidade(new BigDecimal("10"));
        saldo.setCustoMedio(BigDecimal.ZERO);
        saldo.setArmazem(armazem);
        saldo.setProduto(produto);

        MovimentacaoRequest request = new MovimentacaoRequest(
                1L, 1L, 1L, null, 10L, "SAIDA",
                new BigDecimal("50"), null,
                null, null, null, null, null, null);

        when(armazemService.findById(1L)).thenReturn(armazem);
        when(produtoRepository.findByIdAndDeletedFalse(10L)).thenReturn(Optional.of(produto));
        when(saldoRepository.findByEmpresaIdAndFilialIdAndArmazemIdAndProdutoIdAndLote(1L, 1L, 1L, 10L, null))
                .thenReturn(Optional.of(saldo));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.movimentar(request, "admin"));
        assertEquals("Saldo insuficiente para o produto PROD-01", ex.getMessage());
    }

    @Test
    void ajusteShouldSetExactQuantity() {
        SaldoEstoque saldo = new SaldoEstoque();
        saldo.setId(1L);
        saldo.setQuantidade(new BigDecimal("100"));
        saldo.setCustoMedio(new BigDecimal("10.0000"));
        saldo.setArmazem(armazem);
        saldo.setProduto(produto);

        MovimentacaoRequest request = new MovimentacaoRequest(
                1L, 1L, 1L, null, 10L, "AJUSTE",
                new BigDecimal("80"), new BigDecimal("10.0000"),
                null, null, "INVENTARIO", 5L, "INV-001", "Ajuste inventario");

        when(armazemService.findById(1L)).thenReturn(armazem);
        when(produtoRepository.findByIdAndDeletedFalse(10L)).thenReturn(Optional.of(produto));
        when(saldoRepository.findByEmpresaIdAndFilialIdAndArmazemIdAndProdutoIdAndLote(1L, 1L, 1L, 10L, null))
                .thenReturn(Optional.of(saldo));
        when(saldoRepository.save(any(SaldoEstoque.class))).thenAnswer(inv -> inv.getArgument(0));
        when(movimentacaoRepository.save(any(MovimentacaoEstoque.class))).thenAnswer(inv -> {
            MovimentacaoEstoque m = inv.getArgument(0);
            m.setId(4L);
            return m;
        });

        MovimentacaoResponse response = service.movimentar(request, "admin");

        assertEquals(0, new BigDecimal("80").compareTo(saldo.getQuantidade()));
        assertEquals("AJUSTE", response.tipo());
    }

    @Test
    void shouldRejectInvalidTipo() {
        MovimentacaoRequest request = new MovimentacaoRequest(
                1L, 1L, 1L, null, 10L, "INVALIDO",
                new BigDecimal("10"), null,
                null, null, null, null, null, null);

        SaldoEstoque saldo = new SaldoEstoque();
        saldo.setQuantidade(new BigDecimal("100"));
        saldo.setCustoMedio(BigDecimal.ZERO);
        saldo.setArmazem(armazem);
        saldo.setProduto(produto);

        when(armazemService.findById(1L)).thenReturn(armazem);
        when(produtoRepository.findByIdAndDeletedFalse(10L)).thenReturn(Optional.of(produto));
        when(saldoRepository.findByEmpresaIdAndFilialIdAndArmazemIdAndProdutoIdAndLote(1L, 1L, 1L, 10L, null))
                .thenReturn(Optional.of(saldo));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.movimentar(request, "admin"));
        assertEquals("Tipo de movimentacao invalido: INVALIDO", ex.getMessage());
    }

    @Test
    void shouldThrowWhenProductNotFound() {
        MovimentacaoRequest request = new MovimentacaoRequest(
                1L, 1L, 1L, null, 99L, "ENTRADA",
                new BigDecimal("10"), null,
                null, null, null, null, null, null);

        when(armazemService.findById(1L)).thenReturn(armazem);
        when(produtoRepository.findByIdAndDeletedFalse(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.movimentar(request, "admin"));
    }
}
