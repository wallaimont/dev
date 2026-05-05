package com.sigaseguros.service;

import com.sigaseguros.dto.SeguradoraDTO;
import com.sigaseguros.entity.Seguradora;
import com.sigaseguros.exception.BusinessException;
import com.sigaseguros.exception.ResourceNotFoundException;
import com.sigaseguros.repository.SeguradoraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeguradoraService {

    private final SeguradoraRepository seguradoraRepository;
    private final AuditoriaService auditoriaService;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Transactional(readOnly = true)
    public Page<SeguradoraDTO> listar(String nome, Pageable pageable) {
        return seguradoraRepository.findAllWithFilters(nome, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public List<SeguradoraDTO> listarTodas() {
        return seguradoraRepository.findAllByActiveTrue().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public SeguradoraDTO buscarPorId(Long id) {
        return toDTO(findById(id));
    }

    @Transactional
    public SeguradoraDTO criar(SeguradoraDTO dto) {
        if (seguradoraRepository.existsByCnpjAndActiveTrue(dto.getCnpj())) {
            throw new BusinessException("CNPJ já cadastrado");
        }
        Seguradora s = toEntity(dto, new Seguradora());
        s.setActive(true);
        s = seguradoraRepository.save(s);
        auditoriaService.registrar("Seguradora", s.getId(), "CRIAR");
        return toDTO(s);
    }

    @Transactional
    public SeguradoraDTO atualizar(Long id, SeguradoraDTO dto) {
        Seguradora s = findById(id);
        if (!s.getCnpj().equals(dto.getCnpj()) && seguradoraRepository.existsByCnpjAndIdNotAndActiveTrue(dto.getCnpj(), id)) {
            throw new BusinessException("CNPJ já cadastrado");
        }
        s = toEntity(dto, s);
        s = seguradoraRepository.save(s);
        auditoriaService.registrar("Seguradora", s.getId(), "ATUALIZAR");
        return toDTO(s);
    }

    @Transactional
    public void inativar(Long id) {
        Seguradora s = findById(id);
        s.setActive(false);
        seguradoraRepository.save(s);
        auditoriaService.registrar("Seguradora", id, "INATIVAR");
    }

    private Seguradora findById(Long id) {
        return seguradoraRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seguradora não encontrada"));
    }

    private SeguradoraDTO toDTO(Seguradora s) {
        SeguradoraDTO dto = new SeguradoraDTO();
        dto.setId(s.getId());
        dto.setNome(s.getNome());
        dto.setCnpj(s.getCnpj());
        dto.setCodigoInterno(s.getCodigoInterno());
        dto.setTelefone(s.getTelefone());
        dto.setEmail(s.getEmail());
        dto.setContatoComercial(s.getContatoComercial());
        dto.setPercentualComissaoPadrao(s.getPercentualComissaoPadrao());
        dto.setActive(s.getActive());
        dto.setCreatedAt(s.getCreatedAt() != null ? s.getCreatedAt().format(FMT) : null);
        return dto;
    }

    private Seguradora toEntity(SeguradoraDTO dto, Seguradora s) {
        s.setNome(dto.getNome());
        s.setCnpj(dto.getCnpj());
        s.setCodigoInterno(dto.getCodigoInterno());
        s.setTelefone(dto.getTelefone());
        s.setEmail(dto.getEmail());
        s.setContatoComercial(dto.getContatoComercial());
        s.setPercentualComissaoPadrao(dto.getPercentualComissaoPadrao());
        return s;
    }
}
