package com.insuranceflow.tenant.service;

import com.insuranceflow.common.exception.*;
import com.insuranceflow.master.model.ConfiguracaoWhiteLabel;
import com.insuranceflow.master.repository.ConfiguracaoWhiteLabelRepository;
import com.insuranceflow.security.TenantContext;
import com.insuranceflow.tenant.dto.*;
import com.insuranceflow.tenant.model.*;
import com.insuranceflow.tenant.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final ClienteRepository clienteRepo;
    private final SeguradoraRepository seguradoraRepo;
    private final CorretoraRepository corretoraRepo;
    private final PropostaRepository propostaRepo;
    private final ApoliceRepository apoliceRepo;
    private final SinistroRepository sinistroRepo;
    private final RenovacaoRepository renovacaoRepo;
    private final LancamentoFinanceiroRepository financeiroRepo;
    private final BoletoRepository boletoRepo;
    private final ComissaoRepository comissaoRepo;
    private final NotificacaoRepository notificacaoRepo;
    private final AuditoriaRepository auditoriaRepo;
    private final ConfiguracaoWhiteLabelRepository whiteLabelRepo;
    private final MensagemWhatsappRepository whatsappRepo;
    private final AssinaturaDigitalRepository assinaturaRepo;

    private UUID getEmpresaId() {
        UUID empresaId = TenantContext.getCurrentTenant();
        if (empresaId == null) throw new BusinessException("Contexto da empresa não encontrado");
        return empresaId;
    }

    // ========== DASHBOARD ==========
    public DashboardTenantResponse getDashboard() {
        UUID eid = getEmpresaId();
        return DashboardTenantResponse.builder()
                .totalClientes(clienteRepo.countByEmpresaIdAndActiveTrue(eid))
                .totalPropostas(propostaRepo.countByEmpresaIdAndActiveTrue(eid))
                .totalApolices(apoliceRepo.countByEmpresaIdAndActiveTrue(eid))
                .apolicesAtivas(apoliceRepo.countByEmpresaIdAndActiveTrue(eid))
                .sinistrosAbertos(0)
                .renovacoesPendentes(0)
                .boletosVencidos(0)
                .valorTotalPremios(BigDecimal.ZERO)
                .comissoesPendentes(BigDecimal.ZERO)
                .leadsNovos(0)
                .build();
    }

    // ========== CLIENTES ==========
    public Page<Cliente> listarClientes(Pageable pageable) {
        return clienteRepo.findByEmpresaIdAndActiveTrue(getEmpresaId(), pageable);
    }

    public Cliente buscarCliente(UUID id) {
        return clienteRepo.findByIdAndEmpresaId(id, getEmpresaId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
    }

    @Transactional
    public Cliente criarCliente(ClienteRequest req) {
        Cliente c = new Cliente();
        c.setEmpresaId(getEmpresaId());
        c.setTipoPessoa(req.getTipoPessoa());
        c.setNome(req.getNome());
        c.setCpfCnpj(req.getCpfCnpj());
        c.setRg(req.getRg());
        c.setDataNascimento(req.getDataNascimento());
        c.setSexo(req.getSexo());
        c.setEstadoCivil(req.getEstadoCivil());
        c.setProfissao(req.getProfissao());
        c.setEmail(req.getEmail());
        c.setTelefone(req.getTelefone());
        c.setCelular(req.getCelular());
        c.setCep(req.getCep());
        c.setLogradouro(req.getLogradouro());
        c.setNumero(req.getNumero());
        c.setComplemento(req.getComplemento());
        c.setBairro(req.getBairro());
        c.setCidade(req.getCidade());
        c.setEstado(req.getEstado());
        c.setObservacoes(req.getObservacoes());
        c.setOrigem(req.getOrigem());
        return clienteRepo.save(c);
    }

    @Transactional
    public Cliente atualizarCliente(UUID id, ClienteRequest req) {
        Cliente c = buscarCliente(id);
        c.setTipoPessoa(req.getTipoPessoa());
        c.setNome(req.getNome());
        c.setCpfCnpj(req.getCpfCnpj());
        c.setRg(req.getRg());
        c.setDataNascimento(req.getDataNascimento());
        c.setSexo(req.getSexo());
        c.setEstadoCivil(req.getEstadoCivil());
        c.setProfissao(req.getProfissao());
        c.setEmail(req.getEmail());
        c.setTelefone(req.getTelefone());
        c.setCelular(req.getCelular());
        c.setCep(req.getCep());
        c.setLogradouro(req.getLogradouro());
        c.setNumero(req.getNumero());
        c.setComplemento(req.getComplemento());
        c.setBairro(req.getBairro());
        c.setCidade(req.getCidade());
        c.setEstado(req.getEstado());
        c.setObservacoes(req.getObservacoes());
        c.setOrigem(req.getOrigem());
        return clienteRepo.save(c);
    }

    // ========== SEGURADORAS ==========
    public Page<Seguradora> listarSeguradoras(Pageable pageable) {
        return seguradoraRepo.findByEmpresaIdAndActiveTrue(getEmpresaId(), pageable);
    }

    // ========== CORRETORAS ==========
    public Page<Corretora> listarCorretoras(Pageable pageable) {
        return corretoraRepo.findByEmpresaIdAndActiveTrue(getEmpresaId(), pageable);
    }

    // ========== PROPOSTAS ==========
    public Page<Proposta> listarPropostas(Pageable pageable) {
        return propostaRepo.findByEmpresaIdAndActiveTrue(getEmpresaId(), pageable);
    }

    public Proposta buscarProposta(UUID id) {
        return propostaRepo.findByIdAndEmpresaId(id, getEmpresaId())
                .orElseThrow(() -> new ResourceNotFoundException("Proposta", id));
    }

    @Transactional
    public Proposta criarProposta(PropostaRequest req) {
        Proposta p = new Proposta();
        p.setEmpresaId(getEmpresaId());
        p.setClienteId(req.getClienteId());
        p.setSeguradoraId(req.getSeguradoraId());
        p.setCorretoraId(req.getCorretoraId());
        p.setRamoId(req.getRamoId());
        p.setTipoSeguro(req.getTipoSeguro());
        p.setStatus("RASCUNHO");
        p.setDataProposta(LocalDate.now());
        p.setDataInicioVigencia(req.getDataInicioVigencia());
        p.setDataFimVigencia(req.getDataFimVigencia());
        p.setValorImportanciaSegurada(req.getValorImportanciaSegurada());
        p.setValorPremio(req.getValorPremio());
        p.setValorPremioLiquido(req.getValorPremioLiquido());
        p.setValorIof(req.getValorIof());
        p.setFormaPagamento(req.getFormaPagamento());
        p.setNumeroParcelas(req.getNumeroParcelas());
        p.setObservacoes(req.getObservacoes());
        p.setNumero("PROP-" + java.time.Year.now().getValue() + "-" + String.format("%04d", propostaRepo.countByEmpresaIdAndActiveTrue(getEmpresaId()) + 1));
        return propostaRepo.save(p);
    }

    // ========== APÓLICES ==========
    public Page<Apolice> listarApolices(Pageable pageable) {
        return apoliceRepo.findByEmpresaIdAndActiveTrue(getEmpresaId(), pageable);
    }

    public Apolice buscarApolice(UUID id) {
        return apoliceRepo.findByIdAndEmpresaId(id, getEmpresaId())
                .orElseThrow(() -> new ResourceNotFoundException("Apólice", id));
    }

    // ========== SINISTROS ==========
    public Page<Sinistro> listarSinistros(Pageable pageable) {
        return sinistroRepo.findByEmpresaIdAndActiveTrue(getEmpresaId(), pageable);
    }

    @Transactional
    public Sinistro criarSinistro(SinistroRequest req) {
        Sinistro s = new Sinistro();
        s.setEmpresaId(getEmpresaId());
        s.setApoliceId(req.getApoliceId());
        s.setClienteId(req.getClienteId());
        s.setDataOcorrencia(req.getDataOcorrencia());
        s.setDataAviso(req.getDataAviso() != null ? req.getDataAviso() : LocalDate.now());
        s.setTipo(req.getTipo());
        s.setDescricao(req.getDescricao());
        s.setStatus("ABERTO");
        s.setValorEstimado(req.getValorEstimado());
        s.setLocalOcorrencia(req.getLocalOcorrencia());
        s.setBoletimOcorrencia(req.getBoletimOcorrencia());
        s.setObservacoes(req.getObservacoes());
        s.setNumeroSinistro("SIN-" + java.time.Year.now().getValue() + "-" + String.format("%04d", sinistroRepo.findByEmpresaIdAndActiveTrue(getEmpresaId(), Pageable.unpaged()).getTotalElements() + 1));
        return sinistroRepo.save(s);
    }

    // ========== RENOVAÇÕES ==========
    public Page<Renovacao> listarRenovacoes(Pageable pageable) {
        return renovacaoRepo.findByEmpresaIdAndActiveTrue(getEmpresaId(), pageable);
    }

    // ========== FINANCEIRO ==========
    public Page<LancamentoFinanceiro> listarFinanceiro(Pageable pageable) {
        return financeiroRepo.findByEmpresaIdAndActiveTrue(getEmpresaId(), pageable);
    }

    @Transactional
    public LancamentoFinanceiro criarLancamento(LancamentoFinanceiroRequest req) {
        LancamentoFinanceiro l = new LancamentoFinanceiro();
        l.setEmpresaId(getEmpresaId());
        l.setTipo(req.getTipo());
        l.setCategoria(req.getCategoria());
        l.setDescricao(req.getDescricao());
        l.setValor(req.getValor());
        l.setDataVencimento(req.getDataVencimento());
        l.setDataPagamento(req.getDataPagamento());
        l.setStatus(req.getDataPagamento() != null ? "PAGO" : "PENDENTE");
        l.setFormaPagamento(req.getFormaPagamento());
        l.setClienteId(req.getClienteId());
        l.setApoliceId(req.getApoliceId());
        l.setPropostaId(req.getPropostaId());
        l.setObservacoes(req.getObservacoes());
        return financeiroRepo.save(l);
    }

    // ========== BOLETOS ==========
    public Page<Boleto> listarBoletos(Pageable pageable) {
        return boletoRepo.findByEmpresaIdAndActiveTrue(getEmpresaId(), pageable);
    }

    @Transactional
    public Boleto criarBoleto(BoletoRequest req) {
        Boleto b = new Boleto();
        b.setEmpresaId(getEmpresaId());
        b.setLancamentoId(req.getLancamentoId());
        b.setApoliceId(req.getApoliceId());
        b.setClienteId(req.getClienteId());
        b.setNumeroParcela(req.getNumeroParcela());
        b.setValor(req.getValor());
        b.setDataVencimento(req.getDataVencimento());
        b.setStatus("ABERTO");
        b.setLinhaDigitavel(req.getLinhaDigitavel());
        b.setCodigoBarras(req.getCodigoBarras());
        b.setUrlBoleto(req.getUrlBoleto());
        b.setNossoNumero(req.getNossoNumero());
        b.setObservacoes(req.getObservacoes());
        return boletoRepo.save(b);
    }

    // ========== COMISSÕES ==========
    public Page<Comissao> listarComissoes(Pageable pageable) {
        return comissaoRepo.findByEmpresaIdAndActiveTrue(getEmpresaId(), pageable);
    }

    @Transactional
    public Comissao criarComissao(ComissaoRequest req) {
        Comissao c = new Comissao();
        c.setEmpresaId(getEmpresaId());
        c.setApoliceId(req.getApoliceId());
        c.setCorretoraId(req.getCorretoraId());
        c.setSeguradoraId(req.getSeguradoraId());
        c.setTipo(req.getTipo());
        c.setPercentual(req.getPercentual());
        c.setValor(req.getValor());
        c.setDataReferencia(req.getDataReferencia() != null ? req.getDataReferencia() : LocalDate.now());
        c.setStatus("PENDENTE");
        c.setObservacoes(req.getObservacoes());
        return comissaoRepo.save(c);
    }

    // ========== AUDITORIA ==========
    public Page<Auditoria> listarAuditoria(Pageable pageable) {
        return auditoriaRepo.findByEmpresaIdOrderByCreatedAtDesc(getEmpresaId(), pageable);
    }

    // ========== WHITE-LABEL ==========
    public ConfiguracaoWhiteLabel getWhiteLabel() {
        return whiteLabelRepo.findByEmpresaId(getEmpresaId())
                .orElseGet(() -> {
                    ConfiguracaoWhiteLabel wl = new ConfiguracaoWhiteLabel();
                    wl.setEmpresaId(getEmpresaId());
                    wl.setNomeSistema("InsuranceFlow");
                    wl.setCorPrimaria("#4F46E5");
                    wl.setCorSecundaria("#7C3AED");
                    wl.setCorAcento("#10B981");
                    wl.setCorFundo("#F8FAFC");
                    wl.setNomePortalCliente("Portal do Segurado");
                    return wl;
                });
    }

    @Transactional
    public ConfiguracaoWhiteLabel atualizarWhiteLabel(WhiteLabelRequest req) {
        UUID eid = getEmpresaId();
        ConfiguracaoWhiteLabel wl = whiteLabelRepo.findByEmpresaId(eid)
                .orElseGet(() -> {
                    ConfiguracaoWhiteLabel novo = new ConfiguracaoWhiteLabel();
                    novo.setEmpresaId(eid);
                    return novo;
                });
        wl.setLogoUrl(req.getLogoUrl());
        wl.setFaviconUrl(req.getFaviconUrl());
        wl.setNomeSistema(req.getNomeSistema());
        wl.setCorPrimaria(req.getCorPrimaria());
        wl.setCorSecundaria(req.getCorSecundaria());
        wl.setCorAcento(req.getCorAcento());
        wl.setCorFundo(req.getCorFundo());
        wl.setNomePortalCliente(req.getNomePortalCliente());
        wl.setRodape(req.getRodape());
        wl.setEmailSuporte(req.getEmailSuporte());
        wl.setTelefoneSuporte(req.getTelefoneSuporte());
        wl.setTextoInstitucional(req.getTextoInstitucional());
        wl.setDominioPersonalizado(req.getDominioPersonalizado());
        wl.setSubdominio(req.getSubdominio());
        return whiteLabelRepo.save(wl);
    }

    // ========== NOTIFICAÇÕES ==========
    public Page<Notificacao> listarNotificacoes(UUID usuarioId, Pageable pageable) {
        return notificacaoRepo.findByUsuarioIdAndActiveTrue(usuarioId, pageable);
    }

    public long contarNotificacoesNaoLidas(UUID usuarioId) {
        return notificacaoRepo.countByUsuarioIdAndLidaFalse(usuarioId);
    }

    @Transactional
    public Notificacao criarNotificacao(NotificacaoRequest req) {
        Notificacao n = Notificacao.builder()
                .empresaId(getEmpresaId())
                .usuarioId(req.getUsuarioId())
                .titulo(req.getTitulo())
                .mensagem(req.getMensagem())
                .tipo(req.getTipo() != null ? req.getTipo() : "INFO")
                .lida(false)
                .link(req.getLink())
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
        return notificacaoRepo.save(n);
    }

    @Transactional
    public Notificacao marcarNotificacaoLida(UUID id) {
        Notificacao n = notificacaoRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação", id));
        n.setLida(true);
        return notificacaoRepo.save(n);
    }

    // ========== WHATSAPP ==========
    public Page<MensagemWhatsapp> listarWhatsapp(Pageable pageable) {
        return whatsappRepo.findByEmpresaIdAndActiveTrue(getEmpresaId(), pageable);
    }

    @Transactional
    public MensagemWhatsapp enviarWhatsapp(WhatsappRequest req) {
        MensagemWhatsapp m = new MensagemWhatsapp();
        m.setEmpresaId(getEmpresaId());
        m.setClienteId(req.getClienteId());
        m.setTelefoneDestino(req.getTelefoneDestino());
        m.setMensagem(req.getMensagem());
        m.setTipo(req.getTipo() != null ? req.getTipo() : "MANUAL");
        m.setStatus("ENVIADO");
        m.setDataEnvio(LocalDateTime.now());
        return whatsappRepo.save(m);
    }

    // ========== ASSINATURA DIGITAL ==========
    public Page<AssinaturaDigital> listarAssinaturas(Pageable pageable) {
        return assinaturaRepo.findByEmpresaIdAndActiveTrue(getEmpresaId(), pageable);
    }

    @Transactional
    public AssinaturaDigital solicitarAssinatura(AssinaturaDigitalRequest req) {
        AssinaturaDigital a = new AssinaturaDigital();
        a.setEmpresaId(getEmpresaId());
        a.setDocumentoId(req.getDocumentoId());
        a.setEntidadeTipo(req.getEntidadeTipo());
        a.setEntidadeId(req.getEntidadeId());
        a.setSignatarioNome(req.getSignatarioNome());
        a.setSignatarioEmail(req.getSignatarioEmail());
        a.setSignatarioCpf(req.getSignatarioCpf());
        a.setStatus("PENDENTE");
        return assinaturaRepo.save(a);
    }

    @Transactional
    public AssinaturaDigital assinarDocumento(UUID id, String ip) {
        AssinaturaDigital a = assinaturaRepo.findByIdAndEmpresaId(id, getEmpresaId())
                .orElseThrow(() -> new ResourceNotFoundException("Assinatura", id));
        a.setStatus("ASSINADO");
        a.setDataAssinatura(LocalDateTime.now());
        a.setIpAssinatura(ip);
        return assinaturaRepo.save(a);
    }
}
