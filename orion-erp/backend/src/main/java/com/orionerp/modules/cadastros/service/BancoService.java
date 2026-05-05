package com.orionerp.modules.cadastros.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.cadastros.domain.Banco;
import com.orionerp.modules.cadastros.dto.BancoRequest;
import com.orionerp.modules.cadastros.dto.BancoResponse;
import com.orionerp.modules.cadastros.repository.BancoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BancoService {

    private final BancoRepository bancoRepository;

    @Transactional(readOnly = true)
    public Page<BancoResponse> list(Pageable pageable) {
        return bancoRepository.findAll(pageable).map(BancoResponse::from);
    }

    @Transactional(readOnly = true)
    public BancoResponse getById(Long id) {
        Banco entity = bancoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banco não encontrado: " + id));
        return BancoResponse.from(entity);
    }

    @Transactional
    public BancoResponse create(BancoRequest request) {
        if (bancoRepository.existsByCodigoBanco(request.codigoBanco())) {
            throw new BusinessException("Já existe um banco com o código: " + request.codigoBanco());
        }

        Banco entity = new Banco();
        entity.setCodigoBanco(request.codigoBanco());
        entity.setNome(request.nome());

        entity = bancoRepository.save(entity);
        return BancoResponse.from(entity);
    }

    @Transactional
    public BancoResponse update(Long id, BancoRequest request) {
        Banco entity = bancoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banco não encontrado: " + id));

        entity.setNome(request.nome());
        entity.setUpdatedAt(LocalDateTime.now());

        entity = bancoRepository.save(entity);
        return BancoResponse.from(entity);
    }

    @Transactional
    public void delete(Long id) {
        Banco entity = bancoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banco não encontrado: " + id));
        entity.setAtivo(false);
        entity.setUpdatedAt(LocalDateTime.now());
        bancoRepository.save(entity);
    }
}
