package com.orionerp.modules.pcp.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.pcp.domain.OrdemProducao;
import com.orionerp.modules.pcp.dto.OrdemProducaoRequest;
import com.orionerp.modules.pcp.dto.OrdemProducaoResponse;
import com.orionerp.modules.pcp.repository.OrdemProducaoRepository;
import com.orionerp.modules.pcp.repository.PcpSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrdemProducaoService {

    private final OrdemProducaoRepository repository;

    @Transactional(readOnly = true)
    public Page<OrdemProducaoResponse> listar(Long empresaId, Long produtoId, String status, String prioridade, String term, Pageable pageable) {
        return repository.findAll(
                PcpSpecifications.ordemProducaoFilter(empresaId, produtoId, status, prioridade, term), pageable)
                .map(OrdemProducaoResponse::from);
    }

    @Transactional(readOnly = true)
    public OrdemProducaoResponse buscarPorId(Long id) {
        return OrdemProducaoResponse.from(findOrFail(id));
    }

    @Transactional
    public OrdemProducaoResponse criar(OrdemProducaoRequest request) {
        if (repository.existsByEmpresaIdAndNumeroAndDeletedFalse(request.empresaId(), request.numero())) {
            throw new BusinessException("Já existe uma ordem de produção com este número");
        }
        OrdemProducao entity = new OrdemProducao();
        mapFields(entity, request);
        return OrdemProducaoResponse.from(repository.save(entity));
    }

    @Transactional
    public OrdemProducaoResponse atualizar(Long id, OrdemProducaoRequest request) {
        OrdemProducao entity = findOrFail(id);
        if ("FINALIZADA".equals(entity.getStatus())) {
            throw new BusinessException("Ordem de produção finalizada não pode ser alterada");
        }
        mapFields(entity, request);
        return OrdemProducaoResponse.from(repository.save(entity));
    }

    @Transactional
    public OrdemProducaoResponse iniciar(Long id) {
        OrdemProducao entity = findOrFail(id);
        if (!"PLANEJADA".equals(entity.getStatus())) {
            throw new BusinessException("Somente ordens planejadas podem ser iniciadas");
        }
        entity.setStatus("EM_PRODUCAO");
        return OrdemProducaoResponse.from(repository.save(entity));
    }

    @Transactional
    public OrdemProducaoResponse finalizar(Long id) {
        OrdemProducao entity = findOrFail(id);
        if (!"EM_PRODUCAO".equals(entity.getStatus())) {
            throw new BusinessException("Somente ordens em produção podem ser finalizadas");
        }
        entity.setStatus("FINALIZADA");
        entity.setDataFim(java.time.LocalDate.now());
        return OrdemProducaoResponse.from(repository.save(entity));
    }

    @Transactional
    public void excluir(Long id) {
        OrdemProducao entity = findOrFail(id);
        if ("FINALIZADA".equals(entity.getStatus())) {
            throw new BusinessException("Ordem de produção finalizada não pode ser excluída");
        }
        entity.softDelete();
        repository.save(entity);
    }

    private OrdemProducao findOrFail(Long id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordem de produção não encontrada"));
    }

    private void mapFields(OrdemProducao entity, OrdemProducaoRequest r) {
        entity.setEmpresaId(r.empresaId());
        entity.setFilialId(r.filialId());
        entity.setNumero(r.numero());
        entity.setProdutoId(r.produtoId());
        entity.setQuantidade(r.quantidade());
        if (r.dataInicio() != null) entity.setDataInicio(r.dataInicio());
        if (r.dataPrevisaoFim() != null) entity.setDataPrevisaoFim(r.dataPrevisaoFim());
        entity.setDataFim(r.dataFim());
        if (r.status() != null) entity.setStatus(r.status());
        if (r.prioridade() != null) entity.setPrioridade(r.prioridade());
        entity.setCentroCustoId(r.centroCustoId());
        entity.setObservacao(r.observacao());
    }
}
