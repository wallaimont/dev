package com.orionerp.modules.estoque.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.modules.estoque.domain.Armazem;
import com.orionerp.modules.estoque.dto.ArmazemRequest;
import com.orionerp.modules.estoque.dto.ArmazemResponse;
import com.orionerp.modules.estoque.repository.ArmazemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class ArmazemServiceTest {

    @Mock
    private ArmazemRepository armazemRepository;

    private ArmazemService armazemService;

    @BeforeEach
    void setUp() {
        armazemService = new ArmazemService(armazemRepository);
    }

    @Test
    void createShouldSaveAndReturnResponse() {
        ArmazemRequest request = new ArmazemRequest(1L, 1L, " ARM-01 ", " Principal ", "PRINCIPAL", null);

        when(armazemRepository.existsByEmpresaIdAndFilialIdAndCodigoIgnoreCaseAndDeletedFalse(1L, 1L, " ARM-01 "))
                .thenReturn(false);
        when(armazemRepository.save(any(Armazem.class))).thenAnswer(inv -> {
            Armazem a = inv.getArgument(0);
            a.setId(1L);
            return a;
        });

        ArmazemResponse response = armazemService.create(request);

        ArgumentCaptor<Armazem> captor = ArgumentCaptor.forClass(Armazem.class);
        verify(armazemRepository).save(captor.capture());
        Armazem saved = captor.getValue();

        assertEquals("ARM-01", saved.getCodigo());
        assertEquals("Principal", saved.getNome());
        assertEquals(1L, response.id());
    }

    @Test
    void createShouldRejectDuplicateCodigo() {
        ArmazemRequest request = new ArmazemRequest(1L, 1L, "ARM-01", "Principal", null, null);

        when(armazemRepository.existsByEmpresaIdAndFilialIdAndCodigoIgnoreCaseAndDeletedFalse(1L, 1L, "ARM-01"))
                .thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> armazemService.create(request));
        assertEquals("Ja existe armazem com este codigo", ex.getMessage());
        verify(armazemRepository, never()).save(any());
    }

    @Test
    void deleteShouldSoftDeleteEntity() {
        Armazem armazem = new Armazem();
        armazem.setId(5L);
        armazem.setAtivo(true);
        armazem.setDeleted(false);

        when(armazemRepository.findByIdAndDeletedFalse(5L)).thenReturn(Optional.of(armazem));
        when(armazemRepository.save(any(Armazem.class))).thenAnswer(inv -> inv.getArgument(0));

        armazemService.delete(5L);

        assertTrue(armazem.getDeleted());
        verify(armazemRepository).save(armazem);
    }
}
