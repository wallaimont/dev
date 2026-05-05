package com.sigaseguros.service;

import com.sigaseguros.dto.SinistroDTO;
import com.sigaseguros.entity.Sinistro;
import com.sigaseguros.enums.StatusSinistro;
import com.sigaseguros.exception.ResourceNotFoundException;
import com.sigaseguros.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SinistroService {

    private final SinistroRepository sinistroRepository;
    private final ApoliceRepository apoliceRepository;
    private final ClienteRepository clienteRepository;
    private final SeguradoraRepository seguradoraRepository;
    private final AuditoriaService auditoriaService;
    private static final DateTimeFormatter FMT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_DT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Transactional(readOnly = true)
    public Page<SinistroDTO> listar(Long clienteId, Long seguradoraId, StatusSinistro status, Pageable pageable) {
        return sinistroRepository.findAllWithFilters(clienteId, seguradoraId, status, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public SinistroDTO buscarPorId(Long id) {
        return toDTO(findById(id));
    }

    @Transactional(readOnly = true)
    public List<SinistroDTO> sinistrosEmAberto() {
        return sinistroRepository.findTop10ByActiveTrueAndStatusInOrderByDataAvisoDesc(
                List.of(StatusSinistro.ABERTO, StatusSinistro.EM_ANALISE, StatusSinistro.DOCUMENTACAO_PENDENTE))
                .stream().map(this::toDTO).toList();
    }

    @Transactional
    public SinistroDTO criar(SinistroDTO dto) {
        Sinistro s = new Sinistro();
        s.setNumeroSinistro(gerarNumeroSinistro());
        s.setApolice(apoliceRepository.findByIdAndActiveTrue(dto.getApoliceId())
                .orElseThrow(() -> new ResourceNotFoundException("Apólice não encontrada")));
        s.setCliente(clienteRepository.findByIdAndActiveTrue(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado")));
        s.setSeguradora(seguradoraRepository.findByIdAndActiveTrue(dto.getSeguradoraId())
                .orElseThrow(() -> new ResourceNotFoundException("Seguradora não encontrada")));
        s.setDataAviso(LocalDate.parse(dto.getDataAviso(), FMT_DATE));
        s.setDescricao(dto.getDescricao());
        s.setValorEstimado(dto.getValorEstimado());
        s.setValorPago(dto.getValorPago());
        s.setStatus(StatusSinistro.ABERTO);
        s.setResponsavelInterno(dto.getResponsavelInterno());
        s.setObservacoes(dto.getObservacoes());
        s.setActive(true);
        s = sinistroRepository.save(s);
        auditoriaService.registrar("Sinistro", s.getId(), "CRIAR");
        return toDTO(s);
    }

    @Transactional
    public SinistroDTO atualizar(Long id, SinistroDTO dto) {
        Sinistro s = findById(id);
        s.setDescricao(dto.getDescricao());
        s.setValorEstimado(dto.getValorEstimado());
        s.setValorPago(dto.getValorPago());
        if (dto.getStatus() != null) s.setStatus(dto.getStatus());
        s.setResponsavelInterno(dto.getResponsavelInterno());
        s.setObservacoes(dto.getObservacoes());
        s = sinistroRepository.save(s);
        auditoriaService.registrar("Sinistro", s.getId(), "ATUALIZAR");
        return toDTO(s);
    }

    @Transactional
    public SinistroDTO alterarStatus(Long id, StatusSinistro novoStatus) {
        Sinistro s = findById(id);
        s.setStatus(novoStatus);
        s = sinistroRepository.save(s);
        auditoriaService.registrar("Sinistro", s.getId(), "ALTERAR_STATUS", null, novoStatus.name());
        return toDTO(s);
    }

    private String gerarNumeroSinistro() {
        long count = sinistroRepository.count() + 1;
        return String.format("SIN-%d-%04d", Year.now().getValue(), count);
    }

    private Sinistro findById(Long id) {
        return sinistroRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sinistro não encontrado"));
    }

    private SinistroDTO toDTO(Sinistro s) {
        SinistroDTO dto = new SinistroDTO();
        dto.setId(s.getId());
        dto.setNumeroSinistro(s.getNumeroSinistro());
        dto.setApoliceId(s.getApolice().getId());
        dto.setApoliceNumero(s.getApolice().getNumeroApolice());
        dto.setClienteId(s.getCliente().getId());
        dto.setClienteNome(s.getCliente().getNomeExibicao());
        dto.setSeguradoraId(s.getSeguradora().getId());
        dto.setSeguradoraNome(s.getSeguradora().getNome());
        dto.setDataAviso(s.getDataAviso().format(FMT_DATE));
        dto.setDescricao(s.getDescricao());
        dto.setValorEstimado(s.getValorEstimado());
        dto.setValorPago(s.getValorPago());
        dto.setStatus(s.getStatus());
        dto.setResponsavelInterno(s.getResponsavelInterno());
        dto.setObservacoes(s.getObservacoes());
        dto.setActive(s.getActive());
        dto.setCreatedAt(s.getCreatedAt() != null ? s.getCreatedAt().format(FMT_DT) : null);
        return dto;
    }
}
