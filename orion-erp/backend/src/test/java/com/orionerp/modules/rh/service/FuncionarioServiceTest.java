package com.orionerp.modules.rh.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.rh.domain.Funcionario;
import com.orionerp.modules.rh.dto.FuncionarioRequest;
import com.orionerp.modules.rh.dto.FuncionarioResponse;
import com.orionerp.modules.rh.repository.FuncionarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FuncionarioServiceTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @InjectMocks
    private FuncionarioService service;

    private Funcionario funcionario;

    @BeforeEach
    void setUp() {
        funcionario = new Funcionario();
        funcionario.setId(1L);
        funcionario.setEmpresaId(1L);
        funcionario.setFilialId(1L);
        funcionario.setCodigo("FUNC001");
        funcionario.setNome("Maria Santos");
        funcionario.setCpf("12345678901");
        funcionario.setDataAdmissao(LocalDate.of(2024, 1, 15));
        funcionario.setSalario(new BigDecimal("5000.00"));
        funcionario.setSituacao("ATIVO");
    }

    @Test
    void create_comDadosValidos_deveSalvar() {
        var request = new FuncionarioRequest(1L, 1L, "FUNC001", "Maria Santos",
                "12345678901", null, null, null, null, null, null, null, null,
                null, null, null, null, LocalDate.of(2024, 1, 15),
                new BigDecimal("5000.00"), null, null, null);

        when(funcionarioRepository.existsByEmpresaIdAndCodigoAndDeletedFalse(1L, "FUNC001")).thenReturn(false);
        when(funcionarioRepository.existsByEmpresaIdAndCpfAndDeletedFalse(1L, "12345678901")).thenReturn(false);
        when(funcionarioRepository.save(any())).thenReturn(funcionario);

        FuncionarioResponse response = service.create(request);

        assertNotNull(response);
        assertEquals("Maria Santos", response.nome());
        assertEquals("ATIVO", response.situacao());
        verify(funcionarioRepository).save(any());
    }

    @Test
    void create_codigoDuplicado_deveLancarExcecao() {
        var request = new FuncionarioRequest(1L, 1L, "FUNC001", "Maria Santos",
                "12345678901", null, null, null, null, null, null, null, null,
                null, null, null, null, LocalDate.of(2024, 1, 15),
                null, null, null, null);

        when(funcionarioRepository.existsByEmpresaIdAndCodigoAndDeletedFalse(1L, "FUNC001")).thenReturn(true);

        assertThrows(BusinessException.class, () -> service.create(request));
        verify(funcionarioRepository, never()).save(any());
    }

    @Test
    void create_cpfDuplicado_deveLancarExcecao() {
        var request = new FuncionarioRequest(1L, 1L, "FUNC002", "Outro",
                "12345678901", null, null, null, null, null, null, null, null,
                null, null, null, null, LocalDate.of(2024, 1, 15),
                null, null, null, null);

        when(funcionarioRepository.existsByEmpresaIdAndCodigoAndDeletedFalse(1L, "FUNC002")).thenReturn(false);
        when(funcionarioRepository.existsByEmpresaIdAndCpfAndDeletedFalse(1L, "12345678901")).thenReturn(true);

        assertThrows(BusinessException.class, () -> service.create(request));
    }

    @Test
    void demitir_funcionarioAtivo_deveAtualizarSituacao() {
        when(funcionarioRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(funcionario));
        when(funcionarioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        FuncionarioResponse response = service.demitir(1L, LocalDate.of(2025, 6, 30));

        assertEquals("DEMITIDO", response.situacao());
        assertEquals(LocalDate.of(2025, 6, 30), response.dataDemissao());
        assertFalse(response.ativo());
    }

    @Test
    void demitir_jaDemitido_deveLancarExcecao() {
        funcionario.setSituacao("DEMITIDO");
        when(funcionarioRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(funcionario));

        assertThrows(BusinessException.class, () -> service.demitir(1L, LocalDate.now()));
    }

    @Test
    void demitir_dataAnteriorAdmissao_deveLancarExcecao() {
        when(funcionarioRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(funcionario));

        assertThrows(BusinessException.class,
                () -> service.demitir(1L, LocalDate.of(2023, 1, 1)));
    }

    @Test
    void alterarSituacao_ativoParaFerias() {
        when(funcionarioRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(funcionario));
        when(funcionarioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        FuncionarioResponse response = service.alterarSituacao(1L, "FERIAS");

        assertEquals("FERIAS", response.situacao());
    }

    @Test
    void alterarSituacao_demitido_deveLancarExcecao() {
        funcionario.setSituacao("DEMITIDO");
        when(funcionarioRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(funcionario));

        assertThrows(BusinessException.class,
                () -> service.alterarSituacao(1L, "ATIVO"));
    }
}
