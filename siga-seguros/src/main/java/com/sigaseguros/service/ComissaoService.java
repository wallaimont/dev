package com.sigaseguros.service;

import com.sigaseguros.dto.ComissaoDTO;
import com.sigaseguros.entity.Comissao;
import com.sigaseguros.enums.StatusComissao;
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
public class ComissaoService {

    private final ComissaoRepository comissaoRepository;
    private final PropostaRepository propostaRepository;
    private final ApoliceRepository apoliceRepository;
    private final SeguradoraRepository seguradoraRepository;
    private final CorretoraRepository corretoraRepository;
    private final AuditoriaService auditoriaService;
    private static final DateTimeFormatter FMT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_DT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Transactional(readOnly = true)
    public Page<ComissaoDTO> listar(StatusComissao status, Long seguradoraId, Long corretoraId, Pageable pageable) {
        return comissaoRepository.findAllWithFilters(status, seguradoraId, corretoraId, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public ComissaoDTO buscarPorId(Long id) {
        return toDTO(findById(id));
    }

    @Transactional
    public ComissaoDTO criar(ComissaoDTO dto) {
        Comissao c = new Comissao();
        c.setOrigem(dto.getOrigem());
        if (dto.getPropostaId() != null) {
            c.setProposta(propostaRepository.findByIdAndActiveTrue(dto.getPropostaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Proposta não encontrada")));
        }
        if (dto.getApoliceId() != null) {
            c.setApolice(apoliceRepository.findByIdAndActiveTrue(dto.getApoliceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Apólice não encontrada")));
        }
        if (dto.getSeguradoraId() != null) {
            c.setSeguradora(seguradoraRepository.findByIdAndActiveTrue(dto.getSeguradoraId())
                    .orElseThrow(() -> new ResourceNotFoundException("Seguradora não encontrada")));
        }
        if (dto.getCorretoraId() != null) {
            c.setCorretora(corretoraRepository.findByIdAndActiveTrue(dto.getCorretoraId())
                    .orElseThrow(() -> new ResourceNotFoundException("Corretora não encontrada")));
        }
        c.setFavorecido(dto.getFavorecido());
        c.setPercentual(dto.getPercentual());
        c.setValor(dto.getValor());
        c.setStatus(StatusComissao.PENDENTE);
        if (dto.getDataPrevista() != null) {
            c.setDataPrevista(LocalDate.parse(dto.getDataPrevista(), FMT_DATE));
        }
        c.setObservacoes(dto.getObservacoes());
        c.setActive(true);
        c = comissaoRepository.save(c);
        auditoriaService.registrar("Comissao", c.getId(), "CRIAR");
        return toDTO(c);
    }

    @Transactional
    public ComissaoDTO atualizar(Long id, ComissaoDTO dto) {
        Comissao c = findById(id);
        c.setOrigem(dto.getOrigem());
        c.setFavorecido(dto.getFavorecido());
        c.setPercentual(dto.getPercentual());
        c.setValor(dto.getValor());
        if (dto.getDataPrevista() != null) {
            c.setDataPrevista(LocalDate.parse(dto.getDataPrevista(), FMT_DATE));
        }
        if (dto.getDataRecebimento() != null) {
            c.setDataRecebimento(LocalDate.parse(dto.getDataRecebimento(), FMT_DATE));
        }
        c.setObservacoes(dto.getObservacoes());
        c = comissaoRepository.save(c);
        auditoriaService.registrar("Comissao", c.getId(), "ATUALIZAR");
        return toDTO(c);
    }

    @Transactional
    public ComissaoDTO alterarStatus(Long id, StatusComissao novoStatus) {
        Comissao c = findById(id);
        c.setStatus(novoStatus);
        if (novoStatus == StatusComissao.RECEBIDA && c.getDataRecebimento() == null) {
            c.setDataRecebimento(LocalDate.now());
        }
        c = comissaoRepository.save(c);
        auditoriaService.registrar("Comissao", c.getId(), "ALTERAR_STATUS", null, novoStatus.name());
        return toDTO(c);
    }

    private Comissao findById(Long id) {
        return comissaoRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comissão não encontrada"));
    }

    private ComissaoDTO toDTO(Comissao c) {
        ComissaoDTO dto = new ComissaoDTO();
        dto.setId(c.getId());
        dto.setOrigem(c.getOrigem());
        if (c.getProposta() != null) dto.setPropostaId(c.getProposta().getId());
        if (c.getApolice() != null) {
            dto.setApoliceId(c.getApolice().getId());
            dto.setApoliceNumero(c.getApolice().getNumeroApolice());
        }
        if (c.getSeguradora() != null) {
            dto.setSeguradoraId(c.getSeguradora().getId());
            dto.setSeguradoraNome(c.getSeguradora().getNome());
        }
        if (c.getCorretora() != null) {
            dto.setCorretoraId(c.getCorretora().getId());
            dto.setCorretoraNome(c.getCorretora().getNome());
        }
        dto.setFavorecido(c.getFavorecido());
        dto.setPercentual(c.getPercentual());
        dto.setValor(c.getValor());
        dto.setStatus(c.getStatus());
        if (c.getDataPrevista() != null) dto.setDataPrevista(c.getDataPrevista().format(FMT_DATE));
        if (c.getDataRecebimento() != null) dto.setDataRecebimento(c.getDataRecebimento().format(FMT_DATE));
        dto.setObservacoes(c.getObservacoes());
        dto.setActive(c.getActive());
        if (c.getCreatedAt() != null) dto.setCreatedAt(c.getCreatedAt().format(FMT_DT));
        return dto;
    }
}
