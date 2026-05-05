package com.orionerp.modules.rh.service;

import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.rh.domain.FolhaPagamentoItem;
import com.orionerp.modules.rh.dto.FolhaPagamentoItemRequest;
import com.orionerp.modules.rh.dto.FolhaPagamentoItemResponse;
import com.orionerp.modules.rh.repository.FolhaPagamentoItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FolhaPagamentoItemService {

    private final FolhaPagamentoItemRepository repository;

    @Transactional(readOnly = true)
    public List<FolhaPagamentoItemResponse> listarPorFolha(Long folhaPagamentoId) {
        return repository.findByFolhaPagamentoIdAndDeletedFalse(folhaPagamentoId).stream()
                .map(FolhaPagamentoItemResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public FolhaPagamentoItemResponse buscarPorId(Long id) {
        return FolhaPagamentoItemResponse.from(findOrFail(id));
    }

    @Transactional
    public FolhaPagamentoItemResponse criar(FolhaPagamentoItemRequest request) {
        FolhaPagamentoItem entity = new FolhaPagamentoItem();
        mapFields(entity, request);
        return FolhaPagamentoItemResponse.from(repository.save(entity));
    }

    @Transactional
    public FolhaPagamentoItemResponse atualizar(Long id, FolhaPagamentoItemRequest request) {
        FolhaPagamentoItem entity = findOrFail(id);
        mapFields(entity, request);
        return FolhaPagamentoItemResponse.from(repository.save(entity));
    }

    @Transactional
    public void excluir(Long id) {
        FolhaPagamentoItem entity = findOrFail(id);
        entity.softDelete();
        repository.save(entity);
    }

    private FolhaPagamentoItem findOrFail(Long id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item da folha não encontrado"));
    }

    private void mapFields(FolhaPagamentoItem entity, FolhaPagamentoItemRequest r) {
        entity.setEmpresaId(r.empresaId());
        entity.setFilialId(r.filialId());
        entity.setFolhaPagamentoId(r.folhaPagamentoId());
        entity.setFuncionarioId(r.funcionarioId());
        if (r.salarioBase() != null) entity.setSalarioBase(r.salarioBase());
        if (r.horasExtrasValor() != null) entity.setHorasExtrasValor(r.horasExtrasValor());
        if (r.adicionalNoturno() != null) entity.setAdicionalNoturno(r.adicionalNoturno());
        if (r.adicionalPericulosidade() != null) entity.setAdicionalPericulosidade(r.adicionalPericulosidade());
        if (r.adicionalInsalubridade() != null) entity.setAdicionalInsalubridade(r.adicionalInsalubridade());
        if (r.comissoes() != null) entity.setComissoes(r.comissoes());
        if (r.gratificacoes() != null) entity.setGratificacoes(r.gratificacoes());
        if (r.outrosProventos() != null) entity.setOutrosProventos(r.outrosProventos());
        if (r.descontoInss() != null) entity.setDescontoInss(r.descontoInss());
        if (r.descontoIrrf() != null) entity.setDescontoIrrf(r.descontoIrrf());
        if (r.descontoVt() != null) entity.setDescontoVt(r.descontoVt());
        if (r.descontoVr() != null) entity.setDescontoVr(r.descontoVr());
        if (r.descontoPlanoSaude() != null) entity.setDescontoPlanoSaude(r.descontoPlanoSaude());
        if (r.descontoSindical() != null) entity.setDescontoSindical(r.descontoSindical());
        if (r.outrosDescontos() != null) entity.setOutrosDescontos(r.outrosDescontos());
        if (r.salarioLiquido() != null) entity.setSalarioLiquido(r.salarioLiquido());
        if (r.fgts() != null) entity.setFgts(r.fgts());
        if (r.inssEmpresa() != null) entity.setInssEmpresa(r.inssEmpresa());
    }
}
