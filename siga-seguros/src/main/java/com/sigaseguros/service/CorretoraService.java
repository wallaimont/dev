package com.sigaseguros.service;

import com.sigaseguros.dto.CorretoraDTO;
import com.sigaseguros.entity.Corretora;
import com.sigaseguros.exception.BusinessException;
import com.sigaseguros.exception.ResourceNotFoundException;
import com.sigaseguros.repository.CorretoraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CorretoraService {

    private final CorretoraRepository corretoraRepository;
    private final AuditoriaService auditoriaService;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Transactional(readOnly = true)
    public Page<CorretoraDTO> listar(String nome, Pageable pageable) {
        return corretoraRepository.findAllWithFilters(nome, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public List<CorretoraDTO> listarTodas() {
        return corretoraRepository.findAllByActiveTrue().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public CorretoraDTO buscarPorId(Long id) {
        return toDTO(findById(id));
    }

    @Transactional
    public CorretoraDTO criar(CorretoraDTO dto) {
        if (corretoraRepository.existsByCnpjAndActiveTrue(dto.getCnpj())) {
            throw new BusinessException("CNPJ já cadastrado");
        }
        Corretora c = toEntity(dto, new Corretora());
        c.setActive(true);
        c = corretoraRepository.save(c);
        auditoriaService.registrar("Corretora", c.getId(), "CRIAR");
        return toDTO(c);
    }

    @Transactional
    public CorretoraDTO atualizar(Long id, CorretoraDTO dto) {
        Corretora c = findById(id);
        if (!c.getCnpj().equals(dto.getCnpj()) && corretoraRepository.existsByCnpjAndIdNotAndActiveTrue(dto.getCnpj(), id)) {
            throw new BusinessException("CNPJ já cadastrado");
        }
        c = toEntity(dto, c);
        c = corretoraRepository.save(c);
        auditoriaService.registrar("Corretora", c.getId(), "ATUALIZAR");
        return toDTO(c);
    }

    @Transactional
    public void inativar(Long id) {
        Corretora c = findById(id);
        c.setActive(false);
        corretoraRepository.save(c);
        auditoriaService.registrar("Corretora", id, "INATIVAR");
    }

    private Corretora findById(Long id) {
        return corretoraRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Corretora não encontrada"));
    }

    private CorretoraDTO toDTO(Corretora c) {
        CorretoraDTO dto = new CorretoraDTO();
        dto.setId(c.getId());
        dto.setNome(c.getNome());
        dto.setCnpj(c.getCnpj());
        dto.setResponsavel(c.getResponsavel());
        dto.setTelefone(c.getTelefone());
        dto.setEmail(c.getEmail());
        dto.setPercentualComissao(c.getPercentualComissao());
        dto.setObservacoes(c.getObservacoes());
        dto.setActive(c.getActive());
        dto.setCreatedAt(c.getCreatedAt() != null ? c.getCreatedAt().format(FMT) : null);
        return dto;
    }

    private Corretora toEntity(CorretoraDTO dto, Corretora c) {
        c.setNome(dto.getNome());
        c.setCnpj(dto.getCnpj());
        c.setResponsavel(dto.getResponsavel());
        c.setTelefone(dto.getTelefone());
        c.setEmail(dto.getEmail());
        c.setPercentualComissao(dto.getPercentualComissao());
        c.setObservacoes(dto.getObservacoes());
        return c;
    }
}
