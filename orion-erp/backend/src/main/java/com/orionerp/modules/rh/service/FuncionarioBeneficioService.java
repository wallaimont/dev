package com.orionerp.modules.rh.service;

import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.rh.domain.FuncionarioBeneficio;
import com.orionerp.modules.rh.dto.FuncionarioBeneficioRequest;
import com.orionerp.modules.rh.dto.FuncionarioBeneficioResponse;
import com.orionerp.modules.rh.repository.FuncionarioBeneficioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FuncionarioBeneficioService {

    private final FuncionarioBeneficioRepository repository;

    @Transactional(readOnly = true)
    public List<FuncionarioBeneficioResponse> listarPorFuncionario(Long funcionarioId) {
        return repository.findByFuncionarioIdAndDeletedFalse(funcionarioId).stream()
                .map(FuncionarioBeneficioResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public FuncionarioBeneficioResponse buscarPorId(Long id) {
        return FuncionarioBeneficioResponse.from(findOrFail(id));
    }

    @Transactional
    public FuncionarioBeneficioResponse criar(FuncionarioBeneficioRequest request) {
        FuncionarioBeneficio entity = new FuncionarioBeneficio();
        mapFields(entity, request);
        return FuncionarioBeneficioResponse.from(repository.save(entity));
    }

    @Transactional
    public FuncionarioBeneficioResponse atualizar(Long id, FuncionarioBeneficioRequest request) {
        FuncionarioBeneficio entity = findOrFail(id);
        mapFields(entity, request);
        return FuncionarioBeneficioResponse.from(repository.save(entity));
    }

    @Transactional
    public void excluir(Long id) {
        FuncionarioBeneficio entity = findOrFail(id);
        entity.softDelete();
        repository.save(entity);
    }

    private FuncionarioBeneficio findOrFail(Long id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vínculo funcionário-benefício não encontrado"));
    }

    private void mapFields(FuncionarioBeneficio entity, FuncionarioBeneficioRequest r) {
        entity.setEmpresaId(r.empresaId());
        entity.setFuncionarioId(r.funcionarioId());
        entity.setBeneficioId(r.beneficioId());
        entity.setDataInicio(r.dataInicio());
        entity.setDataFim(r.dataFim());
        entity.setValorCustomizado(r.valorCustomizado());
    }
}
