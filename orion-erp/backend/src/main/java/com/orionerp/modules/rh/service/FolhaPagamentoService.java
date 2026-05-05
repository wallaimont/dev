package com.orionerp.modules.rh.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.rh.domain.FolhaPagamento;
import com.orionerp.modules.rh.dto.FolhaPagamentoRequest;
import com.orionerp.modules.rh.dto.FolhaPagamentoResponse;
import com.orionerp.modules.rh.repository.FolhaPagamentoRepository;
import com.orionerp.modules.rh.repository.RhSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FolhaPagamentoService {

    private final FolhaPagamentoRepository repository;

    @Transactional(readOnly = true)
    public Page<FolhaPagamentoResponse> listar(Long empresaId, Integer ano, Integer mes,
                                                String tipo, String status, Pageable pageable) {
        return repository.findAll(
                RhSpecifications.folhaPagamentoFilter(empresaId, ano, mes, tipo, status), pageable)
                .map(FolhaPagamentoResponse::from);
    }

    @Transactional(readOnly = true)
    public FolhaPagamentoResponse buscarPorId(Long id) {
        return FolhaPagamentoResponse.from(findOrFail(id));
    }

    @Transactional
    public FolhaPagamentoResponse criar(FolhaPagamentoRequest request) {
        String tipo = request.tipo() != null ? request.tipo() : "MENSAL";
        if (repository.existsByEmpresaIdAndAnoAndMesAndTipoAndDeletedFalse(
                request.empresaId(), request.ano(), request.mes(), tipo)) {
            throw new BusinessException("Já existe folha de pagamento para este período e tipo");
        }
        FolhaPagamento entity = new FolhaPagamento();
        mapFields(entity, request);
        entity.setStatus("ABERTA");
        return FolhaPagamentoResponse.from(repository.save(entity));
    }

    @Transactional
    public FolhaPagamentoResponse atualizar(Long id, FolhaPagamentoRequest request) {
        FolhaPagamento entity = findOrFail(id);
        if ("FECHADA".equals(entity.getStatus())) {
            throw new BusinessException("Não é possível alterar uma folha fechada");
        }
        mapFields(entity, request);
        return FolhaPagamentoResponse.from(repository.save(entity));
    }

    @Transactional
    public FolhaPagamentoResponse fechar(Long id) {
        FolhaPagamento entity = findOrFail(id);
        if ("FECHADA".equals(entity.getStatus())) {
            throw new BusinessException("Folha já está fechada");
        }
        entity.setStatus("FECHADA");
        return FolhaPagamentoResponse.from(repository.save(entity));
    }

    @Transactional
    public void excluir(Long id) {
        FolhaPagamento entity = findOrFail(id);
        if ("FECHADA".equals(entity.getStatus())) {
            throw new BusinessException("Não é possível excluir uma folha fechada");
        }
        entity.softDelete();
        repository.save(entity);
    }

    private FolhaPagamento findOrFail(Long id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Folha de pagamento não encontrada"));
    }

    private void mapFields(FolhaPagamento entity, FolhaPagamentoRequest r) {
        entity.setEmpresaId(r.empresaId());
        entity.setFilialId(r.filialId());
        entity.setAno(r.ano());
        entity.setMes(r.mes());
        entity.setTipo(r.tipo() != null ? r.tipo() : "MENSAL");
        entity.setDataCalculo(r.dataCalculo() != null ? r.dataCalculo() : java.time.LocalDate.now());
        entity.setDataPagamento(r.dataPagamento());
        entity.setTotalProventos(r.totalProventos() != null ? r.totalProventos() : java.math.BigDecimal.ZERO);
        entity.setTotalDescontos(r.totalDescontos() != null ? r.totalDescontos() : java.math.BigDecimal.ZERO);
        entity.setTotalLiquido(r.totalLiquido() != null ? r.totalLiquido() : java.math.BigDecimal.ZERO);
        entity.setTotalEncargos(r.totalEncargos() != null ? r.totalEncargos() : java.math.BigDecimal.ZERO);
    }
}
