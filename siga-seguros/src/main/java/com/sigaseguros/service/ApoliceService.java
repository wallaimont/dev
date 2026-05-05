package com.sigaseguros.service;

import com.sigaseguros.dto.ApoliceDTO;
import com.sigaseguros.entity.*;
import com.sigaseguros.enums.StatusApolice;
import com.sigaseguros.exception.ResourceNotFoundException;
import com.sigaseguros.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApoliceService {

    private final ApoliceRepository apoliceRepository;
    private final PropostaRepository propostaRepository;
    private final ClienteRepository clienteRepository;
    private final SeguradoraRepository seguradoraRepository;
    private final CorretoraRepository corretoraRepository;
    private final RamoSeguroRepository ramoSeguroRepository;
    private final AuditoriaService auditoriaService;
    private static final DateTimeFormatter FMT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_DT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Transactional(readOnly = true)
    public Page<ApoliceDTO> listar(Long clienteId, Long seguradoraId, StatusApolice status, Long ramoId, Pageable pageable) {
        return apoliceRepository.findAllWithFilters(clienteId, seguradoraId, status, ramoId, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public ApoliceDTO buscarPorId(Long id) {
        return toDTO(findById(id));
    }

    @Transactional(readOnly = true)
    public List<ApoliceDTO> vencendoEm30Dias() {
        LocalDate hoje = LocalDate.now();
        return apoliceRepository.findVencendoEntre(hoje, hoje.plusDays(30)).stream().map(this::toDTO).toList();
    }

    @Transactional
    public ApoliceDTO criar(ApoliceDTO dto) {
        Apolice a = new Apolice();
        a.setNumeroApolice(gerarNumeroApolice());
        preencherApolice(a, dto);
        a.setStatus(StatusApolice.ATIVA);
        a.setActive(true);
        a = apoliceRepository.save(a);
        auditoriaService.registrar("Apolice", a.getId(), "CRIAR");
        return toDTO(a);
    }

    @Transactional
    public ApoliceDTO atualizar(Long id, ApoliceDTO dto) {
        Apolice a = findById(id);
        preencherApolice(a, dto);
        if (dto.getStatus() != null) a.setStatus(dto.getStatus());
        a = apoliceRepository.save(a);
        auditoriaService.registrar("Apolice", a.getId(), "ATUALIZAR");
        return toDTO(a);
    }

    @Transactional
    public ApoliceDTO alterarStatus(Long id, StatusApolice novoStatus) {
        Apolice a = findById(id);
        a.setStatus(novoStatus);
        a = apoliceRepository.save(a);
        auditoriaService.registrar("Apolice", a.getId(), "ALTERAR_STATUS", null, novoStatus.name());
        return toDTO(a);
    }

    @Transactional
    public void inativar(Long id) {
        Apolice a = findById(id);
        a.setActive(false);
        apoliceRepository.save(a);
        auditoriaService.registrar("Apolice", id, "INATIVAR");
    }

    private void preencherApolice(Apolice a, ApoliceDTO dto) {
        if (dto.getPropostaId() != null) {
            a.setProposta(propostaRepository.findByIdAndActiveTrue(dto.getPropostaId()).orElse(null));
        }
        a.setCliente(clienteRepository.findByIdAndActiveTrue(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado")));
        a.setSeguradora(seguradoraRepository.findByIdAndActiveTrue(dto.getSeguradoraId())
                .orElseThrow(() -> new ResourceNotFoundException("Seguradora não encontrada")));
        if (dto.getCorretoraId() != null) {
            a.setCorretora(corretoraRepository.findByIdAndActiveTrue(dto.getCorretoraId()).orElse(null));
        }
        a.setRamoSeguro(ramoSeguroRepository.findByIdAndActiveTrue(dto.getRamoSeguroId())
                .orElseThrow(() -> new ResourceNotFoundException("Ramo de seguro não encontrado")));
        a.setDataEmissao(dto.getDataEmissao() != null ? LocalDate.parse(dto.getDataEmissao(), FMT_DATE) : LocalDate.now());
        a.setInicioVigencia(LocalDate.parse(dto.getInicioVigencia(), FMT_DATE));
        a.setFimVigencia(LocalDate.parse(dto.getFimVigencia(), FMT_DATE));
        a.setPremioTotal(dto.getPremioTotal());
        a.setPercentualComissao(dto.getPercentualComissao());
        if (dto.getValorComissao() != null) {
            a.setValorComissao(dto.getValorComissao());
        } else if (dto.getPercentualComissao() != null && dto.getPremioTotal() != null) {
            a.setValorComissao(dto.getPremioTotal().multiply(dto.getPercentualComissao()).divide(BigDecimal.valueOf(100)));
        }
        a.setFormaPagamento(dto.getFormaPagamento());
        a.setQuantidadeParcelas(dto.getQuantidadeParcelas());
        a.setObservacoes(dto.getObservacoes());
        a.setResponsavelInterno(dto.getResponsavelInterno());
    }

    private String gerarNumeroApolice() {
        long count = apoliceRepository.count() + 1;
        return String.format("APOL-%d-%04d", Year.now().getValue(), count);
    }

    private Apolice findById(Long id) {
        return apoliceRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Apólice não encontrada"));
    }

    private ApoliceDTO toDTO(Apolice a) {
        ApoliceDTO dto = new ApoliceDTO();
        dto.setId(a.getId());
        dto.setNumeroApolice(a.getNumeroApolice());
        if (a.getProposta() != null) {
            dto.setPropostaId(a.getProposta().getId());
            dto.setPropostaNumero(a.getProposta().getNumeroProposta());
        }
        dto.setClienteId(a.getCliente().getId());
        dto.setClienteNome(a.getCliente().getNomeExibicao());
        dto.setSeguradoraId(a.getSeguradora().getId());
        dto.setSeguradoraNome(a.getSeguradora().getNome());
        if (a.getCorretora() != null) {
            dto.setCorretoraId(a.getCorretora().getId());
            dto.setCorretoraNome(a.getCorretora().getNome());
        }
        dto.setRamoSeguroId(a.getRamoSeguro().getId());
        dto.setRamoSeguroNome(a.getRamoSeguro().getNome());
        dto.setDataEmissao(a.getDataEmissao() != null ? a.getDataEmissao().format(FMT_DATE) : null);
        dto.setInicioVigencia(a.getInicioVigencia().format(FMT_DATE));
        dto.setFimVigencia(a.getFimVigencia().format(FMT_DATE));
        dto.setPremioTotal(a.getPremioTotal());
        dto.setPercentualComissao(a.getPercentualComissao());
        dto.setValorComissao(a.getValorComissao());
        dto.setFormaPagamento(a.getFormaPagamento());
        dto.setQuantidadeParcelas(a.getQuantidadeParcelas());
        dto.setStatus(a.getStatus());
        dto.setObservacoes(a.getObservacoes());
        dto.setResponsavelInterno(a.getResponsavelInterno());
        dto.setActive(a.getActive());
        dto.setCreatedAt(a.getCreatedAt() != null ? a.getCreatedAt().format(FMT_DT) : null);
        return dto;
    }
}
