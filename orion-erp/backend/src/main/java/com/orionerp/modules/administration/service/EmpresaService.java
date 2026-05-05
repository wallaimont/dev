package com.orionerp.modules.administration.service;

import com.orionerp.common.PageResponse;
import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.administration.domain.Empresa;
import com.orionerp.modules.administration.dto.EmpresaRequest;
import com.orionerp.modules.administration.dto.EmpresaResponse;
import com.orionerp.modules.administration.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    @Transactional(readOnly = true)
    public PageResponse<EmpresaResponse> list(String term, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "razaoSocial"));
        var result = empresaRepository.findAll(AdministrationSpecifications.empresaFilter(term), pageable)
                .map(this::toResponse);
        return PageResponse.from(result);
    }

    @Transactional(readOnly = true)
    public EmpresaResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public EmpresaResponse create(EmpresaRequest request) {
        validateUnique(null, request.codigo(), request.cnpj());

        Empresa empresa = new Empresa();
        apply(empresa, request);
        return toResponse(empresaRepository.save(empresa));
    }

    @Transactional
    public EmpresaResponse update(Long id, EmpresaRequest request) {
        Empresa empresa = findById(id);
        validateUnique(id, request.codigo(), request.cnpj());
        apply(empresa, request);
        return toResponse(empresaRepository.save(empresa));
    }

    @Transactional
    public void delete(Long id) {
        Empresa empresa = findById(id);
        empresa.softDelete();
        empresaRepository.save(empresa);
    }

    private void validateUnique(Long id, String codigo, String cnpj) {
        boolean codigoExists = id == null
                ? empresaRepository.existsByCodigoIgnoreCaseAndDeletedFalse(codigo)
                : empresaRepository.existsByCodigoIgnoreCaseAndDeletedFalseAndIdNot(codigo, id);
        if (codigoExists) {
            throw new BusinessException("Ja existe empresa com o codigo informado");
        }

        boolean cnpjExists = id == null
                ? empresaRepository.existsByCnpjAndDeletedFalse(cnpj)
                : empresaRepository.existsByCnpjAndDeletedFalseAndIdNot(cnpj, id);
        if (cnpjExists) {
            throw new BusinessException("Ja existe empresa com o CNPJ informado");
        }
    }

    private Empresa findById(Long id) {
        return empresaRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa nao encontrada"));
    }

    private void apply(Empresa empresa, EmpresaRequest request) {
        empresa.setCodigo(request.codigo().trim());
        empresa.setRazaoSocial(request.razaoSocial().trim());
        empresa.setNomeFantasia(request.nomeFantasia());
        empresa.setCnpj(request.cnpj().trim());
        empresa.setEmail(request.email() != null ? request.email().trim().toLowerCase() : null);
        empresa.setCidade(request.cidade());
        empresa.setUf(request.uf());
        empresa.setObservacao(request.observacao());
        if (request.ativo() != null) {
            empresa.setAtivo(request.ativo());
        }
    }

    private EmpresaResponse toResponse(Empresa empresa) {
        return new EmpresaResponse(
                empresa.getId(),
                empresa.getUuid(),
                empresa.getCodigo(),
                empresa.getRazaoSocial(),
                empresa.getNomeFantasia(),
                empresa.getCnpj(),
                empresa.getEmail(),
                empresa.getCidade(),
                empresa.getUf(),
                empresa.getAtivo(),
                empresa.getObservacao()
        );
    }
}
