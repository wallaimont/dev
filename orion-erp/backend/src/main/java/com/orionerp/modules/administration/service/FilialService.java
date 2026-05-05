package com.orionerp.modules.administration.service;

import com.orionerp.common.PageResponse;
import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.administration.domain.Empresa;
import com.orionerp.modules.administration.domain.Filial;
import com.orionerp.modules.administration.dto.FilialRequest;
import com.orionerp.modules.administration.dto.FilialResponse;
import com.orionerp.modules.administration.repository.EmpresaRepository;
import com.orionerp.modules.administration.repository.FilialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FilialService {

    private final FilialRepository filialRepository;
    private final EmpresaRepository empresaRepository;

    @Transactional(readOnly = true)
    public PageResponse<FilialResponse> list(Long empresaId, String term, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "razaoSocial"));
        var result = filialRepository.findAll(AdministrationSpecifications.filialFilter(empresaId, term), pageable)
                .map(this::toResponse);
        return PageResponse.from(result);
    }

    @Transactional(readOnly = true)
    public FilialResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public FilialResponse create(FilialRequest request) {
        Empresa empresa = findEmpresa(request.empresaId());
        validateUnique(null, request.empresaId(), request.codigo(), request.cnpj());

        Filial filial = new Filial();
        filial.setEmpresa(empresa);
        apply(filial, request);
        return toResponse(filialRepository.save(filial));
    }

    @Transactional
    public FilialResponse update(Long id, FilialRequest request) {
        Filial filial = findById(id);
        Empresa empresa = findEmpresa(request.empresaId());
        validateUnique(id, request.empresaId(), request.codigo(), request.cnpj());

        filial.setEmpresa(empresa);
        apply(filial, request);
        return toResponse(filialRepository.save(filial));
    }

    @Transactional
    public void delete(Long id) {
        Filial filial = findById(id);
        filial.softDelete();
        filialRepository.save(filial);
    }

    private void validateUnique(Long id, Long empresaId, String codigo, String cnpj) {
        boolean codigoExists = id == null
                ? filialRepository.existsByEmpresa_IdAndCodigoIgnoreCaseAndDeletedFalse(empresaId, codigo)
                : filialRepository.existsByEmpresa_IdAndCodigoIgnoreCaseAndDeletedFalseAndIdNot(empresaId, codigo, id);

        if (codigoExists) {
            throw new BusinessException("Ja existe filial com o codigo informado para esta empresa");
        }

        boolean cnpjExists = id == null
                ? filialRepository.existsByCnpjAndDeletedFalse(cnpj)
                : filialRepository.existsByCnpjAndDeletedFalseAndIdNot(cnpj, id);

        if (cnpjExists) {
            throw new BusinessException("Ja existe filial com o CNPJ informado");
        }
    }

    private Empresa findEmpresa(Long empresaId) {
        return empresaRepository.findByIdAndDeletedFalse(empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa nao encontrada"));
    }

    private Filial findById(Long id) {
        return filialRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Filial nao encontrada"));
    }

    private void apply(Filial filial, FilialRequest request) {
        filial.setCodigo(request.codigo().trim());
        filial.setRazaoSocial(request.razaoSocial().trim());
        filial.setNomeFantasia(request.nomeFantasia());
        filial.setCnpj(request.cnpj().trim());
        filial.setEmail(request.email() != null ? request.email().trim().toLowerCase() : null);
        filial.setCidade(request.cidade());
        filial.setUf(request.uf());
        filial.setMatriz(Boolean.TRUE.equals(request.matriz()));
        filial.setObservacao(request.observacao());
        if (request.ativo() != null) {
            filial.setAtivo(request.ativo());
        }
    }

    private FilialResponse toResponse(Filial filial) {
        return new FilialResponse(
                filial.getId(),
                filial.getUuid(),
                filial.getEmpresa().getId(),
                filial.getEmpresa().getRazaoSocial(),
                filial.getCodigo(),
                filial.getRazaoSocial(),
                filial.getNomeFantasia(),
                filial.getCnpj(),
                filial.getEmail(),
                filial.getCidade(),
                filial.getUf(),
                filial.getMatriz(),
                filial.getAtivo(),
                filial.getObservacao()
        );
    }
}
