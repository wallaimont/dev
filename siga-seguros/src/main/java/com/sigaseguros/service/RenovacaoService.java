package com.sigaseguros.service;

import com.sigaseguros.dto.RenovacaoDTO;
import com.sigaseguros.entity.Renovacao;
import com.sigaseguros.enums.StatusRenovacao;
import com.sigaseguros.exception.ResourceNotFoundException;
import com.sigaseguros.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class RenovacaoService {

    private final RenovacaoRepository renovacaoRepository;
    private final ApoliceRepository apoliceRepository;
    private final ClienteRepository clienteRepository;
    private final PropostaRepository propostaRepository;
    private final AuditoriaService auditoriaService;
    private static final DateTimeFormatter FMT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_DT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Transactional(readOnly = true)
    public Page<RenovacaoDTO> listar(StatusRenovacao status, Long clienteId, Pageable pageable) {
        return renovacaoRepository.findAllWithFilters(status, clienteId, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public RenovacaoDTO buscarPorId(Long id) {
        return toDTO(findById(id));
    }

    @Transactional
    public RenovacaoDTO criar(RenovacaoDTO dto) {
        Renovacao r = new Renovacao();
        r.setApolice(apoliceRepository.findByIdAndActiveTrue(dto.getApoliceId())
                .orElseThrow(() -> new ResourceNotFoundException("Apólice não encontrada")));
        r.setCliente(clienteRepository.findByIdAndActiveTrue(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado")));
        if (dto.getDataVencimento() != null) {
            r.setDataVencimento(LocalDate.parse(dto.getDataVencimento(), FMT_DATE));
        } else {
            r.setDataVencimento(r.getApolice().getFimVigencia());
        }
        r.setStatus(StatusRenovacao.PENDENTE);
        r.setResponsavel(dto.getResponsavel());
        r.setObservacoes(dto.getObservacoes());
        r.setActive(true);
        r = renovacaoRepository.save(r);
        auditoriaService.registrar("Renovacao", r.getId(), "CRIAR");
        return toDTO(r);
    }

    @Transactional
    public RenovacaoDTO atualizar(Long id, RenovacaoDTO dto) {
        Renovacao r = findById(id);
        r.setResponsavel(dto.getResponsavel());
        if (dto.getDataUltimoContato() != null) {
            r.setDataUltimoContato(LocalDate.parse(dto.getDataUltimoContato(), FMT_DATE));
        }
        r.setRetornoCliente(dto.getRetornoCliente());
        r.setObservacoes(dto.getObservacoes());
        if (dto.getNovaPropostaId() != null) {
            r.setNovaProposta(propostaRepository.findByIdAndActiveTrue(dto.getNovaPropostaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Proposta não encontrada")));
        }
        r = renovacaoRepository.save(r);
        auditoriaService.registrar("Renovacao", r.getId(), "ATUALIZAR");
        return toDTO(r);
    }

    @Transactional
    public RenovacaoDTO alterarStatus(Long id, StatusRenovacao novoStatus) {
        Renovacao r = findById(id);
        r.setStatus(novoStatus);
        r = renovacaoRepository.save(r);
        auditoriaService.registrar("Renovacao", r.getId(), "ALTERAR_STATUS", null, novoStatus.name());
        return toDTO(r);
    }

    @Transactional
    public void inativar(Long id) {
        Renovacao r = findById(id);
        r.setActive(false);
        renovacaoRepository.save(r);
        auditoriaService.registrar("Renovacao", r.getId(), "INATIVAR");
    }

    private Renovacao findById(Long id) {
        return renovacaoRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Renovação não encontrada"));
    }

    private RenovacaoDTO toDTO(Renovacao r) {
        RenovacaoDTO dto = new RenovacaoDTO();
        dto.setId(r.getId());
        dto.setApoliceId(r.getApolice().getId());
        dto.setApoliceNumero(r.getApolice().getNumeroApolice());
        dto.setClienteId(r.getCliente().getId());
        dto.setClienteNome(r.getCliente().getNomeExibicao());
        if (r.getDataVencimento() != null) dto.setDataVencimento(r.getDataVencimento().format(FMT_DATE));
        dto.setStatus(r.getStatus());
        dto.setResponsavel(r.getResponsavel());
        if (r.getDataUltimoContato() != null) dto.setDataUltimoContato(r.getDataUltimoContato().format(FMT_DATE));
        dto.setRetornoCliente(r.getRetornoCliente());
        dto.setObservacoes(r.getObservacoes());
        if (r.getNovaProposta() != null) dto.setNovaPropostaId(r.getNovaProposta().getId());
        dto.setActive(r.getActive());
        if (r.getCreatedAt() != null) dto.setCreatedAt(r.getCreatedAt().format(FMT_DT));
        return dto;
    }
}
