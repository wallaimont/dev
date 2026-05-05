package com.orionerp.modules.administration.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.modules.administration.domain.Perfil;
import com.orionerp.modules.administration.domain.Permissao;
import com.orionerp.modules.administration.dto.PerfilRequest;
import com.orionerp.modules.administration.dto.PerfilResponse;
import com.orionerp.modules.administration.repository.PerfilRepository;
import com.orionerp.modules.administration.repository.PermissaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PerfilServiceTest {

    @Mock
    private PerfilRepository perfilRepository;

    @Mock
    private PermissaoRepository permissaoRepository;

    private PerfilService perfilService;

    @BeforeEach
    void setUp() {
        perfilService = new PerfilService(perfilRepository, permissaoRepository);
    }

    @Test
    void createShouldNormalizeAndLinkPermissions() {
        Permissao perm = new Permissao();
        perm.setId(10L);
        perm.setModulo("ADM");
        perm.setRecurso("empresas");
        perm.setAcao("listar");

        PerfilRequest request = new PerfilRequest(
                1L,
                " ADM-FULL ",
                " Administrador Geral ",
                "Perfil administrativo",
                true,
                List.of(10L),
                null
        );

        when(perfilRepository.existsByEmpresaIdAndCodigoIgnoreCaseAndDeletedFalse(1L, " ADM-FULL ")).thenReturn(false);
        when(permissaoRepository.findByIdIn(List.of(10L))).thenReturn(List.of(perm));
        when(perfilRepository.save(any(Perfil.class))).thenAnswer(invocation -> {
            Perfil p = invocation.getArgument(0);
            p.setId(5L);
            return p;
        });

        PerfilResponse response = perfilService.create(request);

        ArgumentCaptor<Perfil> captor = ArgumentCaptor.forClass(Perfil.class);
        verify(perfilRepository).save(captor.capture());
        Perfil saved = captor.getValue();

        assertEquals("ADM-FULL", saved.getCodigo());
        assertEquals("Administrador Geral", saved.getNome());
        assertTrue(saved.getAdmin());
        assertEquals(1, saved.getPermissoes().size());
        assertEquals(5L, response.id());
        assertEquals(1, response.permissoes().size());
    }

    @Test
    void createShouldRejectDuplicateCodigoInSameEmpresa() {
        PerfilRequest request = new PerfilRequest(
                1L,
                "ADM-FULL",
                "Administrador Geral",
                null,
                false,
                null,
                true
        );

        when(perfilRepository.existsByEmpresaIdAndCodigoIgnoreCaseAndDeletedFalse(1L, "ADM-FULL")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> perfilService.create(request));
        assertEquals("Ja existe perfil com o codigo informado", ex.getMessage());
        verify(perfilRepository, never()).save(any());
    }

    @Test
    void deleteShouldSoftDeleteEntity() {
        Perfil perfil = new Perfil();
        perfil.setId(5L);
        perfil.setAtivo(true);
        perfil.setDeleted(false);

        when(perfilRepository.findByIdAndDeletedFalse(5L)).thenReturn(Optional.of(perfil));
        when(perfilRepository.save(any(Perfil.class))).thenAnswer(invocation -> invocation.getArgument(0));

        perfilService.delete(5L);

        assertTrue(perfil.getDeleted());
        assertFalse(perfil.getAtivo());
        assertNotNull(perfil.getDeletedAt());
    }
}
