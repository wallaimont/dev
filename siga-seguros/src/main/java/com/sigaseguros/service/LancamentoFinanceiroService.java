package com.sigaseguros.service;

import com.sigaseguros.dto.LancamentoFinanceiroDTO;
import com.sigaseguros.entity.LancamentoFinanceiro;
import com.sigaseguros.enums.StatusFinanceiro;
import com.sigaseguros.enums.TipoLancamento;
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
public class LancamentoFinanceiroService {

    private final LancamentoFinanceiroRepository lancamentoRepository;
    private final ClienteRepository clienteRepository;
    private final SeguradoraRepository seguradoraRepository;
    private final CorretoraRepository corretoraRepository;
    private final ApoliceRepository apoliceRepository;
    private final PropostaRepository propostaRepository;
    private final AuditoriaService auditoriaService;
    private static final DateTimeFormatter FMT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_DT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Transactional(readOnly = true)
    public Page<LancamentoFinanceiroDTO> listar(TipoLancamento tipo, StatusFinanceiro status,
                                                 LocalDate dataInicio, LocalDate dataFim, Pageable pageable) {
        return lancamentoRepository.findAllWithFilters(tipo, status, dataInicio, dataFim, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public LancamentoFinanceiroDTO buscarPorId(Long id) {
        return toDTO(findById(id));
    }

    @Transactional
    public LancamentoFinanceiroDTO criar(LancamentoFinanceiroDTO dto) {
        LancamentoFinanceiro l = new LancamentoFinanceiro();
        preencherLancamento(l, dto);
        l.setStatus(StatusFinanceiro.PENDENTE);
        l.setActive(true);
        l = lancamentoRepository.save(l);
        auditoriaService.registrar("LancamentoFinanceiro", l.getId(), "CRIAR");
        return toDTO(l);
    }

    @Transactional
    public LancamentoFinanceiroDTO atualizar(Long id, LancamentoFinanceiroDTO dto) {
        LancamentoFinanceiro l = findById(id);
        preencherLancamento(l, dto);
        if (dto.getStatus() != null) l.setStatus(dto.getStatus());
        l = lancamentoRepository.save(l);
        auditoriaService.registrar("LancamentoFinanceiro", l.getId(), "ATUALIZAR");
        return toDTO(l);
    }

    @Transactional
    public LancamentoFinanceiroDTO registrarBaixa(Long id, String dataPagamento) {
        LancamentoFinanceiro l = findById(id);
        l.setStatus(StatusFinanceiro.PAGO);
        l.setDataPagamento(dataPagamento != null ? LocalDate.parse(dataPagamento, FMT_DATE) : LocalDate.now());
        l = lancamentoRepository.save(l);
        auditoriaService.registrar("LancamentoFinanceiro", l.getId(), "BAIXA");
        return toDTO(l);
    }

    private void preencherLancamento(LancamentoFinanceiro l, LancamentoFinanceiroDTO dto) {
        l.setTipoLancamento(dto.getTipoLancamento());
        l.setOrigem(dto.getOrigem());
        if (dto.getClienteId() != null)
            l.setCliente(clienteRepository.findByIdAndActiveTrue(dto.getClienteId()).orElse(null));
        if (dto.getSeguradoraId() != null)
            l.setSeguradora(seguradoraRepository.findByIdAndActiveTrue(dto.getSeguradoraId()).orElse(null));
        if (dto.getCorretoraId() != null)
            l.setCorretora(corretoraRepository.findByIdAndActiveTrue(dto.getCorretoraId()).orElse(null));
        if (dto.getApoliceId() != null)
            l.setApolice(apoliceRepository.findByIdAndActiveTrue(dto.getApoliceId()).orElse(null));
        if (dto.getPropostaId() != null)
            l.setProposta(propostaRepository.findByIdAndActiveTrue(dto.getPropostaId()).orElse(null));
        l.setDescricao(dto.getDescricao());
        l.setValor(dto.getValor());
        l.setVencimento(LocalDate.parse(dto.getVencimento(), FMT_DATE));
        if (dto.getDataPagamento() != null)
            l.setDataPagamento(LocalDate.parse(dto.getDataPagamento(), FMT_DATE));
        l.setFormaPagamento(dto.getFormaPagamento());
        l.setObservacoes(dto.getObservacoes());
        l.setNumeroParcela(dto.getNumeroParcela());
        l.setTotalParcelas(dto.getTotalParcelas());
    }

    private LancamentoFinanceiro findById(Long id) {
        return lancamentoRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lançamento financeiro não encontrado"));
    }

    private LancamentoFinanceiroDTO toDTO(LancamentoFinanceiro l) {
        LancamentoFinanceiroDTO dto = new LancamentoFinanceiroDTO();
        dto.setId(l.getId());
        dto.setTipoLancamento(l.getTipoLancamento());
        dto.setOrigem(l.getOrigem());
        if (l.getCliente() != null) {
            dto.setClienteId(l.getCliente().getId());
            dto.setClienteNome(l.getCliente().getNomeExibicao());
        }
        if (l.getCorretora() != null) {
            dto.setCorretoraId(l.getCorretora().getId());
            dto.setCorretoraNome(l.getCorretora().getNome());
        }
        if (l.getSeguradora() != null) {
            dto.setSeguradoraId(l.getSeguradora().getId());
            dto.setSeguradoraNome(l.getSeguradora().getNome());
        }
        if (l.getProposta() != null) dto.setPropostaId(l.getProposta().getId());
        if (l.getApolice() != null) dto.setApoliceId(l.getApolice().getId());
        dto.setDescricao(l.getDescricao());
        dto.setValor(l.getValor());
        dto.setVencimento(l.getVencimento().format(FMT_DATE));
        dto.setDataPagamento(l.getDataPagamento() != null ? l.getDataPagamento().format(FMT_DATE) : null);
        dto.setFormaPagamento(l.getFormaPagamento());
        dto.setStatus(l.getStatus());
        dto.setObservacoes(l.getObservacoes());
        dto.setNumeroParcela(l.getNumeroParcela());
        dto.setTotalParcelas(l.getTotalParcelas());
        dto.setActive(l.getActive());
        dto.setCreatedAt(l.getCreatedAt() != null ? l.getCreatedAt().format(FMT_DT) : null);
        return dto;
    }
}
