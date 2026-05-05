package com.orionerp.modules.rh.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.rh.domain.Cargo;
import com.orionerp.modules.rh.dto.CargoRequest;
import com.orionerp.modules.rh.dto.CargoResponse;
import com.orionerp.modules.rh.repository.CargoRepository;
import com.orionerp.modules.rh.repository.RhSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CargoService {

    private final CargoRepository repository;

    @Transactional(readOnly = true)
    public Page<CargoResponse> listar(Long empresaId, String term, Pageable pageable) {
        return repository.findAll(RhSpecifications.cargoFilter(empresaId, term), pageable)
                .map(CargoResponse::from);
    }

    @Transactional(readOnly = true)
    public CargoResponse buscarPorId(Long id) {
        return CargoResponse.from(findOrFail(id));
    }

    @Transactional
    public CargoResponse criar(CargoRequest request) {
        if (repository.existsByEmpresaIdAndCodigoAndDeletedFalse(request.empresaId(), request.codigo())) {
            throw new BusinessException("Já existe um cargo com o código " + request.codigo());
        }
        Cargo entity = new Cargo();
        mapFields(entity, request);
        return CargoResponse.from(repository.save(entity));
    }

    @Transactional
    public CargoResponse atualizar(Long id, CargoRequest request) {
        Cargo entity = findOrFail(id);
        mapFields(entity, request);
        return CargoResponse.from(repository.save(entity));
    }

    @Transactional
    public void excluir(Long id) {
        Cargo entity = findOrFail(id);
        entity.softDelete();
        repository.save(entity);
    }

    private Cargo findOrFail(Long id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cargo não encontrado"));
    }

    private void mapFields(Cargo entity, CargoRequest r) {
        entity.setEmpresaId(r.empresaId());
        entity.setCodigo(r.codigo());
        entity.setNome(r.nome());
        entity.setCbo(r.cbo());
        entity.setSalarioBase(r.salarioBase());
        entity.setNivel(r.nivel());
    }
}
