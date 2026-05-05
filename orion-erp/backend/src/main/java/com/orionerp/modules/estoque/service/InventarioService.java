package com.orionerp.modules.estoque.service;

import com.orionerp.common.PageResponse;
import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.cadastros.domain.Produto;
import com.orionerp.modules.cadastros.repository.ProdutoRepository;
import com.orionerp.modules.estoque.domain.Inventario;
import com.orionerp.modules.estoque.domain.InventarioItem;
import com.orionerp.modules.estoque.domain.SaldoEstoque;
import com.orionerp.modules.estoque.dto.InventarioRequest;
import com.orionerp.modules.estoque.dto.InventarioResponse;
import com.orionerp.modules.estoque.dto.MovimentacaoRequest;
import com.orionerp.modules.estoque.repository.InventarioRepository;
import com.orionerp.modules.estoque.repository.SaldoEstoqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventarioService {

    private final InventarioRepository inventarioRepository;
    private final SaldoEstoqueRepository saldoRepository;
    private final ProdutoRepository produtoRepository;
    private final ArmazemService armazemService;
    private final MovimentacaoEstoqueService movimentacaoService;

    @Transactional(readOnly = true)
    public PageResponse<InventarioResponse> list(Long empresaId, Long filialId, Long armazemId,
                                                 String status, String term, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataInventario"));
        var spec = EstoqueSpecifications.inventarioFilter(empresaId, filialId, armazemId, status, term);
        var result = inventarioRepository.findAll(spec, pageable).map(this::toResponse);
        return PageResponse.from(result);
    }

    @Transactional(readOnly = true)
    public InventarioResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public InventarioResponse create(InventarioRequest request) {
        if (inventarioRepository.existsByEmpresaIdAndFilialIdAndNumeroAndDeletedFalse(
                request.empresaId(), request.filialId(), request.numero())) {
            throw new BusinessException("Ja existe inventario com este numero");
        }

        var armazem = armazemService.findById(request.armazemId());

        Inventario inventario = new Inventario();
        inventario.setEmpresaId(request.empresaId());
        inventario.setFilialId(request.filialId());
        inventario.setArmazem(armazem);
        inventario.setNumero(request.numero().trim());
        inventario.setDataInventario(request.dataInventario() != null ? request.dataInventario() : LocalDate.now());
        inventario.setResponsavelId(request.responsavelId());
        inventario.setObservacao(request.observacao());

        if (request.itens() != null) {
            for (InventarioRequest.ItemInput ii : request.itens()) {
                Produto produto = produtoRepository.findByIdAndDeletedFalse(ii.produtoId())
                        .orElseThrow(() -> new ResourceNotFoundException("Produto nao encontrado"));

                BigDecimal qtdSistema = saldoRepository
                        .findByEmpresaIdAndFilialIdAndArmazemIdAndProdutoIdAndLote(
                                request.empresaId(), request.filialId(), armazem.getId(),
                                produto.getId(), ii.lote())
                        .map(SaldoEstoque::getQuantidade)
                        .orElse(BigDecimal.ZERO);

                InventarioItem item = new InventarioItem();
                item.setProduto(produto);
                item.setLote(ii.lote());
                item.setQuantidadeSistema(qtdSistema);
                inventario.addItem(item);
            }
        }

        return toResponse(inventarioRepository.save(inventario));
    }

    @Transactional
    public InventarioResponse registrarContagem(Long id, Long itemId, BigDecimal quantidadeContada) {
        Inventario inventario = findById(id);
        if (!"ABERTO".equals(inventario.getStatus()) && !"EM_CONTAGEM".equals(inventario.getStatus())) {
            throw new BusinessException("Inventario nao esta aberto para contagem");
        }

        InventarioItem item = inventario.getItens().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item de inventario nao encontrado"));

        item.setQuantidadeContada(quantidadeContada);
        item.setDiferenca(quantidadeContada.subtract(item.getQuantidadeSistema()));
        item.setUpdatedAt(java.time.LocalDateTime.now());

        if ("ABERTO".equals(inventario.getStatus())) {
            inventario.setStatus("EM_CONTAGEM");
        }

        return toResponse(inventarioRepository.save(inventario));
    }

    @Transactional
    public InventarioResponse finalizar(Long id, String username) {
        Inventario inventario = findById(id);
        if (!"EM_CONTAGEM".equals(inventario.getStatus())) {
            throw new BusinessException("Inventario deve estar em contagem para ser finalizado");
        }

        for (InventarioItem item : inventario.getItens()) {
            if (item.getQuantidadeContada() == null) {
                throw new BusinessException("Todos os itens devem ter contagem registrada");
            }

            if (item.getDiferenca() != null && item.getDiferenca().compareTo(BigDecimal.ZERO) != 0 && !item.getAjustado()) {
                MovimentacaoRequest ajuste = new MovimentacaoRequest(
                        inventario.getEmpresaId(), inventario.getFilialId(),
                        inventario.getArmazem().getId(), null,
                        item.getProduto().getId(), "AJUSTE",
                        item.getQuantidadeContada(), null,
                        item.getLote(), null,
                        "INVENTARIO", inventario.getId(), inventario.getNumero(),
                        "Ajuste inventario " + inventario.getNumero());
                movimentacaoService.movimentar(ajuste, username);
                item.setAjustado(true);
            }
        }

        inventario.setStatus("FINALIZADO");
        return toResponse(inventarioRepository.save(inventario));
    }

    @Transactional
    public void cancelar(Long id) {
        Inventario inventario = findById(id);
        if ("FINALIZADO".equals(inventario.getStatus())) {
            throw new BusinessException("Inventario finalizado nao pode ser cancelado");
        }
        inventario.setStatus("CANCELADO");
        inventarioRepository.save(inventario);
    }

    private Inventario findById(Long id) {
        return inventarioRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario nao encontrado"));
    }

    private InventarioResponse toResponse(Inventario inv) {
        List<InventarioResponse.ItemResponse> itens = inv.getItens().stream()
                .map(i -> new InventarioResponse.ItemResponse(
                        i.getId(), i.getProduto().getId(),
                        i.getProduto().getCodigo(), i.getProduto().getNome(),
                        i.getLote(), i.getQuantidadeSistema(),
                        i.getQuantidadeContada(), i.getDiferenca(), i.getAjustado()))
                .toList();

        return new InventarioResponse(
                inv.getId(), inv.getUuid(), inv.getEmpresaId(), inv.getFilialId(),
                inv.getArmazem().getId(), inv.getArmazem().getNome(),
                inv.getNumero(), inv.getDataInventario(),
                inv.getResponsavelId(), inv.getStatus(), inv.getObservacao(), itens);
    }
}
