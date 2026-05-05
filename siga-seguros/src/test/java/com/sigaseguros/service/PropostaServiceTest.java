package com.sigaseguros.service;

import com.sigaseguros.dto.PropostaDTO;
import com.sigaseguros.entity.*;
import com.sigaseguros.enums.StatusProposta;
import com.sigaseguros.enums.TipoPessoa;
import com.sigaseguros.exception.ResourceNotFoundException;
import com.sigaseguros.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropostaServiceTest {

    @Mock
    private PropostaRepository propostaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private SeguradoraRepository seguradoraRepository;

    @Mock
    private CorretoraRepository corretoraRepository;

    @Mock
    private RamoSeguroRepository ramoSeguroRepository;

    @Mock
    private AuditoriaService auditoriaService;

    @InjectMocks
    private PropostaService propostaService;

    private Proposta proposta;
    private Cliente cliente;
    private Seguradora seguradora;
    private RamoSeguro ramoSeguro;
    private PropostaDTO propostaDTO;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setTipoPessoa(TipoPessoa.PF);
        cliente.setNome("João Silva");

        seguradora = new Seguradora();
        seguradora.setId(1L);
        seguradora.setNome("Seguradora ABC");

        ramoSeguro = new RamoSeguro();
        ramoSeguro.setId(1L);
        ramoSeguro.setNome("Auto");

        proposta = new Proposta();
        proposta.setId(1L);
        proposta.setNumeroProposta("PROP-2024-0001");
        proposta.setCliente(cliente);
        proposta.setSeguradora(seguradora);
        proposta.setRamoSeguro(ramoSeguro);
        proposta.setVigenciaInicial(LocalDate.of(2024, 6, 1));
        proposta.setVigenciaFinal(LocalDate.of(2025, 6, 1));
        proposta.setPremioLiquido(new BigDecimal("1000.00"));
        proposta.setPremioTotal(new BigDecimal("1200.00"));
        proposta.setPercentualComissao(new BigDecimal("15.00"));
        proposta.setValorComissao(new BigDecimal("180.00"));
        proposta.setStatus(StatusProposta.EM_ANALISE);
        proposta.setActive(true);
        proposta.setCreatedAt(LocalDateTime.of(2024, 6, 1, 10, 0));

        propostaDTO = new PropostaDTO();
        propostaDTO.setClienteId(1L);
        propostaDTO.setSeguradoraId(1L);
        propostaDTO.setRamoSeguroId(1L);
        propostaDTO.setVigenciaInicial("01/06/2024");
        propostaDTO.setVigenciaFinal("01/06/2025");
        propostaDTO.setPremioLiquido(new BigDecimal("1000.00"));
        propostaDTO.setPremioTotal(new BigDecimal("1200.00"));
        propostaDTO.setPercentualComissao(new BigDecimal("15.00"));
        propostaDTO.setObservacoes("Proposta teste");
    }

    @Nested
    @DisplayName("listar()")
    class Listar {

        @Test
        @DisplayName("Deve listar propostas paginadas com filtros")
        void listarComFiltros() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Proposta> page = new PageImpl<>(List.of(proposta), pageable, 1);

            when(propostaRepository.findAllWithFilters(eq(1L), isNull(), eq(StatusProposta.EM_ANALISE),
                    isNull(), isNull(), eq(pageable))).thenReturn(page);

            Page<PropostaDTO> resultado = propostaService.listar(1L, null, StatusProposta.EM_ANALISE,
                    null, null, pageable);

            assertThat(resultado.getTotalElements()).isEqualTo(1);
            PropostaDTO dto = resultado.getContent().getFirst();
            assertThat(dto.getNumeroProposta()).isEqualTo("PROP-2024-0001");
            assertThat(dto.getClienteNome()).isEqualTo("João Silva");
            assertThat(dto.getSeguradoraNome()).isEqualTo("Seguradora ABC");
            assertThat(dto.getVigenciaInicial()).isEqualTo("01/06/2024");
        }
    }

    @Nested
    @DisplayName("buscarPorId()")
    class BuscarPorId {

        @Test
        @DisplayName("Deve retornar proposta por ID")
        void buscarComSucesso() {
            when(propostaRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(proposta));

            PropostaDTO dto = propostaService.buscarPorId(1L);

            assertThat(dto.getId()).isEqualTo(1L);
            assertThat(dto.getNumeroProposta()).isEqualTo("PROP-2024-0001");
            assertThat(dto.getStatus()).isEqualTo(StatusProposta.EM_ANALISE);
            assertThat(dto.getPremioTotal()).isEqualByComparingTo(new BigDecimal("1200.00"));
            assertThat(dto.getRamoSeguroNome()).isEqualTo("Auto");
        }

        @Test
        @DisplayName("Deve lançar exceção quando proposta não encontrada")
        void buscarNaoEncontrada() {
            when(propostaRepository.findByIdAndActiveTrue(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> propostaService.buscarPorId(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Proposta não encontrada");
        }
    }

    @Nested
    @DisplayName("criar()")
    class Criar {

        @Test
        @DisplayName("Deve criar proposta com sucesso")
        void criarComSucesso() {
            when(clienteRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(cliente));
            when(seguradoraRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(seguradora));
            when(ramoSeguroRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(ramoSeguro));
            when(propostaRepository.count()).thenReturn(0L);
            when(propostaRepository.save(any(Proposta.class))).thenAnswer(invocation -> {
                Proposta p = invocation.getArgument(0);
                p.setId(1L);
                return p;
            });

            PropostaDTO resultado = propostaService.criar(propostaDTO);

            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getStatus()).isEqualTo(StatusProposta.EM_ANALISE);
            verify(auditoriaService).registrar(eq("Proposta"), eq(1L), eq("CRIAR"));
        }

        @Test
        @DisplayName("Deve criar proposta com corretora opcional")
        void criarComCorretora() {
            Corretora corretora = new Corretora();
            corretora.setId(1L);
            corretora.setNome("Corretora XYZ");

            propostaDTO.setCorretoraId(1L);

            when(clienteRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(cliente));
            when(seguradoraRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(seguradora));
            when(corretoraRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(corretora));
            when(ramoSeguroRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(ramoSeguro));
            when(propostaRepository.count()).thenReturn(5L);
            when(propostaRepository.save(any(Proposta.class))).thenAnswer(invocation -> {
                Proposta p = invocation.getArgument(0);
                p.setId(2L);
                return p;
            });

            PropostaDTO resultado = propostaService.criar(propostaDTO);

            assertThat(resultado.getId()).isEqualTo(2L);
            assertThat(resultado.getCorretoraNome()).isEqualTo("Corretora XYZ");
        }

        @Test
        @DisplayName("Deve calcular comissão automaticamente por percentual")
        void criarComCalculoComissao() {
            propostaDTO.setValorComissao(null);
            propostaDTO.setPremioTotal(new BigDecimal("2000.00"));
            propostaDTO.setPercentualComissao(new BigDecimal("10.00"));

            when(clienteRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(cliente));
            when(seguradoraRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(seguradora));
            when(ramoSeguroRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(ramoSeguro));
            when(propostaRepository.count()).thenReturn(0L);
            when(propostaRepository.save(any(Proposta.class))).thenAnswer(invocation -> {
                Proposta p = invocation.getArgument(0);
                p.setId(1L);
                return p;
            });

            PropostaDTO resultado = propostaService.criar(propostaDTO);

            assertThat(resultado.getValorComissao()).isEqualByComparingTo(new BigDecimal("200.00"));
        }

        @Test
        @DisplayName("Deve lançar exceção quando cliente não encontrado")
        void criarClienteNaoEncontrado() {
            when(clienteRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> propostaService.criar(propostaDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Cliente não encontrado");
        }

        @Test
        @DisplayName("Deve lançar exceção quando seguradora não encontrada")
        void criarSeguradoraNaoEncontrada() {
            when(clienteRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(cliente));
            when(seguradoraRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> propostaService.criar(propostaDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Seguradora não encontrada");
        }
    }

    @Nested
    @DisplayName("alterarStatus()")
    class AlterarStatus {

        @Test
        @DisplayName("Deve alterar status da proposta")
        void alterarStatusComSucesso() {
            when(propostaRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(proposta));
            when(propostaRepository.save(any(Proposta.class))).thenReturn(proposta);

            PropostaDTO resultado = propostaService.alterarStatus(1L, StatusProposta.APROVADO);

            assertThat(proposta.getStatus()).isEqualTo(StatusProposta.APROVADO);
            verify(auditoriaService).registrar(eq("Proposta"), eq(1L), eq("ALTERAR_STATUS"),
                    isNull(), eq("APROVADO"));
        }
    }

    @Nested
    @DisplayName("inativar()")
    class Inativar {

        @Test
        @DisplayName("Deve inativar proposta com sucesso")
        void inativarComSucesso() {
            when(propostaRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(proposta));

            propostaService.inativar(1L);

            assertThat(proposta.getActive()).isFalse();
            verify(propostaRepository).save(proposta);
            verify(auditoriaService).registrar("Proposta", 1L, "INATIVAR");
        }
    }

    @Nested
    @DisplayName("ultimasPropostas()")
    class UltimasPropostas {

        @Test
        @DisplayName("Deve retornar as últimas 10 propostas")
        void ultimasPropostasComSucesso() {
            when(propostaRepository.findTop10ByActiveTrueOrderByCreatedAtDesc())
                    .thenReturn(List.of(proposta));

            List<PropostaDTO> resultado = propostaService.ultimasPropostas();

            assertThat(resultado).hasSize(1);
            assertThat(resultado.getFirst().getNumeroProposta()).isEqualTo("PROP-2024-0001");
        }
    }
}
