package com.orionerp.modules.cadastros.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.cadastros.domain.Transportadora;
import com.orionerp.modules.cadastros.dto.TransportadoraRequest;
import com.orionerp.modules.cadastros.dto.TransportadoraResponse;
import com.orionerp.modules.cadastros.repository.CadastrosSpecifications;
import com.orionerp.modules.cadastros.repository.TransportadoraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransportadoraService {

    private final TransportadoraRepository transportadoraRepository;

    @Transactional(readOnly = true)
    public Page<TransportadoraResponse> list(Long empresaId, String term, Pageable pageable) {
        return transportadoraRepository.findAll(
                CadastrosSpecifications.transportadoraFilter(empresaId, term), pageable
        ).map(TransportadoraResponse::from);
    }

    @Transactional(readOnly = true)
    public TransportadoraResponse getById(Long id) {
        Transportadora entity = transportadoraRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transportadora não encontrada: " + id));
        return TransportadoraResponse.from(entity);
    }

    @Transactional
    public TransportadoraResponse create(TransportadoraRequest request) {
        if (transportadoraRepository.existsByEmpresaIdAndCodigoAndDeletedFalse(request.empresaId(), request.codigo())) {
            throw new BusinessException("Já existe uma transportadora com o código: " + request.codigo());
        }

        Transportadora entity = new Transportadora();
        entity.setEmpresaId(request.empresaId());
        entity.setCodigo(request.codigo());
        entity.setRazaoSocial(request.razaoSocial());
        entity.setNomeFantasia(request.nomeFantasia());
        entity.setCpfCnpj(request.cpfCnpj());
        entity.setEndereco(request.endereco());
        entity.setCidade(request.cidade());
        entity.setUf(request.uf());
        entity.setTelefone(request.telefone());
        entity.setEmail(request.email());

        entity = transportadoraRepository.save(entity);
        return TransportadoraResponse.from(entity);
    }

    @Transactional
    public TransportadoraResponse update(Long id, TransportadoraRequest request) {
        Transportadora entity = transportadoraRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transportadora não encontrada: " + id));

        entity.setCodigo(request.codigo());
        entity.setRazaoSocial(request.razaoSocial());
        entity.setNomeFantasia(request.nomeFantasia());
        entity.setCpfCnpj(request.cpfCnpj());
        entity.setEndereco(request.endereco());
        entity.setCidade(request.cidade());
        entity.setUf(request.uf());
        entity.setTelefone(request.telefone());
        entity.setEmail(request.email());

        entity = transportadoraRepository.save(entity);
        return TransportadoraResponse.from(entity);
    }

    @Transactional
    public void delete(Long id) {
        Transportadora entity = transportadoraRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transportadora não encontrada: " + id));
        entity.softDelete();
        transportadoraRepository.save(entity);
    }
}
