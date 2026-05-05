package com.orionerp.modules.financeiro.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.cadastros.domain.ContaBancaria;
import com.orionerp.modules.cadastros.repository.CentroCustoRepository;
import com.orionerp.modules.cadastros.repository.ClienteRepository;
import com.orionerp.modules.cadastros.repository.ContaBancariaRepository;
import com.orionerp.modules.cadastros.repository.FornecedorRepository;
import com.orionerp.modules.cadastros.repository.NaturezaFinanceiraRepository;
import com.orionerp.modules.financeiro.domain.Titulo;
import com.orionerp.modules.financeiro.domain.TituloBaixa;
import com.orionerp.modules.financeiro.domain.TituloParcela;
import com.orionerp.modules.financeiro.dto.BaixaRequest;
import com.orionerp.modules.financeiro.dto.BaixaResponse;
import com.orionerp.modules.financeiro.dto.TituloRequest;
import com.orionerp.modules.financeiro.dto.TituloResponse;
import com.orionerp.modules.financeiro.repository.FluxoCaixaRepository;
import com.orionerp.modules.financeiro.repository.TituloBaixaRepository;
import com.orionerp.modules.financeiro.repository.TituloParcelaRepository;
import com.orionerp.modules.financeiro.repository.TituloRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TituloServiceTest {

    @Mock private TituloRepository tituloRepository;
    @Mock private TituloParcelaRepository parcelaRepository;
    @Mock private TituloBaixaRepository baixaRepository;
    @Mock private FluxoCaixaRepository fluxoCaixaRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private FornecedorRepository fornecedorRepository;
    @Mock private NaturezaFinanceiraRepository naturezaRepository;
    @Mock private CentroCustoRepository centroCustoRepository;
    @Mock private ContaBancariaRepository contaBancariaRepository;

    private TituloService tituloService;

    @BeforeEach
    void setUp() {
        tituloService = new TituloService(
                tituloRepository, parcelaRepository, baixaRepository, fluxoCaixaRepository,
                clienteRepository, fornecedorRepository, naturezaRepository,
                centroCustoRepository, contaBancariaRepository);
    }

    @Test
    void createShouldGenerateSingleParcelaWhenNoneProvided() {
        TituloRequest request = new TituloRequest(
                1L, 1L, "RECEBER", "001", null,
                null, null, null, null, null,
                null, null, LocalDate.of(2025, 1, 15),
                new BigDecimal("1000.00"), null, null);

        when(tituloRepository.existsByEmpresaIdAndFilialIdAndNumeroAndTipoAndDeletedFalse(1L, 1L, "001", "RECEBER"))
                .thenReturn(false);
        when(tituloRepository.save(any(Titulo.class))).thenAnswer(inv -> {
            Titulo t = inv.getArgument(0);
            t.setId(1L);
            return t;
        });

        TituloResponse response = tituloService.create(request);

        ArgumentCaptor<Titulo> captor = ArgumentCaptor.forClass(Titulo.class);
        verify(tituloRepository).save(captor.capture());
        Titulo saved = captor.getValue();

        assertEquals("RECEBER", saved.getTipo());
        assertEquals("001", saved.getNumero());
        assertEquals(new BigDecimal("1000.00"), saved.getValorOriginal());
        assertEquals(new BigDecimal("1000.00"), saved.getValorAberto());
        assertEquals(1, saved.getParcelas().size());
        assertEquals(1, saved.getParcelas().get(0).getNumeroParcela());
        assertEquals(new BigDecimal("1000.00"), saved.getParcelas().get(0).getValor());
        assertNotNull(response);
    }

    @Test
    void createShouldAcceptMultipleParcelas() {
        List<TituloRequest.ParcelaInput> parcelas = List.of(
                new TituloRequest.ParcelaInput(1, LocalDate.of(2025, 2, 15), new BigDecimal("500.00")),
                new TituloRequest.ParcelaInput(2, LocalDate.of(2025, 3, 15), new BigDecimal("500.00")));

        TituloRequest request = new TituloRequest(
                1L, 1L, "PAGAR", "002", null,
                null, null, null, null, null,
                null, null, LocalDate.of(2025, 1, 15),
                new BigDecimal("1000.00"), null, parcelas);

        when(tituloRepository.existsByEmpresaIdAndFilialIdAndNumeroAndTipoAndDeletedFalse(1L, 1L, "002", "PAGAR"))
                .thenReturn(false);
        when(tituloRepository.save(any(Titulo.class))).thenAnswer(inv -> {
            Titulo t = inv.getArgument(0);
            t.setId(2L);
            return t;
        });

        TituloResponse response = tituloService.create(request);

        ArgumentCaptor<Titulo> captor = ArgumentCaptor.forClass(Titulo.class);
        verify(tituloRepository).save(captor.capture());
        Titulo saved = captor.getValue();

        assertEquals("PAGAR", saved.getTipo());
        assertEquals(2, saved.getParcelas().size());
        assertEquals(new BigDecimal("500.00"), saved.getParcelas().get(0).getValor());
        assertEquals(new BigDecimal("500.00"), saved.getParcelas().get(1).getValor());
        assertNotNull(response);
    }

    @Test
    void createShouldRejectDuplicateNumero() {
        TituloRequest request = new TituloRequest(
                1L, 1L, "RECEBER", "001", null,
                null, null, null, null, null,
                null, null, LocalDate.of(2025, 1, 15),
                new BigDecimal("1000.00"), null, null);

        when(tituloRepository.existsByEmpresaIdAndFilialIdAndNumeroAndTipoAndDeletedFalse(1L, 1L, "001", "RECEBER"))
                .thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> tituloService.create(request));
        assertEquals("Ja existe titulo com este numero para o tipo informado", ex.getMessage());
        verify(tituloRepository, never()).save(any());
    }

    @Test
    void deleteShouldSoftDeleteOpenTitulo() {
        Titulo titulo = new Titulo();
        titulo.setId(5L);
        titulo.setStatus("ABERTO");
        titulo.setDeleted(false);
        titulo.setAtivo(true);

        when(tituloRepository.findByIdAndDeletedFalse(5L)).thenReturn(Optional.of(titulo));
        when(tituloRepository.save(any(Titulo.class))).thenAnswer(inv -> inv.getArgument(0));

        tituloService.delete(5L);

        assertTrue(titulo.getDeleted());
        verify(tituloRepository).save(titulo);
    }

    @Test
    void deleteShouldRejectNonOpenTitulo() {
        Titulo titulo = new Titulo();
        titulo.setId(5L);
        titulo.setStatus("PARCIAL");

        when(tituloRepository.findByIdAndDeletedFalse(5L)).thenReturn(Optional.of(titulo));

        BusinessException ex = assertThrows(BusinessException.class, () -> tituloService.delete(5L));
        assertEquals("Somente titulos com status ABERTO podem ser excluidos", ex.getMessage());
        verify(tituloRepository, never()).save(any());
    }

    @Test
    void baixarShouldUpdateParcelaAndGenerateFluxoCaixa() {
        Titulo titulo = buildTituloComParcela();
        TituloParcela parcela = titulo.getParcelas().get(0);

        ContaBancaria conta = new ContaBancaria();
        conta.setId(10L);

        BaixaRequest request = new BaixaRequest(
                parcela.getId(), LocalDate.of(2025, 2, 1),
                new BigDecimal("1000.00"), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                10L, "PIX", null);

        when(tituloRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(titulo));
        when(parcelaRepository.findById(100L)).thenReturn(Optional.of(parcela));
        when(contaBancariaRepository.findByIdAndDeletedFalse(10L)).thenReturn(Optional.of(conta));
        when(baixaRepository.save(any(TituloBaixa.class))).thenAnswer(inv -> {
            TituloBaixa b = inv.getArgument(0);
            b.setId(50L);
            return b;
        });
        when(parcelaRepository.save(any(TituloParcela.class))).thenAnswer(inv -> inv.getArgument(0));
        when(tituloRepository.save(any(Titulo.class))).thenAnswer(inv -> inv.getArgument(0));

        BaixaResponse response = tituloService.baixar(1L, 100L, request, "admin");

        assertNotNull(response);
        assertEquals(new BigDecimal("1000.00"), response.valorPago());
        assertEquals("QUITADO", parcela.getStatus());
        verify(fluxoCaixaRepository).save(any());
    }

    @Test
    void baixarShouldRejectAmountGreaterThanSaldo() {
        Titulo titulo = buildTituloComParcela();
        TituloParcela parcela = titulo.getParcelas().get(0);

        BaixaRequest request = new BaixaRequest(
                parcela.getId(), LocalDate.of(2025, 2, 1),
                new BigDecimal("2000.00"), null, null, null,
                null, "PIX", null);

        when(tituloRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(titulo));
        when(parcelaRepository.findById(100L)).thenReturn(Optional.of(parcela));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> tituloService.baixar(1L, 100L, request, "admin"));
        assertEquals("Valor pago nao pode ser maior que o saldo aberto da parcela", ex.getMessage());
        verify(baixaRepository, never()).save(any());
    }

    @Test
    void estornarShouldRevertBaixaAndRecalculate() {
        Titulo titulo = buildTituloComParcela();
        TituloParcela parcela = titulo.getParcelas().get(0);
        parcela.setValorPago(new BigDecimal("1000.00"));
        parcela.setStatus("QUITADO");
        parcela.setDataPagamento(LocalDate.of(2025, 2, 1));
        titulo.setStatus("QUITADO");
        titulo.setValorAberto(BigDecimal.ZERO);

        ContaBancaria conta = new ContaBancaria();
        conta.setId(10L);

        TituloBaixa baixa = new TituloBaixa();
        baixa.setId(50L);
        baixa.setParcela(parcela);
        baixa.setValorPago(new BigDecimal("1000.00"));
        baixa.setValorJuros(BigDecimal.ZERO);
        baixa.setValorMulta(BigDecimal.ZERO);
        baixa.setValorDesconto(BigDecimal.ZERO);
        baixa.setEstornado(false);
        baixa.setContaBancaria(conta);

        when(tituloRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(titulo));
        when(baixaRepository.findById(50L)).thenReturn(Optional.of(baixa));
        when(baixaRepository.save(any(TituloBaixa.class))).thenAnswer(inv -> inv.getArgument(0));
        when(parcelaRepository.save(any(TituloParcela.class))).thenAnswer(inv -> inv.getArgument(0));
        when(tituloRepository.save(any(Titulo.class))).thenAnswer(inv -> inv.getArgument(0));

        BaixaResponse response = tituloService.estornar(1L, 100L, 50L, "admin");

        assertTrue(baixa.getEstornado());
        assertNotNull(baixa.getEstornadoEm());
        assertEquals("admin", baixa.getEstornadoPor());
        assertEquals(0, parcela.getValorPago().compareTo(BigDecimal.ZERO));
        assertEquals("ABERTO", parcela.getStatus());
        assertEquals("ABERTO", titulo.getStatus());
        verify(fluxoCaixaRepository).save(any());
    }

    @Test
    void estornarShouldRejectAlreadyReversed() {
        Titulo titulo = buildTituloComParcela();
        TituloParcela parcela = titulo.getParcelas().get(0);

        TituloBaixa baixa = new TituloBaixa();
        baixa.setId(50L);
        baixa.setParcela(parcela);
        baixa.setEstornado(true);

        when(tituloRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(titulo));
        when(baixaRepository.findById(50L)).thenReturn(Optional.of(baixa));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> tituloService.estornar(1L, 100L, 50L, "admin"));
        assertEquals("Baixa ja foi estornada", ex.getMessage());
    }

    @Test
    void getByIdShouldThrowWhenNotFound() {
        when(tituloRepository.findByIdAndDeletedFalse(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tituloService.getById(99L));
    }

    private Titulo buildTituloComParcela() {
        Titulo titulo = new Titulo();
        titulo.setId(1L);
        titulo.setEmpresaId(1L);
        titulo.setFilialId(1L);
        titulo.setTipo("RECEBER");
        titulo.setNumero("001");
        titulo.setDataEmissao(LocalDate.of(2025, 1, 15));
        titulo.setValorOriginal(new BigDecimal("1000.00"));
        titulo.setValorAberto(new BigDecimal("1000.00"));
        titulo.setStatus("ABERTO");
        titulo.setParcelas(new ArrayList<>());

        TituloParcela parcela = new TituloParcela();
        parcela.setId(100L);
        parcela.setTitulo(titulo);
        parcela.setNumeroParcela(1);
        parcela.setDataVencimento(LocalDate.of(2025, 2, 15));
        parcela.setValor(new BigDecimal("1000.00"));
        parcela.setValorPago(BigDecimal.ZERO);
        parcela.setValorJuros(BigDecimal.ZERO);
        parcela.setValorMulta(BigDecimal.ZERO);
        parcela.setValorDesconto(BigDecimal.ZERO);
        parcela.setStatus("ABERTO");
        titulo.getParcelas().add(parcela);

        return titulo;
    }
}
