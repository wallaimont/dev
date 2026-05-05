package com.orionerp.modules.administration.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.modules.administration.domain.Empresa;
import com.orionerp.modules.administration.dto.EmpresaRequest;
import com.orionerp.modules.administration.dto.EmpresaResponse;
import com.orionerp.modules.administration.repository.EmpresaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpresaServiceTest {

    @Mock
    private EmpresaRepository empresaRepository;

    private EmpresaService empresaService;

    @BeforeEach
    void setUp() {
        empresaService = new EmpresaService(empresaRepository);
    }

    @Test
    void createShouldNormalizeFieldsAndReturnResponse() {
        EmpresaRequest request = new EmpresaRequest(
                " EMP-01 ",
                " Empresa Orion ",
                " Orion ",
                "12.345.678/0001-99",
                " CONTATO@ORIONERP.COM ",
                "Sao Paulo",
                "SP",
                "Observacao",
                null
        );

        when(empresaRepository.existsByCodigoIgnoreCaseAndDeletedFalse(" EMP-01 ")).thenReturn(false);
        when(empresaRepository.existsByCnpjAndDeletedFalse("12.345.678/0001-99")).thenReturn(false);
        when(empresaRepository.save(any(Empresa.class))).thenAnswer(invocation -> {
            Empresa empresa = invocation.getArgument(0);
            empresa.setId(10L);
            return empresa;
        });

        EmpresaResponse response = empresaService.create(request);

        ArgumentCaptor<Empresa> captor = ArgumentCaptor.forClass(Empresa.class);
        verify(empresaRepository).save(captor.capture());
        Empresa saved = captor.getValue();

        assertEquals("EMP-01", saved.getCodigo());
        assertEquals("Empresa Orion", saved.getRazaoSocial());
        assertEquals("contato@orionerp.com", saved.getEmail());
        assertTrue(saved.getAtivo());
        assertEquals(10L, response.id());
        assertEquals("EMP-01", response.codigo());
        assertEquals("contato@orionerp.com", response.email());
    }

    @Test
    void createShouldRejectDuplicateCodigo() {
        EmpresaRequest request = new EmpresaRequest(
                "EMP-01",
                "Empresa Orion",
                null,
                "12.345.678/0001-99",
                "contato@orionerp.com",
                null,
                null,
                null,
                true
        );

        when(empresaRepository.existsByCodigoIgnoreCaseAndDeletedFalse("EMP-01")).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> empresaService.create(request));

        assertEquals("Ja existe empresa com o codigo informado", exception.getMessage());
        verify(empresaRepository, never()).save(any(Empresa.class));
    }

    @Test
    void deleteShouldSoftDeleteEntity() {
        Empresa empresa = new Empresa();
        empresa.setId(7L);
        empresa.setAtivo(true);
        empresa.setDeleted(false);

        when(empresaRepository.findByIdAndDeletedFalse(7L)).thenReturn(Optional.of(empresa));
        when(empresaRepository.save(any(Empresa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        empresaService.delete(7L);

        assertTrue(empresa.getDeleted());
        assertFalse(empresa.getAtivo());
        assertNotNull(empresa.getDeletedAt());
        verify(empresaRepository).save(empresa);
    }
}