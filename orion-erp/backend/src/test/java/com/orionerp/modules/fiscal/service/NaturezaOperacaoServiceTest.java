package com.orionerp.modules.fiscal.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.fiscal.domain.Cfop;
import com.orionerp.modules.fiscal.domain.NaturezaOperacao;
import com.orionerp.modules.fiscal.dto.NaturezaOperacaoRequest;
import com.orionerp.modules.fiscal.dto.NaturezaOperacaoResponse;
import com.orionerp.modules.fiscal.repository.CfopRepository;
import com.orionerp.modules.fiscal.repository.NaturezaOperacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NaturezaOperacaoServiceTest {

    @Mock
    private NaturezaOperacaoRepository naturezaOperacaoRepository;

    @Mock
    private CfopRepository cfopRepository;

    @InjectMocks
    private NaturezaOperacaoService service;

    private NaturezaOperacao naturezaOperacao;
    private Cfop cfop;

    @BeforeEach
    void setUp() {
        cfop = new Cfop();
        cfop.setId(1L);
        cfop.setCodigo("5102");
        cfop.setDescricao("Venda de mercadoria");
        cfop.setTipo("SAIDA");

        naturezaOperacao = new NaturezaOperacao();
        naturezaOperacao.setId(1L);
        naturezaOperacao.setEmpresaId(1L);
        naturezaOperacao.setCodigo("VENDA_MERC");
        naturezaOperacao.setNome("Venda de Mercadoria");
        naturezaOperacao.setTipo("SAIDA");
        naturezaOperacao.setCfop(cfop);
        naturezaOperacao.setGeraFinanceiro(true);
        naturezaOperacao.setMovimentaEstoque(true);
    }

    @Test
    void create_comDadosValidos_deveSalvar() {
        var request = new NaturezaOperacaoRequest(1L, "VENDA_MERC", "Venda de Mercadoria",
                "SAIDA", 1L, true, true);

        when(naturezaOperacaoRepository.existsByEmpresaIdAndCodigoAndDeletedFalse(1L, "VENDA_MERC"))
                .thenReturn(false);
        when(cfopRepository.findById(1L)).thenReturn(Optional.of(cfop));
        when(naturezaOperacaoRepository.save(any())).thenReturn(naturezaOperacao);

        NaturezaOperacaoResponse response = service.create(request);

        assertNotNull(response);
        assertEquals("VENDA_MERC", response.codigo());
        assertEquals("5102", response.cfopCodigo());
        verify(naturezaOperacaoRepository).save(any());
    }

    @Test
    void create_codigoDuplicado_deveLancarExcecao() {
        var request = new NaturezaOperacaoRequest(1L, "VENDA_MERC", "Venda de Mercadoria",
                "SAIDA", null, null, null);

        when(naturezaOperacaoRepository.existsByEmpresaIdAndCodigoAndDeletedFalse(1L, "VENDA_MERC"))
                .thenReturn(true);

        assertThrows(BusinessException.class, () -> service.create(request));
        verify(naturezaOperacaoRepository, never()).save(any());
    }

    @Test
    void getById_existente_deveRetornar() {
        when(naturezaOperacaoRepository.findByIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(naturezaOperacao));

        NaturezaOperacaoResponse response = service.getById(1L);

        assertEquals("VENDA_MERC", response.codigo());
        assertEquals("SAIDA", response.tipo());
    }

    @Test
    void getById_inexistente_deveLancarExcecao() {
        when(naturezaOperacaoRepository.findByIdAndDeletedFalse(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(99L));
    }

    @Test
    @SuppressWarnings("unchecked")
    void list_comFiltros_deveRetornarPaginado() {
        var page = new PageImpl<>(List.of(naturezaOperacao));
        when(naturezaOperacaoRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        var result = service.list(1L, "SAIDA", null, Pageable.unpaged());

        assertEquals(1, result.getItems().size());
        assertEquals("VENDA_MERC", result.getItems().get(0).codigo());
    }

    @Test
    void delete_existente_deveSoftDelete() {
        when(naturezaOperacaoRepository.findByIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(naturezaOperacao));

        service.delete(1L);

        assertTrue(naturezaOperacao.getDeleted());
        verify(naturezaOperacaoRepository).save(naturezaOperacao);
    }
}
