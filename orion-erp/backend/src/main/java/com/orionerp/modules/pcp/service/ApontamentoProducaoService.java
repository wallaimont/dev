package com.orionerp.modules.pcp.service;

import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.pcp.domain.ApontamentoProducao;
import com.orionerp.modules.pcp.dto.ApontamentoProducaoRequest;
import com.orionerp.modules.pcp.dto.ApontamentoProducaoResponse;
import com.orionerp.modules.pcp.repository.ApontamentoProducaoRepository;
import com.orionerp.modules.pcp.repository.PcpSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApontamentoProducaoService {

    private final ApontamentoProducaoRepository repository;

    @Transactional(readOnly = true)
    public Page<ApontamentoProducaoResponse> listar(Long empresaId, Long ordemProducaoId, Long funcionarioId, Pageable pageable) {
        return repository.findAll(
                PcpSpecifications.apontamentoFilter(empresaId, ordemProducaoId, funcionarioId), pageable)
                .map(ApontamentoProducaoResponse::from);
    }

    @Transactional(readOnly = true)
    public ApontamentoProducaoResponse buscarPorId(Long id) {
        return ApontamentoProducaoResponse.from(findOrFail(id));
    }

    @Transactional
    public ApontamentoProducaoResponse criar(ApontamentoProducaoRequest request) {
        ApontamentoProducao entity = new ApontamentoProducao();
        mapFields(entity, request);
        return ApontamentoProducaoResponse.from(repository.save(entity));
    }

    @Transactional
    public ApontamentoProducaoResponse atualizar(Long id, ApontamentoProducaoRequest request) {
        ApontamentoProducao entity = findOrFail(id);
        mapFields(entity, request);
        return ApontamentoProducaoResponse.from(repository.save(entity));
    }

    @Transactional
    public void excluir(Long id) {
        ApontamentoProducao entity = findOrFail(id);
        entity.softDelete();
        repository.save(entity);
    }

    private ApontamentoProducao findOrFail(Long id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Apontamento de produção não encontrado"));
    }

    private void mapFields(ApontamentoProducao entity, ApontamentoProducaoRequest r) {
        entity.setEmpresaId(r.empresaId());
        entity.setFilialId(r.filialId());
        entity.setOrdemProducaoId(r.ordemProducaoId());
        entity.setFuncionarioId(r.funcionarioId());
        entity.setDataInicio(r.dataInicio());
        entity.setDataFim(r.dataFim());
        entity.setQuantidadeProduzida(r.quantidadeProduzida());
        entity.setQuantidadeRejeitada(r.quantidadeRejeitada());
        entity.setMaquina(r.maquina());
        entity.setObservacao(r.observacao());
    }
}
