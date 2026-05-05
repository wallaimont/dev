package com.orionerp.modules.faturamento.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.faturamento.domain.NotaFiscal;
import com.orionerp.modules.faturamento.dto.NotaFiscalRequest;
import com.orionerp.modules.faturamento.dto.NotaFiscalResponse;
import com.orionerp.modules.faturamento.repository.FaturamentoSpecifications;
import com.orionerp.modules.faturamento.repository.NotaFiscalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class NotaFiscalService {

    private final NotaFiscalRepository repository;

    @Transactional(readOnly = true)
    public Page<NotaFiscalResponse> listar(Long empresaId, String tipo, String numero,
                                            LocalDate dataInicio, LocalDate dataFim, String status, Pageable pageable) {
        var spec = FaturamentoSpecifications.notaFiscalFilter(empresaId, tipo, numero, dataInicio, dataFim, status);
        return repository.findAll(spec, pageable).map(NotaFiscalResponse::from);
    }

    @Transactional(readOnly = true)
    public NotaFiscalResponse buscarPorId(Long id) {
        return repository.findByIdAndDeletedFalse(id)
                .map(NotaFiscalResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Nota fiscal não encontrada"));
    }

    @Transactional
    public NotaFiscalResponse criar(NotaFiscalRequest req) {
        var nf = new NotaFiscal();
        mapFields(nf, req);
        nf.setStatus("DIGITADA");
        return NotaFiscalResponse.from(repository.save(nf));
    }

    @Transactional
    public NotaFiscalResponse atualizar(Long id, NotaFiscalRequest req) {
        var nf = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nota fiscal não encontrada"));
        if (!"DIGITADA".equals(nf.getStatus())) {
            throw new BusinessException("Apenas notas com status DIGITADA podem ser editadas");
        }
        mapFields(nf, req);
        return NotaFiscalResponse.from(repository.save(nf));
    }

    @Transactional
    public NotaFiscalResponse alterarStatus(Long id, String novoStatus) {
        var nf = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nota fiscal não encontrada"));
        nf.setStatus(novoStatus);
        return NotaFiscalResponse.from(repository.save(nf));
    }

    @Transactional
    public void excluir(Long id) {
        var nf = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nota fiscal não encontrada"));
        if (!"DIGITADA".equals(nf.getStatus())) {
            throw new BusinessException("Apenas notas com status DIGITADA podem ser excluídas");
        }
        nf.softDelete();
        repository.save(nf);
    }

    private void mapFields(NotaFiscal nf, NotaFiscalRequest req) {
        nf.setEmpresaId(req.empresaId());
        nf.setFilialId(req.filialId());
        nf.setTipo(req.tipo());
        nf.setSerie(req.serie() != null ? req.serie() : "1");
        nf.setNumero(req.numero());
        nf.setChaveAcesso(req.chaveAcesso());
        nf.setModelo(req.modelo() != null ? req.modelo() : "55");
        nf.setNaturezaOperacaoId(req.naturezaOperacaoId());
        nf.setCfopPredominante(req.cfopPredominante());
        nf.setDataEmissao(req.dataEmissao() != null ? req.dataEmissao() : java.time.LocalDate.now());
        nf.setDataSaidaEntrada(req.dataSaidaEntrada());
        nf.setClienteId(req.clienteId());
        nf.setFornecedorId(req.fornecedorId());
        nf.setTransportadoraId(req.transportadoraId());
        nf.setFretePorConta(normalizeFretePorConta(req.fretePorConta()));
        nf.setValorProdutos(req.valorProdutos() != null ? req.valorProdutos() : java.math.BigDecimal.ZERO);
        nf.setValorFrete(req.valorFrete() != null ? req.valorFrete() : java.math.BigDecimal.ZERO);
        nf.setValorSeguro(req.valorSeguro() != null ? req.valorSeguro() : java.math.BigDecimal.ZERO);
        nf.setValorDesconto(req.valorDesconto() != null ? req.valorDesconto() : java.math.BigDecimal.ZERO);
        nf.setValorOutrasDespesas(req.valorOutrasDespesas() != null ? req.valorOutrasDespesas() : java.math.BigDecimal.ZERO);
        nf.setValorIpi(req.valorIpi() != null ? req.valorIpi() : java.math.BigDecimal.ZERO);
        nf.setValorIcms(req.valorIcms() != null ? req.valorIcms() : java.math.BigDecimal.ZERO);
        nf.setValorIcmsSt(req.valorIcmsSt() != null ? req.valorIcmsSt() : java.math.BigDecimal.ZERO);
        nf.setValorPis(req.valorPis() != null ? req.valorPis() : java.math.BigDecimal.ZERO);
        nf.setValorCofins(req.valorCofins() != null ? req.valorCofins() : java.math.BigDecimal.ZERO);
        nf.setValorTotal(req.valorTotal() != null ? req.valorTotal() : java.math.BigDecimal.ZERO);
        nf.setInformacoesComplementares(req.informacoesComplementares());
        nf.setPedidoVendaId(req.pedidoVendaId());
        nf.setPedidoCompraId(req.pedidoCompraId());
        nf.setProtocoloAutorizacao(req.protocoloAutorizacao());
    }

    private String normalizeFretePorConta(String frete) {
        if (frete == null) return "SEM_FRETE";
        return switch (frete.toUpperCase().trim()) {
            case "EMITENTE", "REMETENTE", "CIF", "0" -> "EMITENTE";
            case "DESTINATARIO", "DESTINATÁRIO", "FOB", "1" -> "DESTINATARIO";
            case "TERCEIROS", "2" -> "TERCEIROS";
            case "SEM_FRETE", "SEM FRETE", "9" -> "SEM_FRETE";
            default -> "SEM_FRETE";
        };
    }
}
