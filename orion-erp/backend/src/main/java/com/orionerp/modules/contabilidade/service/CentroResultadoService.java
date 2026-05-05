package com.orionerp.modules.contabilidade.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.contabilidade.domain.CentroResultado;
import com.orionerp.modules.contabilidade.dto.CentroResultadoRequest;
import com.orionerp.modules.contabilidade.dto.CentroResultadoResponse;
import com.orionerp.modules.contabilidade.repository.CentroResultadoRepository;
import com.orionerp.modules.contabilidade.repository.ContabilidadeSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CentroResultadoService {

    private final CentroResultadoRepository repository;

    @Transactional(readOnly = true)
    public Page<CentroResultadoResponse> listar(Long empresaId, String search, Pageable pageable) {
        return repository.findAll(
                ContabilidadeSpecifications.centroResultadoFilter(empresaId, search), pageable
        ).map(CentroResultadoResponse::from);
    }

    @Transactional(readOnly = true)
    public CentroResultadoResponse buscarPorId(Long id) {
        return CentroResultadoResponse.from(findOrFail(id));
    }

    @Transactional
    public CentroResultadoResponse criar(CentroResultadoRequest request) {
        if (repository.existsByEmpresaIdAndCodigoAndDeletedFalse(request.empresaId(), request.codigo())) {
            throw new BusinessException("Já existe um centro de resultado com este código");
        }
        CentroResultado entity = new CentroResultado();
        entity.setEmpresaId(request.empresaId());
        entity.setCodigo(request.codigo());
        entity.setDescricao(request.descricao());
        entity.setTipo(normalizeTipoCR(request.tipo()));
        entity.setResponsavel(request.responsavel());
        return CentroResultadoResponse.from(repository.save(entity));
    }

    @Transactional
    public CentroResultadoResponse atualizar(Long id, CentroResultadoRequest request) {
        CentroResultado entity = findOrFail(id);
        entity.setCodigo(request.codigo());
        entity.setDescricao(request.descricao());
        entity.setTipo(normalizeTipoCR(request.tipo()));
        entity.setResponsavel(request.responsavel());
        return CentroResultadoResponse.from(repository.save(entity));
    }

    @Transactional
    public void excluir(Long id) {
        CentroResultado entity = findOrFail(id);
        entity.softDelete();
        repository.save(entity);
    }

    private CentroResultado findOrFail(Long id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Centro de resultado não encontrado"));
    }

    private String normalizeTipoCR(String tipo) {
        if (tipo == null) return "RESULTADO";
        String upper = tipo.toUpperCase().trim();
        return switch (upper) {
            case "RESULTADO", "RES" -> "RESULTADO";
            case "INVESTIMENTO", "INV" -> "INVESTIMENTO";
            case "FINANCEIRO", "FIN" -> "FINANCEIRO";
            default -> "RESULTADO";
        };
    }
}
