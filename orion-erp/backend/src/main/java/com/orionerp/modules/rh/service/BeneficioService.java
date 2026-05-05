package com.orionerp.modules.rh.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.rh.domain.Beneficio;
import com.orionerp.modules.rh.dto.BeneficioRequest;
import com.orionerp.modules.rh.dto.BeneficioResponse;
import com.orionerp.modules.rh.repository.BeneficioRepository;
import com.orionerp.modules.rh.repository.RhSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BeneficioService {

    private final BeneficioRepository repository;

    @Transactional(readOnly = true)
    public Page<BeneficioResponse> listar(Long empresaId, String tipo, String term, Pageable pageable) {
        return repository.findAll(RhSpecifications.beneficioFilter(empresaId, tipo, term), pageable)
                .map(BeneficioResponse::from);
    }

    @Transactional(readOnly = true)
    public BeneficioResponse buscarPorId(Long id) {
        return BeneficioResponse.from(findOrFail(id));
    }

    @Transactional
    public BeneficioResponse criar(BeneficioRequest request) {
        if (repository.existsByEmpresaIdAndCodigoAndDeletedFalse(request.empresaId(), request.codigo())) {
            throw new BusinessException("Já existe um benefício com o código " + request.codigo());
        }
        Beneficio entity = new Beneficio();
        mapFields(entity, request);
        return BeneficioResponse.from(repository.save(entity));
    }

    @Transactional
    public BeneficioResponse atualizar(Long id, BeneficioRequest request) {
        Beneficio entity = findOrFail(id);
        mapFields(entity, request);
        return BeneficioResponse.from(repository.save(entity));
    }

    @Transactional
    public void excluir(Long id) {
        Beneficio entity = findOrFail(id);
        entity.softDelete();
        repository.save(entity);
    }

    private Beneficio findOrFail(Long id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Benefício não encontrado"));
    }

    private void mapFields(Beneficio entity, BeneficioRequest r) {
        entity.setEmpresaId(r.empresaId());
        entity.setCodigo(r.codigo());
        entity.setNome(r.nome());
        entity.setTipo(normalizeTipoBeneficio(r.tipo()));
        entity.setValorEmpresa(r.valorEmpresa() != null ? r.valorEmpresa() : BigDecimal.ZERO);
        entity.setValorFuncionario(r.valorFuncionario() != null ? r.valorFuncionario() : BigDecimal.ZERO);
        entity.setDescontoFolha(r.descontoFolha() != null ? r.descontoFolha() : true);
    }

    private String normalizeTipoBeneficio(String tipo) {
        if (tipo == null) return "OUTROS";
        String upper = tipo.toUpperCase().trim();
        return switch (upper) {
            case "VT", "VALE_TRANSPORTE" -> "VALE_TRANSPORTE";
            case "VR", "VALE_REFEICAO" -> "VALE_REFEICAO";
            case "VA", "VALE_ALIMENTACAO" -> "VALE_ALIMENTACAO";
            case "PS", "PLANO_SAUDE" -> "PLANO_SAUDE";
            case "PO", "PLANO_ODONTOLOGICO" -> "PLANO_ODONTOLOGICO";
            case "SV", "SEGURO_VIDA" -> "SEGURO_VIDA";
            case "AC", "AUXILIO_CRECHE" -> "AUXILIO_CRECHE";
            case "PLR" -> "PLR";
            default -> "OUTROS";
        };
    }
}
