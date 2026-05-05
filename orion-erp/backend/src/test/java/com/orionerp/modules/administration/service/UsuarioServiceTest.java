package com.orionerp.modules.administration.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.modules.administration.domain.Perfil;
import com.orionerp.modules.administration.domain.Usuario;
import com.orionerp.modules.administration.dto.UsuarioRequest;
import com.orionerp.modules.administration.dto.UsuarioResponse;
import com.orionerp.modules.administration.repository.PerfilRepository;
import com.orionerp.modules.administration.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
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
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PerfilRepository perfilRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService(usuarioRepository, perfilRepository, passwordEncoder);
    }

    @Test
    void createShouldNormalizeEmailAndEncodePassword() {
        UsuarioRequest request = new UsuarioRequest(
                1L,
                2L,
                3L,
                " Maria Orion ",
                " MARIA@ORIONERP.COM ",
                "Senha123",
                "11999999999",
                true,
                true
        );
        Perfil perfil = new Perfil();
        perfil.setId(3L);
        perfil.setNome("Administrador");

        when(usuarioRepository.existsByEmailIgnoreCaseAndDeletedFalse("maria@orionerp.com")).thenReturn(false);
        when(perfilRepository.findByIdAndDeletedFalse(3L)).thenReturn(Optional.of(perfil));
        when(passwordEncoder.encode("Senha123")).thenReturn("hash-123");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setId(9L);
            return usuario;
        });

        UsuarioResponse response = usuarioService.create(request);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario saved = captor.getValue();

        assertEquals("Maria Orion", saved.getNome());
        assertEquals("maria@orionerp.com", saved.getEmail());
        assertEquals("hash-123", saved.getSenhaHash());
        assertEquals(perfil, saved.getPerfil());
        assertEquals(9L, response.id());
        assertEquals("maria@orionerp.com", response.email());
        assertEquals("Administrador", response.perfilNome());
    }

    @Test
    void createShouldRejectDuplicateEmail() {
        UsuarioRequest request = new UsuarioRequest(
                1L,
                null,
                3L,
                "Maria Orion",
                "maria@orionerp.com",
                "Senha123",
                null,
                false,
                true
        );

        when(usuarioRepository.existsByEmailIgnoreCaseAndDeletedFalse("maria@orionerp.com")).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> usuarioService.create(request));

        assertEquals("Ja existe usuario com o email informado", exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void resetPasswordShouldClearLockFlagsAndPersistHash() {
        Perfil perfil = new Perfil();
        perfil.setId(3L);
        perfil.setNome("Administrador");

        Usuario usuario = new Usuario();
        usuario.setId(5L);
        usuario.setPerfil(perfil);
        usuario.setEmail("maria@orionerp.com");
        usuario.setNome("Maria Orion");
        usuario.setBloqueado(true);
        usuario.setBloqueadoAte(LocalDateTime.now().plusDays(1));
        usuario.setTentativasLogin(4);
        usuario.setTrocarSenha(true);
        usuario.setDeleted(false);

        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("NovaSenha123")).thenReturn("hash-nova");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        usuarioService.resetPassword(5L, "NovaSenha123");

        assertEquals("hash-nova", usuario.getSenhaHash());
        assertFalse(usuario.getTrocarSenha());
        assertEquals(0, usuario.getTentativasLogin());
        assertFalse(usuario.getBloqueado());
        assertEquals(null, usuario.getBloqueadoAte());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void resetPasswordShouldRejectShortPassword() {
        BusinessException exception = assertThrows(BusinessException.class,
                () -> usuarioService.resetPassword(5L, "123"));

        assertEquals("Senha deve ter no minimo 8 caracteres", exception.getMessage());
        verify(usuarioRepository, never()).findById(any());
    }
}