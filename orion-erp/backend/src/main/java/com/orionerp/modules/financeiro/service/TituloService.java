package com.orionerp.modules.financeiro.service;

import com.orionerp.common.PageResponse;
import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.cadastros.domain.CentroCusto;
import com.orionerp.modules.cadastros.domain.Cliente;
import com.orionerp.modules.cadastros.domain.ContaBancaria;
import com.orionerp.modules.cadastros.domain.Fornecedor;
import com.orionerp.modules.cadastros.domain.NaturezaFinanceira;
import com.orionerp.modules.cadastros.repository.CentroCustoRepository;
import com.orionerp.modules.cadastros.repository.ClienteRepository;
import com.orionerp.modules.cadastros.repository.ContaBancariaRepository;
import com.orionerp.modules.cadastros.repository.FornecedorRepository;
import com.orionerp.modules.cadastros.repository.NaturezaFinanceiraRepository;
import com.orionerp.modules.financeiro.domain.FluxoCaixa;
import com.orionerp.modules.financeiro.domain.Titulo;
import com.orionerp.modules.financeiro.domain.TituloBaixa;
import com.orionerp.modules.financeiro.domain.TituloParcela;
import com.orionerp.modules.financeiro.dto.BaixaRequest;
import com.orionerp.modules.financeiro.dto.BaixaResponse;
import com.orionerp.modules.financeiro.dto.TituloRequest;
import com.orionerp.modules.financeiro.dto.TituloResponse;
import com.orionerp.modules.financeiro.repository.FluxoCaixaRepository;
import com.orionerp.modules.financeiro.repository.TituloBaixaRepository;
import com.orionerp.modules.financeiro.repository.TituloParcelaRepository;
import com.orionerp.modules.financeiro.repository.TituloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TituloService {

    private final TituloRepository tituloRepository;
    private final TituloParcelaRepository parcelaRepository;
    private final TituloBaixaRepository baixaRepository;
    private final FluxoCaixaRepository fluxoCaixaRepository;
    private final ClienteRepository clienteRepository;
    private final FornecedorRepository fornecedorRepository;
    private final NaturezaFinanceiraRepository naturezaRepository;
    private final CentroCustoRepository centroCustoRepository;
    private final ContaBancariaRepository contaBancariaRepository;

    @Transactional(readOnly = true)
    public PageResponse<TituloResponse> list(Long empresaId, Long filialId, String tipo, String status,
                                             Long clienteId, Long fornecedorId,
                                             LocalDate dataInicio, LocalDate dataFim,
                                             String term, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataEmissao"));
        var spec = FinanceiroSpecifications.tituloFilter(empresaId, filialId, tipo, status,
                clienteId, fornecedorId, dataInicio, dataFim, term);
        var result = tituloRepository.findAll(spec, pageable).map(this::toResponse);
        return PageResponse.from(result);
    }

    @Transactional(readOnly = true)
    public TituloResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public TituloResponse create(TituloRequest request) {
        validateUniqueNumero(request.empresaId(), request.filialId(), request.numero(), request.tipo());

        Titulo titulo = new Titulo();
        titulo.setEmpresaId(request.empresaId());
        titulo.setFilialId(request.filialId());
        titulo.setTipo(request.tipo().trim().toUpperCase());
        titulo.setNumero(request.numero().trim());
        titulo.setSerie(request.serie());
        titulo.setDocumentoOrigem(request.documentoOrigem());
        titulo.setDocumentoOrigemId(request.documentoOrigemId());
        titulo.setDataEmissao(request.dataEmissao());
        titulo.setValorOriginal(request.valorOriginal());
        titulo.setValorAberto(request.valorOriginal());
        titulo.setObservacao(request.observacao());

        loadRelations(titulo, request);

        if (request.parcelas() != null && !request.parcelas().isEmpty()) {
            for (TituloRequest.ParcelaInput pi : request.parcelas()) {
                TituloParcela parcela = new TituloParcela();
                parcela.setNumeroParcela(pi.numeroParcela());
                parcela.setDataVencimento(pi.dataVencimento());
                parcela.setValor(pi.valor());
                titulo.addParcela(parcela);
            }
        } else {
            TituloParcela parcela = new TituloParcela();
            parcela.setNumeroParcela(1);
            parcela.setDataVencimento(request.dataEmissao());
            parcela.setValor(request.valorOriginal());
            titulo.addParcela(parcela);
        }

        return toResponse(tituloRepository.save(titulo));
    }

    @Transactional
    public TituloResponse update(Long id, TituloRequest request) {
        Titulo titulo = findById(id);
        if (!"ABERTO".equals(titulo.getStatus())) {
            throw new BusinessException("Somente titulos com status ABERTO podem ser alterados");
        }

        titulo.setTipo(request.tipo().trim().toUpperCase());
        titulo.setNumero(request.numero().trim());
        titulo.setSerie(request.serie());
        titulo.setDocumentoOrigem(request.documentoOrigem());
        titulo.setDocumentoOrigemId(request.documentoOrigemId());
        titulo.setDataEmissao(request.dataEmissao());
        titulo.setValorOriginal(request.valorOriginal());
        titulo.setObservacao(request.observacao());

        loadRelations(titulo, request);

        titulo.getParcelas().clear();
        if (request.parcelas() != null && !request.parcelas().isEmpty()) {
            for (TituloRequest.ParcelaInput pi : request.parcelas()) {
                TituloParcela parcela = new TituloParcela();
                parcela.setNumeroParcela(pi.numeroParcela());
                parcela.setDataVencimento(pi.dataVencimento());
                parcela.setValor(pi.valor());
                titulo.addParcela(parcela);
            }
        }
        titulo.recalcularAberto();

        return toResponse(tituloRepository.save(titulo));
    }

    @Transactional
    public void delete(Long id) {
        Titulo titulo = findById(id);
        if (!"ABERTO".equals(titulo.getStatus())) {
            throw new BusinessException("Somente titulos com status ABERTO podem ser excluidos");
        }
        titulo.softDelete();
        tituloRepository.save(titulo);
    }

    @Transactional
    public BaixaResponse baixar(Long tituloId, Long parcelaId, BaixaRequest request, String username) {
        Titulo titulo = findById(tituloId);
        TituloParcela parcela = parcelaRepository.findById(parcelaId)
                .filter(p -> p.getTitulo().getId().equals(tituloId))
                .orElseThrow(() -> new ResourceNotFoundException("Parcela nao encontrada"));

        if ("QUITADO".equals(parcela.getStatus()) || "CANCELADO".equals(parcela.getStatus())) {
            throw new BusinessException("Parcela ja esta quitada ou cancelada");
        }

        BigDecimal saldoAberto = parcela.getSaldoAberto();
        if (request.valorPago().compareTo(saldoAberto) > 0) {
            throw new BusinessException("Valor pago nao pode ser maior que o saldo aberto da parcela");
        }

        TituloBaixa baixa = new TituloBaixa();
        baixa.setParcela(parcela);
        baixa.setDataBaixa(request.dataBaixa());
        baixa.setValorPago(request.valorPago());
        baixa.setValorJuros(request.valorJuros() != null ? request.valorJuros() : BigDecimal.ZERO);
        baixa.setValorMulta(request.valorMulta() != null ? request.valorMulta() : BigDecimal.ZERO);
        baixa.setValorDesconto(request.valorDesconto() != null ? request.valorDesconto() : BigDecimal.ZERO);
        baixa.setFormaPagamento(request.formaPagamento());
        baixa.setObservacao(request.observacao());

        if (request.contaBancariaId() != null) {
            baixa.setContaBancaria(contaBancariaRepository.findByIdAndDeletedFalse(request.contaBancariaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Conta bancaria nao encontrada")));
        }

        baixa = baixaRepository.save(baixa);

        parcela.setValorPago(parcela.getValorPago().add(request.valorPago()));
        parcela.setValorJuros(parcela.getValorJuros().add(baixa.getValorJuros()));
        parcela.setValorMulta(parcela.getValorMulta().add(baixa.getValorMulta()));
        parcela.setValorDesconto(parcela.getValorDesconto().add(baixa.getValorDesconto()));

        if (parcela.getSaldoAberto().compareTo(BigDecimal.ZERO) <= 0) {
            parcela.setStatus("QUITADO");
            parcela.setDataPagamento(request.dataBaixa());
        } else {
            parcela.setStatus("PARCIAL");
        }
        parcela.setUpdatedAt(LocalDateTime.now());
        parcelaRepository.save(parcela);

        titulo.recalcularAberto();
        tituloRepository.save(titulo);

        gerarFluxoCaixa(titulo, baixa);

        return toBaixaResponse(baixa);
    }

    @Transactional
    public BaixaResponse estornar(Long tituloId, Long parcelaId, Long baixaId, String username) {
        Titulo titulo = findById(tituloId);
        TituloBaixa baixa = baixaRepository.findById(baixaId)
                .filter(b -> b.getParcela().getId().equals(parcelaId)
                        && b.getParcela().getTitulo().getId().equals(tituloId))
                .orElseThrow(() -> new ResourceNotFoundException("Baixa nao encontrada"));

        if (baixa.getEstornado()) {
            throw new BusinessException("Baixa ja foi estornada");
        }

        baixa.setEstornado(true);
        baixa.setEstornadoEm(LocalDateTime.now());
        baixa.setEstornadoPor(username);
        baixaRepository.save(baixa);

        TituloParcela parcela = baixa.getParcela();
        parcela.setValorPago(parcela.getValorPago().subtract(baixa.getValorPago()));
        parcela.setValorJuros(parcela.getValorJuros().subtract(baixa.getValorJuros()));
        parcela.setValorMulta(parcela.getValorMulta().subtract(baixa.getValorMulta()));
        parcela.setValorDesconto(parcela.getValorDesconto().subtract(baixa.getValorDesconto()));
        parcela.setStatus(parcela.getSaldoAberto().compareTo(BigDecimal.ZERO) > 0 ? "ABERTO" : "QUITADO");
        parcela.setDataPagamento("QUITADO".equals(parcela.getStatus()) ? parcela.getDataPagamento() : null);
        parcela.setUpdatedAt(LocalDateTime.now());
        parcelaRepository.save(parcela);

        titulo.recalcularAberto();
        tituloRepository.save(titulo);

        gerarFluxoCaixaEstorno(titulo, baixa);

        return toBaixaResponse(baixa);
    }

    @Transactional(readOnly = true)
    public List<BaixaResponse> listarBaixas(Long tituloId, Long parcelaId) {
        findById(tituloId);
        return baixaRepository.findByParcelaIdAndEstornadoFalse(parcelaId).stream()
                .map(this::toBaixaResponse)
                .toList();
    }

    // --- private helpers ---

    private void validateUniqueNumero(Long empresaId, Long filialId, String numero, String tipo) {
        if (tituloRepository.existsByEmpresaIdAndFilialIdAndNumeroAndTipoAndDeletedFalse(
                empresaId, filialId, numero, tipo.trim().toUpperCase())) {
            throw new BusinessException("Ja existe titulo com este numero para o tipo informado");
        }
    }

    private void loadRelations(Titulo titulo, TituloRequest request) {
        if (request.clienteId() != null) {
            titulo.setCliente(clienteRepository.findByIdAndDeletedFalse(request.clienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente nao encontrado")));
        } else {
            titulo.setCliente(null);
        }
        if (request.fornecedorId() != null) {
            titulo.setFornecedor(fornecedorRepository.findByIdAndDeletedFalse(request.fornecedorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Fornecedor nao encontrado")));
        } else {
            titulo.setFornecedor(null);
        }
        if (request.naturezaFinanceiraId() != null) {
            titulo.setNaturezaFinanceira(naturezaRepository.findByIdAndDeletedFalse(request.naturezaFinanceiraId())
                    .orElseThrow(() -> new ResourceNotFoundException("Natureza financeira nao encontrada")));
        } else {
            titulo.setNaturezaFinanceira(null);
        }
        if (request.centroCustoId() != null) {
            titulo.setCentroCusto(centroCustoRepository.findByIdAndDeletedFalse(request.centroCustoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Centro de custo nao encontrado")));
        } else {
            titulo.setCentroCusto(null);
        }
        if (request.contaBancariaId() != null) {
            titulo.setContaBancaria(contaBancariaRepository.findByIdAndDeletedFalse(request.contaBancariaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Conta bancaria nao encontrada")));
        } else {
            titulo.setContaBancaria(null);
        }
    }

    private void gerarFluxoCaixa(Titulo titulo, TituloBaixa baixa) {
        if (baixa.getContaBancaria() == null) return;

        FluxoCaixa fc = new FluxoCaixa();
        fc.setEmpresaId(titulo.getEmpresaId());
        fc.setFilialId(titulo.getFilialId());
        fc.setContaBancaria(baixa.getContaBancaria());
        fc.setTipo("RECEBER".equals(titulo.getTipo()) ? "ENTRADA" : "SAIDA");
        fc.setValor(baixa.getValorPago());
        fc.setDataLancamento(baixa.getDataBaixa());
        fc.setDescricao("Baixa titulo " + titulo.getNumero() + " parcela " + baixa.getParcela().getNumeroParcela());
        fc.setTituloId(titulo.getId());
        fc.setBaixaId(baixa.getId());
        fluxoCaixaRepository.save(fc);
    }

    private void gerarFluxoCaixaEstorno(Titulo titulo, TituloBaixa baixa) {
        if (baixa.getContaBancaria() == null) return;

        FluxoCaixa fc = new FluxoCaixa();
        fc.setEmpresaId(titulo.getEmpresaId());
        fc.setFilialId(titulo.getFilialId());
        fc.setContaBancaria(baixa.getContaBancaria());
        fc.setTipo("RECEBER".equals(titulo.getTipo()) ? "SAIDA" : "ENTRADA");
        fc.setValor(baixa.getValorPago());
        fc.setDataLancamento(LocalDate.now());
        fc.setDescricao("Estorno titulo " + titulo.getNumero() + " parcela " + baixa.getParcela().getNumeroParcela());
        fc.setTituloId(titulo.getId());
        fc.setBaixaId(baixa.getId());
        fluxoCaixaRepository.save(fc);
    }

    private Titulo findById(Long id) {
        return tituloRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Titulo nao encontrado"));
    }

    private TituloResponse toResponse(Titulo t) {
        List<TituloResponse.ParcelaResponse> parcelas = t.getParcelas().stream()
                .map(p -> new TituloResponse.ParcelaResponse(
                        p.getId(), p.getUuid(), p.getNumeroParcela(), p.getDataVencimento(),
                        p.getValor(), p.getValorPago(), p.getValorJuros(), p.getValorMulta(),
                        p.getValorDesconto(), p.getStatus(), p.getDataPagamento()))
                .toList();

        return new TituloResponse(
                t.getId(), t.getUuid(), t.getEmpresaId(), t.getFilialId(),
                t.getTipo(), t.getNumero(), t.getSerie(),
                t.getCliente() != null ? t.getCliente().getId() : null,
                t.getCliente() != null ? t.getCliente().getRazaoSocial() : null,
                t.getFornecedor() != null ? t.getFornecedor().getId() : null,
                t.getFornecedor() != null ? t.getFornecedor().getRazaoSocial() : null,
                t.getNaturezaFinanceira() != null ? t.getNaturezaFinanceira().getId() : null,
                t.getNaturezaFinanceira() != null ? t.getNaturezaFinanceira().getNome() : null,
                t.getCentroCusto() != null ? t.getCentroCusto().getId() : null,
                t.getCentroCusto() != null ? t.getCentroCusto().getNome() : null,
                t.getContaBancaria() != null ? t.getContaBancaria().getId() : null,
                t.getDocumentoOrigem(),
                t.getDataEmissao(), t.getValorOriginal(), t.getValorAberto(),
                t.getStatus(), t.getObservacao(), parcelas);
    }

    private BaixaResponse toBaixaResponse(TituloBaixa b) {
        return new BaixaResponse(
                b.getId(), b.getUuid(), b.getParcela().getId(),
                b.getDataBaixa(), b.getValorPago(), b.getValorJuros(),
                b.getValorMulta(), b.getValorDesconto(),
                b.getContaBancaria() != null ? b.getContaBancaria().getId() : null,
                b.getFormaPagamento(), b.getObservacao(),
                b.getEstornado(), b.getEstornadoEm(), b.getEstornadoPor(),
                b.getCreatedAt());
    }
}
