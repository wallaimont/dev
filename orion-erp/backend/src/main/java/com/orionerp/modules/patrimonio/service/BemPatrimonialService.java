package com.orionerp.modules.patrimonio.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.patrimonio.domain.BemPatrimonial;
import com.orionerp.modules.patrimonio.dto.BemPatrimonialRequest;
import com.orionerp.modules.patrimonio.dto.BemPatrimonialResponse;
import com.orionerp.modules.patrimonio.repository.BemPatrimonialRepository;
import com.orionerp.modules.patrimonio.repository.PatrimonioSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BemPatrimonialService {

    private final BemPatrimonialRepository repository;

    @Transactional(readOnly = true)
    public Page<BemPatrimonialResponse> listar(Long empresaId, String grupo, String status, String term, Pageable pageable) {
        return repository.findAll(
                PatrimonioSpecifications.bemPatrimonialFilter(empresaId, grupo, status, term), pageable)
                .map(BemPatrimonialResponse::from);
    }

    @Transactional(readOnly = true)
    public BemPatrimonialResponse buscarPorId(Long id) {
        return BemPatrimonialResponse.from(findOrFail(id));
    }

    @Transactional
    public BemPatrimonialResponse criar(BemPatrimonialRequest request) {
        if (repository.existsByEmpresaIdAndCodigoAndDeletedFalse(request.empresaId(), request.codigo())) {
            throw new BusinessException("Já existe um bem patrimonial com este código");
        }
        BemPatrimonial entity = new BemPatrimonial();
        mapFields(entity, request);
        return BemPatrimonialResponse.from(repository.save(entity));
    }

    @Transactional
    public BemPatrimonialResponse atualizar(Long id, BemPatrimonialRequest request) {
        BemPatrimonial entity = findOrFail(id);
        mapFields(entity, request);
        return BemPatrimonialResponse.from(repository.save(entity));
    }

    @Transactional
    public void excluir(Long id) {
        BemPatrimonial entity = findOrFail(id);
        entity.softDelete();
        repository.save(entity);
    }

    private BemPatrimonial findOrFail(Long id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bem patrimonial não encontrado"));
    }

    private void mapFields(BemPatrimonial entity, BemPatrimonialRequest r) {
        entity.setEmpresaId(r.empresaId());
        entity.setFilialId(r.filialId());
        entity.setCodigo(r.codigo());
        entity.setDescricao(r.descricao());
        entity.setNumeroPatrimonio(r.numeroPatrimonio());
        entity.setDataAquisicao(r.dataAquisicao());
        entity.setValorAquisicao(r.valorAquisicao());
        entity.setValorResidual(r.valorResidual() != null ? r.valorResidual() : java.math.BigDecimal.ZERO);
        entity.setVidaUtilMeses(r.vidaUtilMeses() != null ? r.vidaUtilMeses() : 60);
        entity.setTaxaDepreciacao(r.taxaDepreciacao() != null ? r.taxaDepreciacao() : java.math.BigDecimal.ZERO);
        entity.setGrupo(r.grupo());
        entity.setLocalizacao(r.localizacao());
        entity.setCentroCustoId(r.centroCustoId());
        entity.setFornecedorId(r.fornecedorId());
        entity.setNotaFiscalId(r.notaFiscalId());
        if (r.status() != null) entity.setStatus(r.status());
    }
}
