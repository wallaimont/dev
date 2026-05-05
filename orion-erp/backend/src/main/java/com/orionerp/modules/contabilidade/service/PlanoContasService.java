package com.orionerp.modules.contabilidade.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.contabilidade.domain.PlanoContas;
import com.orionerp.modules.contabilidade.dto.PlanoContasRequest;
import com.orionerp.modules.contabilidade.dto.PlanoContasResponse;
import com.orionerp.modules.contabilidade.repository.ContabilidadeSpecifications;
import com.orionerp.modules.contabilidade.repository.PlanoContasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlanoContasService {

    private final PlanoContasRepository repository;

    @Transactional(readOnly = true)
    public Page<PlanoContasResponse> listar(Long empresaId, String search, String tipo, Pageable pageable) {
        return repository.findAll(
                ContabilidadeSpecifications.planoContasFilter(empresaId, search, tipo), pageable
        ).map(PlanoContasResponse::from);
    }

    @Transactional(readOnly = true)
    public PlanoContasResponse buscarPorId(Long id) {
        return PlanoContasResponse.from(findOrFail(id));
    }

    @Transactional
    public PlanoContasResponse criar(PlanoContasRequest request) {
        if (repository.existsByEmpresaIdAndCodigoAndDeletedFalse(request.empresaId(), request.codigo())) {
            throw new BusinessException("Já existe uma conta com este código para esta empresa");
        }
        PlanoContas entity = new PlanoContas();
        mapToEntity(request, entity);
        return PlanoContasResponse.from(repository.save(entity));
    }

    @Transactional
    public PlanoContasResponse atualizar(Long id, PlanoContasRequest request) {
        PlanoContas entity = findOrFail(id);
        mapToEntity(request, entity);
        return PlanoContasResponse.from(repository.save(entity));
    }

    @Transactional
    public void excluir(Long id) {
        PlanoContas entity = findOrFail(id);
        entity.softDelete();
        repository.save(entity);
    }

    private PlanoContas findOrFail(Long id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plano de contas não encontrado"));
    }

    private void mapToEntity(PlanoContasRequest req, PlanoContas entity) {
        entity.setEmpresaId(req.empresaId());
        entity.setCodigo(req.codigo());
        entity.setDescricao(req.descricao());
        entity.setTipo(normalizeTipo(req.tipo()));
        entity.setNatureza(normalizeNatureza(req.natureza()));
        entity.setClassificacao(req.classificacao());
        entity.setContaPaiId(req.contaPaiId());
        entity.setNivel(req.nivel() != null ? req.nivel() : 1);
        entity.setAceitaLancamento(req.aceitaLancamento() != null ? req.aceitaLancamento() : true);
    }

    private String normalizeTipo(String tipo) {
        if (tipo == null) return "S";
        return switch (tipo.toUpperCase().trim()) {
            case "SINTETICA", "SINTÉTICA", "S" -> "S";
            case "ANALITICA", "ANALÍTICA", "A" -> "A";
            default -> "S";
        };
    }

    private String normalizeNatureza(String natureza) {
        if (natureza == null) return "D";
        return switch (natureza.toUpperCase().trim()) {
            case "DEVEDORA", "D" -> "D";
            case "CREDORA", "C" -> "C";
            default -> "D";
        };
    }
}
