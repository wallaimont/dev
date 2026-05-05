package com.orionerp.modules.estoque.service;

import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.estoque.domain.Armazem;
import com.orionerp.modules.estoque.domain.Localizacao;
import com.orionerp.modules.estoque.dto.LocalizacaoRequest;
import com.orionerp.modules.estoque.dto.LocalizacaoResponse;
import com.orionerp.modules.estoque.repository.ArmazemRepository;
import com.orionerp.modules.estoque.repository.LocalizacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LocalizacaoService {

    private final LocalizacaoRepository localizacaoRepository;
    private final ArmazemRepository armazemRepository;

    @Transactional(readOnly = true)
    public List<LocalizacaoResponse> listarPorArmazem(Long armazemId) {
        return localizacaoRepository.findByArmazemIdAndAtivoTrue(armazemId)
                .stream().map(LocalizacaoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public LocalizacaoResponse buscarPorId(Long id) {
        Localizacao entity = localizacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Localização não encontrada: " + id));
        return LocalizacaoResponse.from(entity);
    }

    @Transactional
    public LocalizacaoResponse criar(LocalizacaoRequest request) {
        Armazem armazem = armazemRepository.findByIdAndDeletedFalse(request.armazemId())
                .orElseThrow(() -> new ResourceNotFoundException("Armazém não encontrado: " + request.armazemId()));

        Localizacao entity = new Localizacao();
        entity.setArmazem(armazem);
        entity.setCodigo(request.codigo());
        entity.setDescricao(request.descricao());
        entity.setAtivo(request.ativo() != null ? request.ativo() : true);

        return LocalizacaoResponse.from(localizacaoRepository.save(entity));
    }

    @Transactional
    public LocalizacaoResponse atualizar(Long id, LocalizacaoRequest request) {
        Localizacao entity = localizacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Localização não encontrada: " + id));

        if (!entity.getArmazem().getId().equals(request.armazemId())) {
            Armazem armazem = armazemRepository.findByIdAndDeletedFalse(request.armazemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Armazém não encontrado: " + request.armazemId()));
            entity.setArmazem(armazem);
        }

        entity.setCodigo(request.codigo());
        entity.setDescricao(request.descricao());
        if (request.ativo() != null) entity.setAtivo(request.ativo());
        entity.setUpdatedAt(LocalDateTime.now());

        return LocalizacaoResponse.from(localizacaoRepository.save(entity));
    }

    @Transactional
    public void alterarStatus(Long id) {
        Localizacao entity = localizacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Localização não encontrada: " + id));
        entity.setAtivo(!entity.getAtivo());
        entity.setUpdatedAt(LocalDateTime.now());
        localizacaoRepository.save(entity);
    }
}
