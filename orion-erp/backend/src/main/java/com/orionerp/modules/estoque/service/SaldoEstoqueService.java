package com.orionerp.modules.estoque.service;

import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.estoque.domain.SaldoEstoque;
import com.orionerp.modules.estoque.dto.SaldoEstoqueResponse;
import com.orionerp.modules.estoque.repository.SaldoEstoqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SaldoEstoqueService {

    private final SaldoEstoqueRepository saldoEstoqueRepository;

    @Transactional(readOnly = true)
    public List<SaldoEstoqueResponse> listarPorProduto(Long empresaId, Long produtoId) {
        return saldoEstoqueRepository.findByEmpresaIdAndProdutoId(empresaId, produtoId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<SaldoEstoqueResponse> listarPorArmazem(Long empresaId, Long armazemId) {
        return saldoEstoqueRepository.findByEmpresaIdAndArmazemId(empresaId, armazemId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public SaldoEstoqueResponse buscarPorId(Long id) {
        SaldoEstoque entity = saldoEstoqueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Saldo de estoque não encontrado: " + id));
        return toResponse(entity);
    }

    private SaldoEstoqueResponse toResponse(SaldoEstoque e) {
        return new SaldoEstoqueResponse(
                e.getId(),
                e.getEmpresaId(),
                e.getFilialId(),
                e.getArmazem().getId(),
                e.getArmazem().getNome(),
                e.getLocalizacao() != null ? e.getLocalizacao().getId() : null,
                e.getProduto().getId(),
                e.getProduto().getCodigo(),
                e.getProduto().getNome(),
                e.getLote(),
                e.getValidade(),
                e.getQuantidade(),
                e.getCustoMedio(),
                e.getReservado(),
                e.getDisponivel());
    }
}
