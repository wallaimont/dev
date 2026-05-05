package com.orionerp.modules.fiscal.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.fiscal.domain.Cfop;
import com.orionerp.modules.fiscal.dto.CfopRequest;
import com.orionerp.modules.fiscal.dto.CfopResponse;
import com.orionerp.modules.fiscal.repository.CfopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CfopService {

    private final CfopRepository cfopRepository;

    @Transactional(readOnly = true)
    public List<CfopResponse> listar(String tipo) {
        List<Cfop> list = (tipo != null && !tipo.isBlank())
                ? cfopRepository.findByTipoAndAtivoTrue(tipo)
                : cfopRepository.findByAtivoTrue();
        return list.stream().map(CfopResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public CfopResponse buscarPorId(Long id) {
        Cfop entity = cfopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CFOP não encontrado: " + id));
        return CfopResponse.from(entity);
    }

    @Transactional
    public CfopResponse criar(CfopRequest request) {
        cfopRepository.findByCodigoAndAtivoTrue(request.codigo()).ifPresent(existing -> {
            throw new BusinessException("Já existe CFOP com código '" + request.codigo() + "'");
        });

        Cfop entity = new Cfop();
        entity.setCodigo(request.codigo());
        entity.setDescricao(request.descricao());
        entity.setTipo(request.tipo());
        entity.setAtivo(request.ativo() != null ? request.ativo() : true);

        return CfopResponse.from(cfopRepository.save(entity));
    }

    @Transactional
    public CfopResponse atualizar(Long id, CfopRequest request) {
        Cfop entity = cfopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CFOP não encontrado: " + id));

        if (!entity.getCodigo().equals(request.codigo())) {
            cfopRepository.findByCodigoAndAtivoTrue(request.codigo()).ifPresent(dup -> {
                throw new BusinessException("Já existe CFOP com código '" + request.codigo() + "'");
            });
            entity.setCodigo(request.codigo());
        }

        entity.setDescricao(request.descricao());
        entity.setTipo(request.tipo());
        if (request.ativo() != null) entity.setAtivo(request.ativo());

        return CfopResponse.from(cfopRepository.save(entity));
    }

    @Transactional
    public void alterarStatus(Long id) {
        Cfop entity = cfopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CFOP não encontrado: " + id));
        entity.setAtivo(!entity.getAtivo());
        cfopRepository.save(entity);
    }
}
