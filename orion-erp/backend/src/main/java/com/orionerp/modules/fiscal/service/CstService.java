package com.orionerp.modules.fiscal.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.fiscal.domain.Cst;
import com.orionerp.modules.fiscal.dto.CstRequest;
import com.orionerp.modules.fiscal.dto.CstResponse;
import com.orionerp.modules.fiscal.repository.CstRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CstService {

    private final CstRepository cstRepository;

    @Transactional(readOnly = true)
    public List<CstResponse> listar(String tipoImposto) {
        List<Cst> list = (tipoImposto != null && !tipoImposto.isBlank())
                ? cstRepository.findByTipoImpostoAndAtivoTrue(tipoImposto)
                : cstRepository.findByAtivoTrue();
        return list.stream().map(CstResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public CstResponse buscarPorId(Long id) {
        Cst entity = cstRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CST não encontrado: " + id));
        return CstResponse.from(entity);
    }

    @Transactional
    public CstResponse criar(CstRequest request) {
        cstRepository.findByCodigoAndAtivoTrue(request.codigo()).ifPresent(existing -> {
            throw new BusinessException("Já existe CST com código '" + request.codigo() + "'");
        });

        Cst entity = new Cst();
        entity.setCodigo(request.codigo());
        entity.setDescricao(request.descricao());
        entity.setTipoImposto(request.tipoImposto());
        entity.setAtivo(request.ativo() != null ? request.ativo() : true);

        return CstResponse.from(cstRepository.save(entity));
    }

    @Transactional
    public CstResponse atualizar(Long id, CstRequest request) {
        Cst entity = cstRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CST não encontrado: " + id));

        if (!entity.getCodigo().equals(request.codigo())) {
            cstRepository.findByCodigoAndAtivoTrue(request.codigo()).ifPresent(dup -> {
                throw new BusinessException("Já existe CST com código '" + request.codigo() + "'");
            });
            entity.setCodigo(request.codigo());
        }

        entity.setDescricao(request.descricao());
        entity.setTipoImposto(request.tipoImposto());
        if (request.ativo() != null) entity.setAtivo(request.ativo());

        return CstResponse.from(cstRepository.save(entity));
    }

    @Transactional
    public void alterarStatus(Long id) {
        Cst entity = cstRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CST não encontrado: " + id));
        entity.setAtivo(!entity.getAtivo());
        cstRepository.save(entity);
    }
}
