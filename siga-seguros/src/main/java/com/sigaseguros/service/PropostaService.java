package com.sigaseguros.service;

import com.sigaseguros.dto.PropostaDTO;
import com.sigaseguros.entity.*;
import com.sigaseguros.enums.StatusProposta;
import com.sigaseguros.exception.BusinessException;
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
public class PropostaService {

    private final PropostaRepository propostaRepository;
    private final ClienteRepository clienteRepository;
    private final SeguradoraRepository seguradoraRepository;
    private final CorretoraRepository corretoraRepository;
    private final RamoSeguroRepository ramoSeguroRepository;
    private final AuditoriaService auditoriaService;
    private static final DateTimeFormatter FMT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_DT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Transactional(readOnly = true)
    public Page<PropostaDTO> listar(Long clienteId, Long seguradoraId, StatusProposta status,
                                     LocalDate dataInicio, LocalDate dataFim, Pageable pageable) {
        return propostaRepository.findAllWithFilters(clienteId, seguradoraId, status, dataInicio, dataFim, pageable)
                .map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public PropostaDTO buscarPorId(Long id) {
        return toDTO(findById(id));
    }

    @Transactional(readOnly = true)
    public List<PropostaDTO> ultimasPropostas() {
        return propostaRepository.findTop10ByActiveTrueOrderByCreatedAtDesc().stream().map(this::toDTO).toList();
    }

    @Transactional
    public PropostaDTO criar(PropostaDTO dto) {
        Proposta p = new Proposta();
        p.setNumeroProposta(gerarNumeroProposta());
        p.setCliente(clienteRepository.findByIdAndActiveTrue(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado")));
        p.setSeguradora(seguradoraRepository.findByIdAndActiveTrue(dto.getSeguradoraId())
                .orElseThrow(() -> new ResourceNotFoundException("Seguradora não encontrada")));
        if (dto.getCorretoraId() != null) {
            p.setCorretora(corretoraRepository.findByIdAndActiveTrue(dto.getCorretoraId())
                    .orElseThrow(() -> new ResourceNotFoundException("Corretora não encontrada")));
        }
        p.setRamoSeguro(ramoSeguroRepository.findByIdAndActiveTrue(dto.getRamoSeguroId())
                .orElseThrow(() -> new ResourceNotFoundException("Ramo de seguro não encontrado")));

        p.setVigenciaInicial(dto.getVigenciaInicial() != null ? LocalDate.parse(dto.getVigenciaInicial(), FMT_DATE) : null);
        p.setVigenciaFinal(dto.getVigenciaFinal() != null ? LocalDate.parse(dto.getVigenciaFinal(), FMT_DATE) : null);
        p.setPremioLiquido(dto.getPremioLiquido());
        p.setPremioTotal(dto.getPremioTotal());
        p.setPercentualComissao(dto.getPercentualComissao());
        calcularComissao(p, dto);
        p.setStatus(StatusProposta.EM_ANALISE);
        p.setObservacoes(dto.getObservacoes());
        p.setResponsavelInterno(dto.getResponsavelInterno());
        p.setActive(true);

        p = propostaRepository.save(p);
        auditoriaService.registrar("Proposta", p.getId(), "CRIAR");
        return toDTO(p);
    }

    @Transactional
    public PropostaDTO atualizar(Long id, PropostaDTO dto) {
        Proposta p = findById(id);
        p.setCliente(clienteRepository.findByIdAndActiveTrue(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado")));
        p.setSeguradora(seguradoraRepository.findByIdAndActiveTrue(dto.getSeguradoraId())
                .orElseThrow(() -> new ResourceNotFoundException("Seguradora não encontrada")));
        if (dto.getCorretoraId() != null) {
            p.setCorretora(corretoraRepository.findByIdAndActiveTrue(dto.getCorretoraId())
                    .orElseThrow(() -> new ResourceNotFoundException("Corretora não encontrada")));
        } else {
            p.setCorretora(null);
        }
        p.setRamoSeguro(ramoSeguroRepository.findByIdAndActiveTrue(dto.getRamoSeguroId())
                .orElseThrow(() -> new ResourceNotFoundException("Ramo de seguro não encontrado")));

        p.setVigenciaInicial(dto.getVigenciaInicial() != null ? LocalDate.parse(dto.getVigenciaInicial(), FMT_DATE) : null);
        p.setVigenciaFinal(dto.getVigenciaFinal() != null ? LocalDate.parse(dto.getVigenciaFinal(), FMT_DATE) : null);
        p.setPremioLiquido(dto.getPremioLiquido());
        p.setPremioTotal(dto.getPremioTotal());
        p.setPercentualComissao(dto.getPercentualComissao());
        calcularComissao(p, dto);
        if (dto.getStatus() != null) p.setStatus(dto.getStatus());
        p.setObservacoes(dto.getObservacoes());
        p.setResponsavelInterno(dto.getResponsavelInterno());

        p = propostaRepository.save(p);
        auditoriaService.registrar("Proposta", p.getId(), "ATUALIZAR");
        return toDTO(p);
    }

    @Transactional
    public PropostaDTO alterarStatus(Long id, StatusProposta novoStatus) {
        Proposta p = findById(id);
        p.setStatus(novoStatus);
        p = propostaRepository.save(p);
        auditoriaService.registrar("Proposta", p.getId(), "ALTERAR_STATUS", null, novoStatus.name());
        return toDTO(p);
    }

    @Transactional
    public void inativar(Long id) {
        Proposta p = findById(id);
        p.setActive(false);
        propostaRepository.save(p);
        auditoriaService.registrar("Proposta", id, "INATIVAR");
    }

    private void calcularComissao(Proposta p, PropostaDTO dto) {
        if (dto.getValorComissao() != null) {
            p.setValorComissao(dto.getValorComissao());
        } else if (dto.getPercentualComissao() != null && dto.getPremioTotal() != null) {
            p.setValorComissao(dto.getPremioTotal().multiply(dto.getPercentualComissao()).divide(BigDecimal.valueOf(100)));
        }
    }

    private String gerarNumeroProposta() {
        long count = propostaRepository.count() + 1;
        return String.format("PROP-%d-%04d", Year.now().getValue(), count);
    }

    private Proposta findById(Long id) {
        return propostaRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proposta não encontrada"));
    }

    private PropostaDTO toDTO(Proposta p) {
        PropostaDTO dto = new PropostaDTO();
        dto.setId(p.getId());
        dto.setNumeroProposta(p.getNumeroProposta());
        dto.setClienteId(p.getCliente().getId());
        dto.setClienteNome(p.getCliente().getNomeExibicao());
        dto.setSeguradoraId(p.getSeguradora().getId());
        dto.setSeguradoraNome(p.getSeguradora().getNome());
        if (p.getCorretora() != null) {
            dto.setCorretoraId(p.getCorretora().getId());
            dto.setCorretoraNome(p.getCorretora().getNome());
        }
        dto.setRamoSeguroId(p.getRamoSeguro().getId());
        dto.setRamoSeguroNome(p.getRamoSeguro().getNome());
        dto.setVigenciaInicial(p.getVigenciaInicial() != null ? p.getVigenciaInicial().format(FMT_DATE) : null);
        dto.setVigenciaFinal(p.getVigenciaFinal() != null ? p.getVigenciaFinal().format(FMT_DATE) : null);
        dto.setPremioLiquido(p.getPremioLiquido());
        dto.setPremioTotal(p.getPremioTotal());
        dto.setPercentualComissao(p.getPercentualComissao());
        dto.setValorComissao(p.getValorComissao());
        dto.setStatus(p.getStatus());
        dto.setObservacoes(p.getObservacoes());
        dto.setResponsavelInterno(p.getResponsavelInterno());
        dto.setActive(p.getActive());
        dto.setCreatedAt(p.getCreatedAt() != null ? p.getCreatedAt().format(FMT_DT) : null);
        return dto;
    }
}
