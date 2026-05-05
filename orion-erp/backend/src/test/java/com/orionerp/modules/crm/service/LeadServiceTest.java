package com.orionerp.modules.crm.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.crm.domain.Lead;
import com.orionerp.modules.crm.dto.LeadRequest;
import com.orionerp.modules.crm.dto.LeadResponse;
import com.orionerp.modules.crm.repository.LeadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeadServiceTest {

    @Mock
    private LeadRepository leadRepository;

    @InjectMocks
    private LeadService service;

    private Lead lead;

    @BeforeEach
    void setUp() {
        lead = new Lead();
        lead.setId(1L);
        lead.setEmpresaId(1L);
        lead.setNome("João Silva");
        lead.setEmail("joao@empresa.com");
        lead.setTelefone("11999999999");
        lead.setEmpresaLead("Tech Corp");
        lead.setOrigem("SITE");
        lead.setStatus("NOVO");
    }

    @Test
    void create_deveSalvarComStatusNovo() {
        var request = new LeadRequest(1L, "João Silva", "joao@empresa.com",
                "11999999999", "Tech Corp", null, "SITE", null, null);

        when(leadRepository.save(any())).thenReturn(lead);

        LeadResponse response = service.create(request);

        assertNotNull(response);
        assertEquals("João Silva", response.nome());
        verify(leadRepository).save(any());
    }

    @Test
    void avancarStatus_novoParaContatado() {
        when(leadRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(lead));
        when(leadRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LeadResponse response = service.avancarStatus(1L);

        assertEquals("CONTATADO", response.status());
    }

    @Test
    void avancarStatus_contatadoParaQualificado() {
        lead.setStatus("CONTATADO");
        when(leadRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(lead));
        when(leadRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LeadResponse response = service.avancarStatus(1L);

        assertEquals("QUALIFICADO", response.status());
    }

    @Test
    void avancarStatus_qualificadoNaoPodeAvancar() {
        lead.setStatus("QUALIFICADO");
        when(leadRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(lead));

        assertThrows(BusinessException.class, () -> service.avancarStatus(1L));
    }

    @Test
    void converter_qualificadoParaConvertido() {
        lead.setStatus("QUALIFICADO");
        when(leadRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(lead));
        when(leadRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LeadResponse response = service.converter(1L, 10L);

        assertEquals("CONVERTIDO", response.status());
        assertEquals(10L, response.convertidoClienteId());
    }

    @Test
    void converter_naoQualificado_deveLancarExcecao() {
        lead.setStatus("NOVO");
        when(leadRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(lead));

        assertThrows(BusinessException.class, () -> service.converter(1L, 10L));
    }

    @Test
    void perder_deveMarcarComoPerdido() {
        when(leadRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(lead));
        when(leadRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LeadResponse response = service.perder(1L, "Sem orçamento");

        assertEquals("PERDIDO", response.status());
    }

    @Test
    void perder_jaConvertido_deveLancarExcecao() {
        lead.setStatus("CONVERTIDO");
        when(leadRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(lead));

        assertThrows(BusinessException.class, () -> service.perder(1L, "Motivo"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void list_deveRetornarPaginado() {
        var page = new PageImpl<>(List.of(lead));
        when(leadRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        var result = service.list(1L, null, null, null, Pageable.unpaged());

        assertEquals(1, result.getItems().size());
        assertEquals("João Silva", result.getItems().get(0).nome());
    }
}
