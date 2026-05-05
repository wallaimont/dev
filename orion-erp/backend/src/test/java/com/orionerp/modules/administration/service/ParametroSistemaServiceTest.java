package com.orionerp.modules.administration.service;

import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.administration.domain.ParametroSistema;
import com.orionerp.modules.administration.dto.ParametroSistemaRequest;
import com.orionerp.modules.administration.dto.ParametroSistemaResponse;
import com.orionerp.modules.administration.repository.ParametroSistemaRepository;
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
class ParametroSistemaServiceTest {

    @Mock
    private ParametroSistemaRepository parametroSistemaRepository;

    private ParametroSistemaService parametroSistemaService;

    @BeforeEach
    void setUp() {
        parametroSistemaService = new ParametroSistemaService(parametroSistemaRepository);
    }

    @Test
    void createShouldUpperCaseChaveAndTipo() {
        ParametroSistemaRequest request = new ParametroSistemaRequest(
                1L, 2L,
                " nf.serie.padrao ",
                "001",
                " integer ",
                "Serie padrao da NF",
                " fiscal ",
                true,
                true
        );

        when(parametroSistemaRepository.save(any(ParametroSistema.class))).thenAnswer(invocation -> {
            ParametroSistema p = invocation.getArgument(0);
            p.setId(15L);
            return p;
        });

        ParametroSistemaResponse response = parametroSistemaService.create(request);

        ArgumentCaptor<ParametroSistema> captor = ArgumentCaptor.forClass(ParametroSistema.class);
        verify(parametroSistemaRepository).save(captor.capture());
        ParametroSistema saved = captor.getValue();

        assertEquals("NF.SERIE.PADRAO", saved.getChave());
        assertEquals("INTEGER", saved.getTipo());
        assertEquals("FISCAL", saved.getModulo());
        assertEquals(1L, saved.getEmpresaId());
        assertEquals(2L, saved.getFilialId());
        assertEquals(15L, response.id());
    }

    @Test
    void resolveShouldFallbackFromFilialToEmpresaToGlobal() {
        ParametroSistema global = new ParametroSistema();
        global.setId(1L);
        global.setChave("APP.NOME");
        global.setValor("OrionERP");
        global.setTipo("STRING");

        when(parametroSistemaRepository.findFirstByEmpresaIdAndFilialIdAndChave(10L, 20L, "APP.NOME"))
                .thenReturn(Optional.empty());
        when(parametroSistemaRepository.findFirstByEmpresaIdAndFilialIdIsNullAndChave(10L, "APP.NOME"))
                .thenReturn(Optional.empty());
        when(parametroSistemaRepository.findFirstByEmpresaIdIsNullAndFilialIdIsNullAndChave("APP.NOME"))
                .thenReturn(Optional.of(global));

        ParametroSistemaResponse response = parametroSistemaService.resolve("APP.NOME", 10L, 20L);

        assertEquals("OrionERP", response.valor());
        assertEquals(1L, response.id());
    }

    @Test
    void resolveShouldThrowWhenNotFound() {
        when(parametroSistemaRepository.findFirstByEmpresaIdAndFilialIdAndChave(10L, 20L, "X"))
                .thenReturn(Optional.empty());
        when(parametroSistemaRepository.findFirstByEmpresaIdAndFilialIdIsNullAndChave(10L, "X"))
                .thenReturn(Optional.empty());
        when(parametroSistemaRepository.findFirstByEmpresaIdIsNullAndFilialIdIsNullAndChave("X"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> parametroSistemaService.resolve("X", 10L, 20L));
    }

    @Test
    void deleteShouldSoftDeleteEntity() {
        ParametroSistema param = new ParametroSistema();
        param.setId(15L);
        param.setAtivo(true);
        param.setDeleted(false);

        when(parametroSistemaRepository.findByIdAndDeletedFalse(15L)).thenReturn(Optional.of(param));
        when(parametroSistemaRepository.save(any(ParametroSistema.class))).thenAnswer(invocation -> invocation.getArgument(0));

        parametroSistemaService.delete(15L);

        assertTrue(param.getDeleted());
        assertFalse(param.getAtivo());
        assertNotNull(param.getDeletedAt());
    }
}
