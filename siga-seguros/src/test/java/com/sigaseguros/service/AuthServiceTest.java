package com.sigaseguros.service;

import com.sigaseguros.dto.AlterarSenhaDTO;
import com.sigaseguros.dto.LoginRequest;
import com.sigaseguros.dto.LoginResponse;
import com.sigaseguros.entity.Usuario;
import com.sigaseguros.enums.Perfil;
import com.sigaseguros.exception.BusinessException;
import com.sigaseguros.exception.ResourceNotFoundException;
import com.sigaseguros.repository.UsuarioRepository;
import com.sigaseguros.security.JwtTokenProvider;
import com.sigaseguros.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditoriaService auditoriaService;

    @InjectMocks
    private AuthService authService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Admin");
        usuario.setEmail("admin@sigaseguros.com");
        usuario.setSenha("$2a$10$encodedpassword");
        usuario.setPerfil(Perfil.ADMIN);
        usuario.setActive(true);
        usuario.setTentativasLogin(0);
        usuario.setBloqueadoAte(null);
    }

    @Nested
    @DisplayName("login()")
    class Login {

        @Test
        @DisplayName("Deve autenticar com sucesso e retornar LoginResponse")
        void loginComSucesso() {
            LoginRequest request = new LoginRequest();
            request.setEmail("admin@sigaseguros.com");
            request.setSenha("admin123");

            UserPrincipal userPrincipal = new UserPrincipal(usuario);
            Authentication authentication = mock(Authentication.class);
            when(authentication.getPrincipal()).thenReturn(userPrincipal);

            when(usuarioRepository.findByEmailAndActiveTrue("admin@sigaseguros.com"))
                    .thenReturn(Optional.of(usuario));
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(tokenProvider.generateToken(any(UserPrincipal.class))).thenReturn("jwt-token");
            when(tokenProvider.generateRefreshToken(any(UserPrincipal.class))).thenReturn("refresh-token");
            when(tokenProvider.getJwtExpirationMs()).thenReturn(3600000L);

            LoginResponse response = authService.login(request);

            assertThat(response.getToken()).isEqualTo("jwt-token");
            assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
            assertThat(response.getNome()).isEqualTo("Admin");
            assertThat(response.getEmail()).isEqualTo("admin@sigaseguros.com");
            assertThat(response.getPerfil()).isEqualTo("ADMIN");
            assertThat(response.getExpiresIn()).isEqualTo(3600000L);

            verify(usuarioRepository).save(usuario);
            assertThat(usuario.getTentativasLogin()).isZero();
            assertThat(usuario.getBloqueadoAte()).isNull();
        }

        @Test
        @DisplayName("Deve lançar exceção quando usuário não encontrado")
        void loginUsuarioNaoEncontrado() {
            LoginRequest request = new LoginRequest();
            request.setEmail("inexistente@email.com");
            request.setSenha("123");

            when(usuarioRepository.findByEmailAndActiveTrue("inexistente@email.com"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(BadCredentialsException.class);
        }

        @Test
        @DisplayName("Deve bloquear conta após 5 tentativas falhas")
        void loginBloquearApos5Tentativas() {
            LoginRequest request = new LoginRequest();
            request.setEmail("admin@sigaseguros.com");
            request.setSenha("senhaerrada");

            usuario.setTentativasLogin(4);

            when(usuarioRepository.findByEmailAndActiveTrue("admin@sigaseguros.com"))
                    .thenReturn(Optional.of(usuario));
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(BadCredentialsException.class);

            assertThat(usuario.getTentativasLogin()).isEqualTo(5);
            assertThat(usuario.getBloqueadoAte()).isNotNull();
            assertThat(usuario.getBloqueadoAte()).isAfter(LocalDateTime.now().plusMinutes(29));
            verify(usuarioRepository).save(usuario);
        }

        @Test
        @DisplayName("Deve rejeitar login quando conta está bloqueada")
        void loginContaBloqueada() {
            LoginRequest request = new LoginRequest();
            request.setEmail("admin@sigaseguros.com");
            request.setSenha("admin123");

            usuario.setBloqueadoAte(LocalDateTime.now().plusMinutes(15));

            when(usuarioRepository.findByEmailAndActiveTrue("admin@sigaseguros.com"))
                    .thenReturn(Optional.of(usuario));

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("bloqueada");
        }

        @Test
        @DisplayName("Deve incrementar tentativas em credenciais inválidas")
        void loginIncrementarTentativas() {
            LoginRequest request = new LoginRequest();
            request.setEmail("admin@sigaseguros.com");
            request.setSenha("senhaerrada");

            usuario.setTentativasLogin(1);

            when(usuarioRepository.findByEmailAndActiveTrue("admin@sigaseguros.com"))
                    .thenReturn(Optional.of(usuario));
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(BadCredentialsException.class);

            assertThat(usuario.getTentativasLogin()).isEqualTo(2);
            assertThat(usuario.getBloqueadoAte()).isNull();
        }
    }

    @Nested
    @DisplayName("alterarSenha()")
    class AlterarSenha {

        @Test
        @DisplayName("Deve alterar senha com sucesso")
        void alterarSenhaComSucesso() {
            AlterarSenhaDTO dto = new AlterarSenhaDTO();
            dto.setSenhaAtual("admin123");
            dto.setNovaSenha("novaSenha123");

            when(usuarioRepository.findByEmailAndActiveTrue("admin@sigaseguros.com"))
                    .thenReturn(Optional.of(usuario));
            when(passwordEncoder.matches("admin123", usuario.getSenha())).thenReturn(true);
            when(passwordEncoder.encode("novaSenha123")).thenReturn("$2a$10$novasenhaencoded");

            authService.alterarSenha("admin@sigaseguros.com", dto);

            assertThat(usuario.getSenha()).isEqualTo("$2a$10$novasenhaencoded");
            assertThat(usuario.getSenhaTemporaria()).isFalse();
            verify(usuarioRepository).save(usuario);
            verify(auditoriaService).registrar("Usuario", 1L, "ALTERACAO_SENHA");
        }

        @Test
        @DisplayName("Deve rejeitar quando senha atual incorreta")
        void alterarSenhaSenhaAtualIncorreta() {
            AlterarSenhaDTO dto = new AlterarSenhaDTO();
            dto.setSenhaAtual("senhaerrada");
            dto.setNovaSenha("novaSenha123");

            when(usuarioRepository.findByEmailAndActiveTrue("admin@sigaseguros.com"))
                    .thenReturn(Optional.of(usuario));
            when(passwordEncoder.matches("senhaerrada", usuario.getSenha())).thenReturn(false);

            assertThatThrownBy(() -> authService.alterarSenha("admin@sigaseguros.com", dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Senha atual incorreta");
        }

        @Test
        @DisplayName("Deve lançar exceção quando usuário não encontrado")
        void alterarSenhaUsuarioNaoEncontrado() {
            AlterarSenhaDTO dto = new AlterarSenhaDTO();
            dto.setSenhaAtual("123");
            dto.setNovaSenha("456");

            when(usuarioRepository.findByEmailAndActiveTrue("x@x.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.alterarSenha("x@x.com", dto))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("refreshToken()")
    class RefreshToken {

        @Test
        @DisplayName("Deve renovar token com sucesso")
        void refreshTokenComSucesso() {
            when(tokenProvider.validateToken("valid-refresh")).thenReturn(true);
            when(tokenProvider.getEmailFromToken("valid-refresh")).thenReturn("admin@sigaseguros.com");
            when(usuarioRepository.findByEmailAndActiveTrue("admin@sigaseguros.com"))
                    .thenReturn(Optional.of(usuario));
            when(tokenProvider.generateToken(any(UserPrincipal.class))).thenReturn("new-jwt");
            when(tokenProvider.generateRefreshToken(any(UserPrincipal.class))).thenReturn("new-refresh");
            when(tokenProvider.getJwtExpirationMs()).thenReturn(3600000L);

            LoginResponse response = authService.refreshToken("valid-refresh");

            assertThat(response.getToken()).isEqualTo("new-jwt");
            assertThat(response.getRefreshToken()).isEqualTo("new-refresh");
            assertThat(response.getNome()).isEqualTo("Admin");
        }

        @Test
        @DisplayName("Deve rejeitar refresh token inválido")
        void refreshTokenInvalido() {
            when(tokenProvider.validateToken("invalid")).thenReturn(false);

            assertThatThrownBy(() -> authService.refreshToken("invalid"))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("inválido");
        }
    }
}
