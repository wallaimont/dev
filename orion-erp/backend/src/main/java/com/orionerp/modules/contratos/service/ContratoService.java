package com.orionerp.modules.contratos.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.contratos.domain.Contrato;
import com.orionerp.modules.contratos.dto.ContratoRequest;
import com.orionerp.modules.contratos.dto.ContratoResponse;
import com.orionerp.modules.contratos.repository.ContratoRepository;
import com.orionerp.modules.contratos.repository.ContratosSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContratoService {

    private final ContratoRepository repository;

    @Transactional(readOnly = true)
    public Page<ContratoResponse> listar(Long empresaId, Long clienteId, String tipo, String status, String term, Pageable pageable) {
        return repository.findAll(
                ContratosSpecifications.contratoFilter(empresaId, clienteId, tipo, status, term), pageable)
                .map(ContratoResponse::from);
    }

    @Transactional(readOnly = true)
    public ContratoResponse buscarPorId(Long id) {
        return ContratoResponse.from(findOrFail(id));
    }

    @Transactional
    public ContratoResponse criar(ContratoRequest request) {
        if (repository.existsByEmpresaIdAndNumeroAndDeletedFalse(request.empresaId(), request.numero())) {
            throw new BusinessException("Já existe um contrato com este número");
        }
        Contrato entity = new Contrato();
        mapFields(entity, request);
        return ContratoResponse.from(repository.save(entity));
    }

    @Transactional
    public ContratoResponse atualizar(Long id, ContratoRequest request) {
        Contrato entity = findOrFail(id);
        mapFields(entity, request);
        return ContratoResponse.from(repository.save(entity));
    }

    @Transactional
    public void excluir(Long id) {
        Contrato entity = findOrFail(id);
        entity.softDelete();
        repository.save(entity);
    }

    private Contrato findOrFail(Long id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrato não encontrado"));
    }

    private void mapFields(Contrato entity, ContratoRequest r) {
        entity.setEmpresaId(r.empresaId());
        entity.setFilialId(r.filialId());
        entity.setNumero(r.numero());
        entity.setClienteId(r.clienteId());
        if (r.tipo() != null) entity.setTipo(r.tipo());
        if (r.descricao() != null) entity.setDescricao(r.descricao());
        if (r.dataInicio() != null) entity.setDataInicio(r.dataInicio());
        if (r.dataFim() != null) entity.setDataFim(r.dataFim());
        entity.setDataAssinatura(r.dataAssinatura());
        entity.setValorTotal(r.valorTotal() != null ? r.valorTotal() : java.math.BigDecimal.ZERO);
        entity.setValorMensal(r.valorMensal());
        entity.setFormaPagamento(r.formaPagamento());
        entity.setDiaVencimento(r.diaVencimento());
        if (r.status() != null) entity.setStatus(r.status());
        entity.setObservacao(r.observacao());
    }
}
