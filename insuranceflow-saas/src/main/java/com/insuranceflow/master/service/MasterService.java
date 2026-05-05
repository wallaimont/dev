package com.insuranceflow.master.service;

import com.insuranceflow.auth.model.Perfil;
import com.insuranceflow.auth.model.Usuario;
import com.insuranceflow.auth.repository.UsuarioRepository;
import com.insuranceflow.common.exception.BusinessException;
import com.insuranceflow.master.dto.*;
import com.insuranceflow.master.model.*;
import com.insuranceflow.master.repository.*;
import com.insuranceflow.tenant.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MasterService {

    private final EmpresaRepository empresaRepo;
    private final PlanoRepository planoRepo;
    private final AssinaturaEmpresaRepository assinaturaRepo;
    private final PagamentoAssinaturaRepository pagamentoRepo;
    private final LeadSaasRepository leadRepo;
    private final TicketSuporteRepository ticketRepo;
    private final UsuarioRepository usuarioRepo;
    private final PropostaRepository propostaRepo;
    private final ApoliceRepository apoliceRepo;
    private final ConfiguracaoWhiteLabelRepository whiteLabelRepo;
    private final PasswordEncoder passwordEncoder;

    public DashboardMasterResponse getDashboard() {
        BigDecimal mrrMensal = assinaturaRepo.calcularMRR();
        BigDecimal mrrAnual = assinaturaRepo.calcularMRRAnual();
        BigDecimal mrr = mrrMensal.add(mrrAnual);

        return DashboardMasterResponse.builder()
                .totalEmpresas(empresaRepo.countByActiveTrue())
                .empresasAtivas(empresaRepo.countAtivas())
                .empresasTrial(empresaRepo.countTrialAtivas())
                .empresasSuspensas(empresaRepo.countByStatus("SUSPENSA"))
                .empresasCanceladas(empresaRepo.countByStatus("CANCELADA"))
                .mrr(mrr)
                .arr(mrr.multiply(BigDecimal.valueOf(12)))
                .totalUsuarios(usuarioRepo.countByActiveTrue())
                .totalPropostas(propostaRepo.countByActiveTrue())
                .totalApolices(apoliceRepo.countByActiveTrue())
                .ticketsAbertos(ticketRepo.countByStatus("ABERTO"))
                .leadsNovos(leadRepo.countByStatus("NOVO"))
                .pagamentosPendentes(pagamentoRepo.countByStatus("PENDENTE"))
                .build();
    }

    // ========== PLANOS ==========
    public Page<Plano> listarPlanos(Pageable pageable) {
        return planoRepo.findAll(pageable);
    }

    public Plano buscarPlano(UUID id) {
        return planoRepo.findById(id).orElseThrow(() -> new BusinessException("Plano não encontrado"));
    }

    @Transactional
    public Plano criarPlano(PlanoRequest req) {
        Plano p = new Plano();
        p.setNome(req.getNome());
        p.setDescricao(req.getDescricao());
        p.setPrecoMensal(req.getPrecoMensal());
        p.setPrecoAnual(req.getPrecoAnual());
        p.setLimiteUsuarios(req.getLimiteUsuarios());
        p.setLimiteClientes(req.getLimiteClientes());
        p.setLimitePropostasMes(req.getLimitePropostasMes());
        p.setLimiteApolices(req.getLimiteApolices());
        p.setLimiteArmazenamentoGb(req.getLimiteArmazenamentoGb());
        p.setPortalCliente(req.getPortalCliente());
        p.setWhatsappIntegrado(req.getWhatsappIntegrado());
        p.setRelatoriosAvancados(req.getRelatoriosAvancados());
        p.setWhiteLabel(req.getWhiteLabel());
        p.setAcessoApi(req.getAcessoApi());
        p.setSuportePrioritario(req.getSuportePrioritario());
        return planoRepo.save(p);
    }

    @Transactional
    public Plano atualizarPlano(UUID id, PlanoRequest req) {
        Plano p = buscarPlano(id);
        p.setNome(req.getNome());
        p.setDescricao(req.getDescricao());
        p.setPrecoMensal(req.getPrecoMensal());
        p.setPrecoAnual(req.getPrecoAnual());
        p.setLimiteUsuarios(req.getLimiteUsuarios());
        p.setLimiteClientes(req.getLimiteClientes());
        p.setLimitePropostasMes(req.getLimitePropostasMes());
        p.setLimiteApolices(req.getLimiteApolices());
        p.setLimiteArmazenamentoGb(req.getLimiteArmazenamentoGb());
        p.setPortalCliente(req.getPortalCliente());
        p.setWhatsappIntegrado(req.getWhatsappIntegrado());
        p.setRelatoriosAvancados(req.getRelatoriosAvancados());
        p.setWhiteLabel(req.getWhiteLabel());
        p.setAcessoApi(req.getAcessoApi());
        p.setSuportePrioritario(req.getSuportePrioritario());
        return planoRepo.save(p);
    }

    // ========== EMPRESAS ==========
    public Page<Empresa> listarEmpresas(Pageable pageable) {
        return empresaRepo.findByActiveTrue(pageable);
    }

    public Empresa buscarEmpresa(UUID id) {
        return empresaRepo.findById(id).orElseThrow(() -> new BusinessException("Empresa não encontrada"));
    }

    @Transactional
    public Empresa criarEmpresa(EmpresaRequest req) {
        if (empresaRepo.existsByCnpj(req.getCnpj())) {
            throw new BusinessException("CNPJ já cadastrado");
        }

        Empresa e = new Empresa();
        e.setRazaoSocial(req.getRazaoSocial());
        e.setNomeFantasia(req.getNomeFantasia());
        e.setCnpj(req.getCnpj());
        e.setInscricaoEstadual(req.getInscricaoEstadual());
        e.setEmail(req.getEmail());
        e.setTelefone(req.getTelefone());
        e.setCelular(req.getCelular());
        e.setWebsite(req.getWebsite());
        e.setCep(req.getCep());
        e.setLogradouro(req.getLogradouro());
        e.setNumero(req.getNumero());
        e.setComplemento(req.getComplemento());
        e.setBairro(req.getBairro());
        e.setCidade(req.getCidade());
        e.setEstado(req.getEstado());
        e.setPlanoId(req.getPlanoId());
        e.setStatus("TRIAL");
        e.setDataInicioTrial(LocalDateTime.now());
        e.setDataFimTrial(LocalDateTime.now().plusDays(14));
        e.setSlug(req.getNomeFantasia().toLowerCase().replaceAll("[^a-z0-9]", "-").replaceAll("-+", "-"));
        e.setBloqueada(false);
        return empresaRepo.save(e);
    }

    @Transactional
    public void bloquearEmpresa(UUID id, String motivo) {
        Empresa e = buscarEmpresa(id);
        e.setBloqueada(true);
        e.setMotivoBloqueio(motivo);
        e.setStatus("BLOQUEADA");
        empresaRepo.save(e);
    }

    @Transactional
    public void desbloquearEmpresa(UUID id) {
        Empresa e = buscarEmpresa(id);
        e.setBloqueada(false);
        e.setMotivoBloqueio(null);
        e.setStatus("ATIVA");
        empresaRepo.save(e);
    }

    // ========== ASSINATURAS ==========
    public Page<AssinaturaEmpresa> listarAssinaturas(Pageable pageable) {
        return assinaturaRepo.findByActiveTrue(pageable);
    }

    // ========== LEADS ==========
    @Transactional
    public LeadSaas criarLead(LeadSaasRequest req) {
        LeadSaas lead = new LeadSaas();
        lead.setNome(req.getNome());
        lead.setEmpresaNome(req.getEmpresaNome());
        lead.setEmail(req.getEmail());
        lead.setTelefone(req.getTelefone());
        lead.setOrigem(req.getOrigem());
        lead.setInteresse(req.getInteresse());
        lead.setObservacoes(req.getObservacoes());
        lead.setStatus("NOVO");
        return leadRepo.save(lead);
    }

    public Page<LeadSaas> listarLeads(Pageable pageable) {
        return leadRepo.findByActiveTrue(pageable);
    }

    // ========== TICKETS ==========
    public Page<TicketSuporte> listarTickets(Pageable pageable) {
        return ticketRepo.findByActiveTrue(pageable);
    }

    public Page<TicketSuporte> listarTicketsEmpresa(UUID empresaId, Pageable pageable) {
        return ticketRepo.findByEmpresaIdAndActiveTrue(empresaId, pageable);
    }

    // ========== ONBOARDING ==========
    @Transactional
    public Empresa onboarding(OnboardingRequest req) {
        if (empresaRepo.existsByCnpj(req.getCnpj())) {
            throw new BusinessException("CNPJ já cadastrado na plataforma");
        }
        if (usuarioRepo.existsByEmail(req.getEmailAdmin())) {
            throw new BusinessException("Email já cadastrado na plataforma");
        }

        // Buscar plano (default: Starter)
        Plano plano = planoRepo.findByActiveTrue().stream()
                .filter(p -> p.getNome().equalsIgnoreCase(
                        req.getPlanoEscolhido() != null ? req.getPlanoEscolhido() : "Starter"))
                .findFirst()
                .orElse(planoRepo.findByActiveTrue().get(0));

        // Criar empresa
        Empresa empresa = new Empresa();
        empresa.setRazaoSocial(req.getRazaoSocial());
        empresa.setNomeFantasia(req.getNomeFantasia());
        empresa.setCnpj(req.getCnpj());
        empresa.setEmail(req.getEmail());
        empresa.setTelefone(req.getTelefone());
        empresa.setPlanoId(plano.getId());
        empresa.setStatus("TRIAL");
        empresa.setDataInicioTrial(LocalDateTime.now());
        empresa.setDataFimTrial(LocalDateTime.now().plusDays(14));
        empresa.setSlug(req.getNomeFantasia().toLowerCase().replaceAll("[^a-z0-9]", "-").replaceAll("-+", "-"));
        empresa.setBloqueada(false);
        empresa = empresaRepo.save(empresa);

        // Criar assinatura trial
        AssinaturaEmpresa assinatura = new AssinaturaEmpresa();
        assinatura.setEmpresaId(empresa.getId());
        assinatura.setPlanoId(plano.getId());
        assinatura.setCiclo("MENSAL");
        assinatura.setValor(plano.getPrecoMensal());
        assinatura.setStatus("TRIAL");
        assinatura.setDataInicio(LocalDateTime.now());
        assinatura.setDataVencimento(LocalDateTime.now().plusDays(14));
        assinaturaRepo.save(assinatura);

        // Criar white-label default
        ConfiguracaoWhiteLabel wl = new ConfiguracaoWhiteLabel();
        wl.setEmpresaId(empresa.getId());
        wl.setNomeSistema(req.getNomeFantasia() + " - Sistema de Gestão");
        wl.setCorPrimaria("#1A365D");
        wl.setCorSecundaria("#2B6CB0");
        wl.setCorAcento("#38A169");
        wl.setCorFundo("#F7FAFC");
        wl.setEmailSuporte(req.getEmail());
        whiteLabelRepo.save(wl);

        // Criar admin da empresa
        Usuario admin = new Usuario();
        admin.setEmpresaId(empresa.getId());
        admin.setNome(req.getNomeAdmin());
        admin.setEmail(req.getEmailAdmin());
        admin.setSenha(passwordEncoder.encode(req.getSenhaAdmin()));
        admin.setPerfil(Perfil.ADMIN_EMPRESA);
        admin.setCargo("Administrador");
        usuarioRepo.save(admin);

        return empresa;
    }

    // ========== PAGAMENTOS ==========
    public Page<PagamentoAssinatura> listarPagamentos(Pageable pageable) {
        return pagamentoRepo.findByActiveTrue(pageable);
    }
}
