package com.orionerp.modules.contratos.service;

import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.contratos.domain.ContratoParcela;
import com.orionerp.modules.contratos.dto.ContratoParcelaRequest;
import com.orionerp.modules.contratos.dto.ContratoParcelaResponse;
import com.orionerp.modules.contratos.repository.ContratoParcelaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContratoParcelaService {

    private final ContratoParcelaRepository repository;

    @Transactional(readOnly = true)
    public List<ContratoParcelaResponse> listarPorContrato(Long contratoId) {
        return repository.findByContratoIdAndDeletedFalse(contratoId).stream()
                .map(ContratoParcelaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ContratoParcelaResponse buscarPorId(Long id) {
        return ContratoParcelaResponse.from(findOrFail(id));
    }

    @Transactional
    public ContratoParcelaResponse criar(ContratoParcelaRequest request) {
        ContratoParcela entity = new ContratoParcela();
        mapFields(entity, request);
        return ContratoParcelaResponse.from(repository.save(entity));
    }

    @Transactional
    public ContratoParcelaResponse atualizar(Long id, ContratoParcelaRequest request) {
        ContratoParcela entity = findOrFail(id);
        mapFields(entity, request);
        return ContratoParcelaResponse.from(repository.save(entity));
    }

    @Transactional
    public void excluir(Long id) {
        ContratoParcela entity = findOrFail(id);
        entity.softDelete();
        repository.save(entity);
    }

    private ContratoParcela findOrFail(Long id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parcela não encontrada"));
    }

    private void mapFields(ContratoParcela entity, ContratoParcelaRequest r) {
        entity.setEmpresaId(r.empresaId());
        entity.setFilialId(r.filialId());
        entity.setContratoId(r.contratoId());
        entity.setNumeroParcela(r.numeroParcela());
        entity.setDataVencimento(r.dataVencimento());
        entity.setDataPagamento(r.dataPagamento());
        entity.setValor(r.valor());
        if (r.valorPago() != null) entity.setValorPago(r.valorPago());
        if (r.status() != null) entity.setStatus(r.status());
    }
}
