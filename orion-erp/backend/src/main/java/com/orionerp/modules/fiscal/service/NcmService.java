package com.orionerp.modules.fiscal.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.fiscal.domain.Ncm;
import com.orionerp.modules.fiscal.dto.NcmRequest;
import com.orionerp.modules.fiscal.dto.NcmResponse;
import com.orionerp.modules.fiscal.repository.NcmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NcmService {

    private final NcmRepository ncmRepository;

    @Transactional(readOnly = true)
    public List<NcmResponse> listar() {
        return ncmRepository.findByAtivoTrue().stream().map(NcmResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public NcmResponse buscarPorId(Long id) {
        Ncm entity = ncmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("NCM não encontrado: " + id));
        return NcmResponse.from(entity);
    }

    @Transactional
    public NcmResponse criar(NcmRequest request) {
        ncmRepository.findByCodigoAndAtivoTrue(request.codigo()).ifPresent(existing -> {
            throw new BusinessException("Já existe NCM com código '" + request.codigo() + "'");
        });

        Ncm entity = new Ncm();
        entity.setCodigo(request.codigo());
        entity.setDescricao(request.descricao());
        entity.setAliquotaIpi(request.aliquotaIpi());
        entity.setAtivo(request.ativo() != null ? request.ativo() : true);

        return NcmResponse.from(ncmRepository.save(entity));
    }

    @Transactional
    public NcmResponse atualizar(Long id, NcmRequest request) {
        Ncm entity = ncmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("NCM não encontrado: " + id));

        if (!entity.getCodigo().equals(request.codigo())) {
            ncmRepository.findByCodigoAndAtivoTrue(request.codigo()).ifPresent(dup -> {
                throw new BusinessException("Já existe NCM com código '" + request.codigo() + "'");
            });
            entity.setCodigo(request.codigo());
        }

        entity.setDescricao(request.descricao());
        entity.setAliquotaIpi(request.aliquotaIpi());
        if (request.ativo() != null) entity.setAtivo(request.ativo());

        return NcmResponse.from(ncmRepository.save(entity));
    }

    @Transactional
    public void alterarStatus(Long id) {
        Ncm entity = ncmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("NCM não encontrado: " + id));
        entity.setAtivo(!entity.getAtivo());
        ncmRepository.save(entity);
    }
}
