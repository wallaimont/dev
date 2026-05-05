package com.orionerp.modules.faturamento.service;

import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.faturamento.domain.NotaFiscalItem;
import com.orionerp.modules.faturamento.dto.NotaFiscalItemRequest;
import com.orionerp.modules.faturamento.dto.NotaFiscalItemResponse;
import com.orionerp.modules.faturamento.repository.NotaFiscalItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotaFiscalItemService {

    private final NotaFiscalItemRepository repository;

    @Transactional(readOnly = true)
    public List<NotaFiscalItemResponse> listarPorNotaFiscal(Long notaFiscalId) {
        return repository.findByNotaFiscalIdAndDeletedFalse(notaFiscalId).stream()
                .map(NotaFiscalItemResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public NotaFiscalItemResponse buscarPorId(Long id) {
        return repository.findByIdAndDeletedFalse(id)
                .map(NotaFiscalItemResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Item da nota fiscal não encontrado"));
    }

    @Transactional
    public NotaFiscalItemResponse criar(NotaFiscalItemRequest req) {
        var item = new NotaFiscalItem();
        mapFields(item, req);
        return NotaFiscalItemResponse.from(repository.save(item));
    }

    @Transactional
    public NotaFiscalItemResponse atualizar(Long id, NotaFiscalItemRequest req) {
        var item = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item da nota fiscal não encontrado"));
        mapFields(item, req);
        return NotaFiscalItemResponse.from(repository.save(item));
    }

    @Transactional
    public void excluir(Long id) {
        var item = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item da nota fiscal não encontrado"));
        item.softDelete();
        repository.save(item);
    }

    private void mapFields(NotaFiscalItem item, NotaFiscalItemRequest req) {
        item.setEmpresaId(req.empresaId());
        if (req.filialId() != null) item.setFilialId(req.filialId());
        item.setNotaFiscalId(req.notaFiscalId());
        if (req.numeroItem() != null) item.setNumeroItem(req.numeroItem());
        item.setProdutoId(req.produtoId());
        item.setDescricao(req.descricao());
        item.setNcm(req.ncm());
        item.setCfop(req.cfop());
        item.setQuantidade(req.quantidade());
        item.setValorUnitario(req.valorUnitario());
        item.setValorTotal(req.valorTotal());
        if (req.valorDesconto() != null) item.setValorDesconto(req.valorDesconto());
        item.setIcmsCst(req.icmsCst());
        if (req.icmsBase() != null) item.setIcmsBase(req.icmsBase());
        if (req.icmsAliquota() != null) item.setIcmsAliquota(req.icmsAliquota());
        if (req.icmsValor() != null) item.setIcmsValor(req.icmsValor());
        if (req.icmsStBase() != null) item.setIcmsStBase(req.icmsStBase());
        if (req.icmsStAliquota() != null) item.setIcmsStAliquota(req.icmsStAliquota());
        if (req.icmsStValor() != null) item.setIcmsStValor(req.icmsStValor());
        item.setIpiCst(req.ipiCst());
        if (req.ipiBase() != null) item.setIpiBase(req.ipiBase());
        if (req.ipiAliquota() != null) item.setIpiAliquota(req.ipiAliquota());
        if (req.ipiValor() != null) item.setIpiValor(req.ipiValor());
        item.setPisCst(req.pisCst());
        if (req.pisBase() != null) item.setPisBase(req.pisBase());
        if (req.pisAliquota() != null) item.setPisAliquota(req.pisAliquota());
        if (req.pisValor() != null) item.setPisValor(req.pisValor());
        item.setCofinsCst(req.cofinsCst());
        if (req.cofinsBase() != null) item.setCofinsBase(req.cofinsBase());
        if (req.cofinsAliquota() != null) item.setCofinsAliquota(req.cofinsAliquota());
        if (req.cofinsValor() != null) item.setCofinsValor(req.cofinsValor());
    }
}
