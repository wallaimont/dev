package com.orionerp.modules.administration.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.modules.administration.domain.Empresa;
import com.orionerp.modules.administration.domain.Filial;
import com.orionerp.modules.administration.dto.FilialRequest;
import com.orionerp.modules.administration.dto.FilialResponse;
import com.orionerp.modules.administration.repository.EmpresaRepository;
import com.orionerp.modules.administration.repository.FilialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilialServiceTest {

    @Mock
    private FilialRepository filialRepository;

    @Mock
    private EmpresaRepository empresaRepository;

    private FilialService filialService;

    @BeforeEach
    void setUp() {
        filialService = new FilialService(filialRepository, empresaRepository);
    }

    @Test
    void createShouldNormalizeFieldsAndSetMatriz() {
        Empresa empresa = new Empresa();
        empresa.setId(1L);
        empresa.setRazaoSocial("Empresa Orion");

        FilialRequest request = new FilialRequest(
                1L,
                " FIL-01 ",
                " Filial Centro ",
                "Orion Centro",
                "12.345.678/0002-80",
                " FILIAL@ORION.COM ",
                "Sao Paulo",
                "SP",
                true,
                "Obs",
                null
        );

        when(empresaRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(empresa));
        when(filialRepository.existsByEmpresa_IdAndCodigoIgnoreCaseAndDeletedFalse(1L, " FIL-01 ")).thenReturn(false);
        when(filialRepository.existsByCnpjAndDeletedFalse("12.345.678/0002-80")).thenReturn(false);
        when(filialRepository.save(any(Filial.class))).thenAnswer(invocation -> {
            Filial f = invocation.getArgument(0);
            f.setId(20L);
            return f;
        });

        FilialResponse response = filialService.create(request);

        ArgumentCaptor<Filial> captor = ArgumentCaptor.forClass(Filial.class);
        verify(filialRepository).save(captor.capture());
        Filial saved = captor.getValue();

        assertEquals("FIL-01", saved.getCodigo());
        assertEquals("Filial Centro", saved.getRazaoSocial());
        assertEquals("filial@orion.com", saved.getEmail());
        assertTrue(saved.getMatriz());
        assertEquals(20L, response.id());
    }

    @Test
    void createShouldRejectDuplicateCnpj() {
        Empresa empresa = new Empresa();
        empresa.setId(1L);
        empresa.setRazaoSocial("Empresa Orion");

        FilialRequest request = new FilialRequest(
                1L,
                "FIL-02",
                "Filial Norte",
                null,
                "12.345.678/0002-80",
                null,
                null,
                null,
                false,
                null,
                true
        );

        when(empresaRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(empresa));
        when(filialRepository.existsByEmpresa_IdAndCodigoIgnoreCaseAndDeletedFalse(1L, "FIL-02")).thenReturn(false);
        when(filialRepository.existsByCnpjAndDeletedFalse("12.345.678/0002-80")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> filialService.create(request));
        assertEquals("Ja existe filial com o CNPJ informado", ex.getMessage());
        verify(filialRepository, never()).save(any());
    }

    @Test
    void deleteShouldSoftDeleteEntity() {
        Filial filial = new Filial();
        filial.setId(20L);
        filial.setAtivo(true);
        filial.setDeleted(false);

        when(filialRepository.findByIdAndDeletedFalse(20L)).thenReturn(Optional.of(filial));
        when(filialRepository.save(any(Filial.class))).thenAnswer(invocation -> invocation.getArgument(0));

        filialService.delete(20L);

        assertTrue(filial.getDeleted());
        assertFalse(filial.getAtivo());
        assertNotNull(filial.getDeletedAt());
    }
}
