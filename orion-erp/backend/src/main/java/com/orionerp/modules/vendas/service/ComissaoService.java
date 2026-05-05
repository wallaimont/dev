package com.orionerp.modules.vendas.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.vendas.domain.Comissao;
import com.orionerp.modules.vendas.dto.ComissaoRequest;
import com.orionerp.modules.vendas.dto.ComissaoResponse;
import com.orionerp.modules.vendas.repository.ComissaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComissaoService {

    private final ComissaoRepository comissaoRepository;

    @Transactional(readOnly = true)
    public List<ComissaoResponse> listarPorVendedor(Long empresaId, Long vendedorId) {
        return comissaoRepository.findByEmpresaIdAndVendedorId(empresaId, vendedorId)
                .stream().map(ComissaoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ComissaoResponse> listarPorPedido(Long pedidoVendaId) {
        return comissaoRepository.findByPedidoVendaId(pedidoVendaId)
                .stream().map(ComissaoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ComissaoResponse buscarPorId(Long id) {
        Comissao entity = comissaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comissão não encontrada: " + id));
        return ComissaoResponse.from(entity);
    }

    @Transactional
    public ComissaoResponse criar(ComissaoRequest request) {
        Comissao entity = new Comissao();
        entity.setEmpresaId(request.empresaId());
        entity.setFilialId(request.filialId());
        entity.setVendedorId(request.vendedorId());
        entity.setPedidoVendaId(request.pedidoVendaId());
        entity.setPercentual(request.percentual());
        entity.setValorBase(request.valorBase());
        entity.setValorComissao(request.valorComissao());
        entity.setStatus(request.status() != null ? request.status() : "PENDENTE");

        return ComissaoResponse.from(comissaoRepository.save(entity));
    }

    @Transactional
    public ComissaoResponse atualizar(Long id, ComissaoRequest request) {
        Comissao entity = comissaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comissão não encontrada: " + id));

        entity.setVendedorId(request.vendedorId());
        entity.setPedidoVendaId(request.pedidoVendaId());
        entity.setPercentual(request.percentual());
        entity.setValorBase(request.valorBase());
        entity.setValorComissao(request.valorComissao());
        if (request.status() != null) entity.setStatus(request.status());
        entity.setUpdatedAt(LocalDateTime.now());

        return ComissaoResponse.from(comissaoRepository.save(entity));
    }

    @Transactional
    public void pagar(Long id) {
        Comissao entity = comissaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comissão não encontrada: " + id));
        if ("PAGO".equals(entity.getStatus())) {
            throw new BusinessException("Comissão já está paga");
        }
        entity.setStatus("PAGO");
        entity.setDataPagamento(LocalDate.now());
        entity.setUpdatedAt(LocalDateTime.now());
        comissaoRepository.save(entity);
    }
}
