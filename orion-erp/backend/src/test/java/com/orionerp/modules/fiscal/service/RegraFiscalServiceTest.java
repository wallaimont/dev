package com.orionerp.modules.fiscal.service;

import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.fiscal.domain.Cfop;
import com.orionerp.modules.fiscal.domain.Ncm;
import com.orionerp.modules.fiscal.domain.RegraFiscal;
import com.orionerp.modules.fiscal.dto.RegraFiscalRequest;
import com.orionerp.modules.fiscal.dto.RegraFiscalResponse;
import com.orionerp.modules.fiscal.repository.CfopRepository;
import com.orionerp.modules.fiscal.repository.CstRepository;
import com.orionerp.modules.fiscal.repository.NcmRepository;
import com.orionerp.modules.fiscal.repository.RegraFiscalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegraFiscalServiceTest {

    @Mock
    private RegraFiscalRepository regraFiscalRepository;
    @Mock
    private NcmRepository ncmRepository;
    @Mock
    private CfopRepository cfopRepository;
    @Mock
    private CstRepository cstRepository;

    @InjectMocks
    private RegraFiscalService service;

    private Ncm ncm;
    private Cfop cfop;
    private RegraFiscal regraEspecifica;
    private RegraFiscal regraUf;
    private RegraFiscal regraGenerica;

    @BeforeEach
    void setUp() {
        ncm = new Ncm();
        ncm.setId(1L);
        ncm.setCodigo("84713012");

        cfop = new Cfop();
        cfop.setId(1L);
        cfop.setCodigo("5102");

        // Regra mais específica: UF + NCM
        regraEspecifica = new RegraFiscal();
        regraEspecifica.setId(1L);
        regraEspecifica.setEmpresaId(1L);
        regraEspecifica.setUfOrigem("SP");
        regraEspecifica.setUfDestino("RJ");
        regraEspecifica.setNcm(ncm);
        regraEspecifica.setCfop(cfop);
        regraEspecifica.setAliquotaIcms(new BigDecimal("12.0000"));
        regraEspecifica.setAliquotaPis(new BigDecimal("1.6500"));
        regraEspecifica.setAliquotaCofins(new BigDecimal("7.6000"));
        regraEspecifica.setAtivo(true);

        // Regra por UF apenas
        regraUf = new RegraFiscal();
        regraUf.setId(2L);
        regraUf.setEmpresaId(1L);
        regraUf.setUfOrigem("SP");
        regraUf.setUfDestino("RJ");
        regraUf.setAliquotaIcms(new BigDecimal("12.0000"));
        regraUf.setAtivo(true);

        // Regra genérica (sem UF/NCM)
        regraGenerica = new RegraFiscal();
        regraGenerica.setId(3L);
        regraGenerica.setEmpresaId(1L);
        regraGenerica.setAliquotaIcms(new BigDecimal("18.0000"));
        regraGenerica.setAtivo(true);
    }

    @Test
    void create_comDadosValidos_deveSalvar() {
        var request = new RegraFiscalRequest(1L, "SP", "RJ", 1L, 1L,
                null, null, null,
                new BigDecimal("12.0000"), new BigDecimal("1.6500"), new BigDecimal("7.6000"),
                null, null);

        when(ncmRepository.findById(1L)).thenReturn(Optional.of(ncm));
        when(cfopRepository.findById(1L)).thenReturn(Optional.of(cfop));
        when(regraFiscalRepository.save(any())).thenReturn(regraEspecifica);

        RegraFiscalResponse response = service.create(request);

        assertNotNull(response);
        assertEquals("SP", response.ufOrigem());
        assertEquals("RJ", response.ufDestino());
        verify(regraFiscalRepository).save(any());
    }

    @Test
    void create_ncmInexistente_deveLancarExcecao() {
        var request = new RegraFiscalRequest(1L, "SP", "RJ", 99L, null,
                null, null, null, null, null, null, null, null);

        when(ncmRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(request));
    }

    @Test
    void findRegraAplicavel_comUfENcm_deveRetornarMaisEspecifica() {
        when(regraFiscalRepository.findByEmpresaIdAndAtivoTrueOrderById(1L))
                .thenReturn(List.of(regraGenerica, regraUf, regraEspecifica));

        Optional<RegraFiscalResponse> result = service.findRegraAplicavel(1L, "SP", "RJ", 1L);

        assertTrue(result.isPresent());
        assertEquals(regraEspecifica.getId(), result.get().id());
    }

    @Test
    void findRegraAplicavel_somenteUf_deveRetornarRegraUf() {
        when(regraFiscalRepository.findByEmpresaIdAndAtivoTrueOrderById(1L))
                .thenReturn(List.of(regraGenerica, regraUf, regraEspecifica));

        Optional<RegraFiscalResponse> result = service.findRegraAplicavel(1L, "SP", "RJ", null);

        assertTrue(result.isPresent());
        assertEquals(regraUf.getId(), result.get().id());
    }

    @Test
    void findRegraAplicavel_semMatch_deveRetornarGenerica() {
        when(regraFiscalRepository.findByEmpresaIdAndAtivoTrueOrderById(1L))
                .thenReturn(List.of(regraGenerica));

        Optional<RegraFiscalResponse> result = service.findRegraAplicavel(1L, "MG", "BA", null);

        assertTrue(result.isPresent());
        assertEquals(regraGenerica.getId(), result.get().id());
    }

    @Test
    void findRegraAplicavel_semRegras_deveRetornarVazio() {
        when(regraFiscalRepository.findByEmpresaIdAndAtivoTrueOrderById(1L))
                .thenReturn(List.of());

        Optional<RegraFiscalResponse> result = service.findRegraAplicavel(1L, "SP", "RJ", 1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void deactivate_existente_deveDesativar() {
        when(regraFiscalRepository.findByIdAndAtivoTrue(1L))
                .thenReturn(Optional.of(regraEspecifica));

        service.deactivate(1L);

        assertFalse(regraEspecifica.getAtivo());
        verify(regraFiscalRepository).save(regraEspecifica);
    }

    @Test
    void calcularEspecificidade_ufOrigemDestinoNcm_deveRetornar8() {
        int score = service.calcularEspecificidade(regraEspecifica, "SP", "RJ", 1L);
        assertEquals(8, score);
    }
}
