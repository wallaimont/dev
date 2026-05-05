package com.sigaseguros.service;

import com.sigaseguros.dto.ClienteDTO;
import com.sigaseguros.entity.Cliente;
import com.sigaseguros.enums.TipoPessoa;
import com.sigaseguros.exception.BusinessException;
import com.sigaseguros.exception.ResourceNotFoundException;
import com.sigaseguros.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private AuditoriaService auditoriaService;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente clientePF;
    private ClienteDTO clientePFDTO;

    @BeforeEach
    void setUp() {
        clientePF = new Cliente();
        clientePF.setId(1L);
        clientePF.setTipoPessoa(TipoPessoa.PF);
        clientePF.setNome("João Silva");
        clientePF.setCpf("12345678901");
        clientePF.setEmail("joao@email.com");
        clientePF.setTelefone("11999999999");
        clientePF.setCidade("São Paulo");
        clientePF.setEstado("SP");
        clientePF.setDataNascimento(LocalDate.of(1990, 5, 15));
        clientePF.setActive(true);
        clientePF.setCreatedAt(LocalDateTime.of(2024, 1, 10, 14, 30));

        clientePFDTO = new ClienteDTO();
        clientePFDTO.setTipoPessoa(TipoPessoa.PF);
        clientePFDTO.setNome("João Silva");
        clientePFDTO.setCpf("12345678901");
        clientePFDTO.setEmail("joao@email.com");
        clientePFDTO.setTelefone("11999999999");
        clientePFDTO.setCidade("São Paulo");
        clientePFDTO.setEstado("SP");
        clientePFDTO.setDataNascimento("15/05/1990");
    }

    @Nested
    @DisplayName("listar()")
    class Listar {

        @Test
        @DisplayName("Deve listar clientes paginados com filtros")
        void listarComFiltros() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Cliente> page = new PageImpl<>(List.of(clientePF), pageable, 1);

            when(clienteRepository.findAllWithFilters("João", null, TipoPessoa.PF, null, pageable))
                    .thenReturn(page);

            Page<ClienteDTO> resultado = clienteService.listar("João", null, TipoPessoa.PF, null, pageable);

            assertThat(resultado.getTotalElements()).isEqualTo(1);
            assertThat(resultado.getContent().getFirst().getNome()).isEqualTo("João Silva");
            assertThat(resultado.getContent().getFirst().getDataNascimento()).isEqualTo("15/05/1990");
        }

        @Test
        @DisplayName("Deve retornar página vazia quando sem resultados")
        void listarSemResultados() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Cliente> page = new PageImpl<>(List.of(), pageable, 0);

            when(clienteRepository.findAllWithFilters(any(), any(), any(), any(), eq(pageable)))
                    .thenReturn(page);

            Page<ClienteDTO> resultado = clienteService.listar(null, null, null, null, pageable);

            assertThat(resultado.getTotalElements()).isZero();
        }
    }

    @Nested
    @DisplayName("buscarPorId()")
    class BuscarPorId {

        @Test
        @DisplayName("Deve retornar cliente por ID")
        void buscarComSucesso() {
            when(clienteRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(clientePF));

            ClienteDTO dto = clienteService.buscarPorId(1L);

            assertThat(dto.getId()).isEqualTo(1L);
            assertThat(dto.getNome()).isEqualTo("João Silva");
            assertThat(dto.getCpf()).isEqualTo("12345678901");
            assertThat(dto.getTipoPessoa()).isEqualTo(TipoPessoa.PF);
            assertThat(dto.getNomeExibicao()).isEqualTo("João Silva");
            assertThat(dto.getCreatedAt()).isEqualTo("10/01/2024 14:30");
        }

        @Test
        @DisplayName("Deve lançar exceção quando cliente não encontrado")
        void buscarNaoEncontrado() {
            when(clienteRepository.findByIdAndActiveTrue(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> clienteService.buscarPorId(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Cliente não encontrado");
        }
    }

    @Nested
    @DisplayName("criar()")
    class Criar {

        @Test
        @DisplayName("Deve criar cliente PF com sucesso")
        void criarClientePF() {
            when(clienteRepository.existsByCpfAndActiveTrue("12345678901")).thenReturn(false);
            when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> {
                Cliente c = invocation.getArgument(0);
                c.setId(1L);
                c.setCreatedAt(LocalDateTime.now());
                return c;
            });

            ClienteDTO resultado = clienteService.criar(clientePFDTO);

            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getNome()).isEqualTo("João Silva");

            ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
            verify(clienteRepository).save(captor.capture());
            assertThat(captor.getValue().getActive()).isTrue();
            assertThat(captor.getValue().getDataNascimento()).isEqualTo(LocalDate.of(1990, 5, 15));

            verify(auditoriaService).registrar(eq("Cliente"), eq(1L), eq("CRIAR"));
        }

        @Test
        @DisplayName("Deve criar cliente PJ com sucesso")
        void criarClientePJ() {
            ClienteDTO pjDTO = new ClienteDTO();
            pjDTO.setTipoPessoa(TipoPessoa.PJ);
            pjDTO.setRazaoSocial("Empresa ABC Ltda");
            pjDTO.setNomeFantasia("ABC Seguros");
            pjDTO.setCnpj("12345678000199");
            pjDTO.setEmail("contato@abc.com");

            when(clienteRepository.existsByCnpjAndActiveTrue("12345678000199")).thenReturn(false);
            when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> {
                Cliente c = invocation.getArgument(0);
                c.setId(2L);
                return c;
            });

            ClienteDTO resultado = clienteService.criar(pjDTO);

            assertThat(resultado.getId()).isEqualTo(2L);
            assertThat(resultado.getRazaoSocial()).isEqualTo("Empresa ABC Ltda");
        }

        @Test
        @DisplayName("Deve rejeitar CPF duplicado")
        void criarCpfDuplicado() {
            when(clienteRepository.existsByCpfAndActiveTrue("12345678901")).thenReturn(true);

            assertThatThrownBy(() -> clienteService.criar(clientePFDTO))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("CPF já cadastrado");

            verify(clienteRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve rejeitar CNPJ duplicado")
        void criarCnpjDuplicado() {
            ClienteDTO pjDTO = new ClienteDTO();
            pjDTO.setTipoPessoa(TipoPessoa.PJ);
            pjDTO.setCnpj("12345678000199");

            when(clienteRepository.existsByCnpjAndActiveTrue("12345678000199")).thenReturn(true);

            assertThatThrownBy(() -> clienteService.criar(pjDTO))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("CNPJ já cadastrado");
        }
    }

    @Nested
    @DisplayName("atualizar()")
    class Atualizar {

        @Test
        @DisplayName("Deve atualizar cliente com sucesso")
        void atualizarComSucesso() {
            ClienteDTO atualizacao = new ClienteDTO();
            atualizacao.setTipoPessoa(TipoPessoa.PF);
            atualizacao.setNome("João Silva Atualizado");
            atualizacao.setCpf("12345678901");
            atualizacao.setEmail("joao.novo@email.com");

            when(clienteRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(clientePF));
            when(clienteRepository.existsByCpfAndIdNotAndActiveTrue("12345678901", 1L)).thenReturn(false);
            when(clienteRepository.save(any(Cliente.class))).thenReturn(clientePF);

            ClienteDTO resultado = clienteService.atualizar(1L, atualizacao);

            assertThat(resultado).isNotNull();
            verify(clienteRepository).save(clientePF);
            verify(auditoriaService).registrar(eq("Cliente"), eq(1L), eq("ATUALIZAR"));
        }

        @Test
        @DisplayName("Deve rejeitar atualização com CPF duplicado de outro cliente")
        void atualizarCpfDuplicadoOutroCliente() {
            ClienteDTO atualizacao = new ClienteDTO();
            atualizacao.setTipoPessoa(TipoPessoa.PF);
            atualizacao.setCpf("99988877766");

            when(clienteRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(clientePF));
            when(clienteRepository.existsByCpfAndIdNotAndActiveTrue("99988877766", 1L)).thenReturn(true);

            assertThatThrownBy(() -> clienteService.atualizar(1L, atualizacao))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("CPF já cadastrado");
        }
    }

    @Nested
    @DisplayName("inativar()")
    class Inativar {

        @Test
        @DisplayName("Deve inativar cliente com sucesso")
        void inativarComSucesso() {
            when(clienteRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(clientePF));

            clienteService.inativar(1L);

            assertThat(clientePF.getActive()).isFalse();
            verify(clienteRepository).save(clientePF);
            verify(auditoriaService).registrar("Cliente", 1L, "INATIVAR");
        }

        @Test
        @DisplayName("Deve lançar exceção ao inativar cliente inexistente")
        void inativarNaoEncontrado() {
            when(clienteRepository.findByIdAndActiveTrue(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> clienteService.inativar(999L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
