package com.orionerp.modules.workflow.service;

import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.workflow.domain.WorkflowAlcada;
import com.orionerp.modules.workflow.domain.WorkflowAprovacao;
import com.orionerp.modules.workflow.domain.WorkflowDefinicao;
import com.orionerp.modules.workflow.dto.*;
import com.orionerp.modules.workflow.repository.WorkflowAprovacaoRepository;
import com.orionerp.modules.workflow.repository.WorkflowDefinicaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final WorkflowDefinicaoRepository definicaoRepository;
    private final WorkflowAprovacaoRepository aprovacaoRepository;

    // ── Definição CRUD ──────────────────────────────────────────────────

    @Transactional
    public WorkflowDefinicaoResponse criarDefinicao(WorkflowDefinicaoRequest req) {
        if (definicaoRepository.existsByEmpresaIdAndCodigo(req.empresaId(), req.codigo())) {
            throw new BusinessException("Já existe um workflow com o código '" + req.codigo()
                    + "' para esta empresa");
        }

        WorkflowDefinicao def = new WorkflowDefinicao();
        def.setEmpresaId(req.empresaId());
        def.setCodigo(req.codigo());
        def.setNome(req.nome());
        def.setDescricao(req.descricao());
        def.setModulo(req.modulo());
        def.setEntidade(req.entidade());
        syncAlcadas(def, req.alcadas());

        return WorkflowDefinicaoResponse.from(definicaoRepository.save(def));
    }

    @Transactional
    public WorkflowDefinicaoResponse atualizarDefinicao(Long id, WorkflowDefinicaoRequest req) {
        WorkflowDefinicao def = findDefinicaoOrThrow(id);
        def.setNome(req.nome());
        def.setDescricao(req.descricao());
        def.setModulo(req.modulo());
        def.setEntidade(req.entidade());
        def.setUpdatedAt(LocalDateTime.now());
        syncAlcadas(def, req.alcadas());

        return WorkflowDefinicaoResponse.from(definicaoRepository.save(def));
    }

    @Transactional(readOnly = true)
    public WorkflowDefinicaoResponse buscarDefinicaoPorId(Long id) {
        return WorkflowDefinicaoResponse.from(findDefinicaoOrThrow(id));
    }

    @Transactional
    public void desativarDefinicao(Long id) {
        WorkflowDefinicao def = findDefinicaoOrThrow(id);
        def.setAtivo(false);
        def.setUpdatedAt(LocalDateTime.now());
        definicaoRepository.save(def);
    }

    @Transactional(readOnly = true)
    public List<WorkflowDefinicaoResponse> listDefinicoes(Long empresaId) {
        return definicaoRepository.findByEmpresaIdAndAtivoTrueOrderByNome(empresaId)
                .stream().map(WorkflowDefinicaoResponse::from).toList();
    }

    // ── Aprovação ───────────────────────────────────────────────────────

    @Transactional
    public void iniciarAprovacao(Long empresaId, Long filialId, String modulo,
                                 String entidade, Long entidadeId) {
        WorkflowDefinicao def = definicaoRepository
                .findByEmpresaIdAndModuloAndEntidadeAndAtivoTrue(empresaId, modulo, entidade)
                .orElse(null);

        if (def == null || def.getAlcadas().isEmpty()) {
            return;
        }

        for (WorkflowAlcada alcada : def.getAlcadas()) {
            WorkflowAprovacao aprovacao = new WorkflowAprovacao();
            aprovacao.setEmpresaId(empresaId);
            aprovacao.setFilialId(filialId);
            aprovacao.setWorkflowDefinicao(def);
            aprovacao.setAlcada(alcada);
            aprovacao.setEntidade(entidade);
            aprovacao.setEntidadeId(entidadeId);
            aprovacao.setNivel(alcada.getNivel());
            aprovacao.setAprovadorId(alcada.getUsuarioId());
            aprovacao.setStatus("PENDENTE");
            aprovacaoRepository.save(aprovacao);
        }
    }

    @Transactional
    public WorkflowAprovacaoResponse decidir(Long aprovacaoId, Long aprovadorId,
                                             String decisao, String justificativa) {
        WorkflowAprovacao aprovacao = aprovacaoRepository.findById(aprovacaoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aprovação não encontrada: " + aprovacaoId));

        if (!"PENDENTE".equals(aprovacao.getStatus())) {
            throw new BusinessException("Aprovação já foi decidida: " + aprovacao.getStatus());
        }

        aprovacao.setAprovadorId(aprovadorId);
        aprovacao.setDecisao(decisao);
        aprovacao.setJustificativa(justificativa);
        aprovacao.setDataDecisao(LocalDateTime.now());
        aprovacao.setStatus(decisao);
        aprovacao.setUpdatedAt(LocalDateTime.now());

        return WorkflowAprovacaoResponse.from(aprovacaoRepository.save(aprovacao));
    }

    @Transactional(readOnly = true)
    public List<WorkflowAprovacaoResponse> listarPendentes(Long aprovadorId) {
        return aprovacaoRepository
                .findByAprovadorIdAndStatusOrderByCreatedAtDesc(aprovadorId, "PENDENTE")
                .stream().map(WorkflowAprovacaoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<WorkflowAprovacaoResponse> listarPorEntidade(String entidade, Long entidadeId) {
        return aprovacaoRepository
                .findByEntidadeAndEntidadeIdOrderByNivelAsc(entidade, entidadeId)
                .stream().map(WorkflowAprovacaoResponse::from).toList();
    }

    // ── Helpers ─────────────────────────────────────────────────────────

    private WorkflowDefinicao findDefinicaoOrThrow(Long id) {
        return definicaoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Workflow não encontrado: " + id));
    }

    private void syncAlcadas(WorkflowDefinicao def, List<WorkflowAlcadaRequest> items) {
        def.getAlcadas().clear();
        if (items == null || items.isEmpty()) {
            return;
        }
        for (WorkflowAlcadaRequest r : items) {
            WorkflowAlcada a = new WorkflowAlcada();
            a.setWorkflowDefinicao(def);
            a.setNivel(r.nivel() != null ? r.nivel() : (items.indexOf(r) + 1));
            a.setNome(r.nome() != null ? r.nome() : ("Nível " + (items.indexOf(r) + 1)));
            a.setPerfilId(r.perfilId());
            a.setUsuarioId(r.usuarioId());
            a.setValorMinimo(r.valorMinimo());
            a.setValorMaximo(r.valorMaximo());
            a.setObrigatorio(r.obrigatorio() != null ? r.obrigatorio() : true);
            a.setOrdem(r.ordem() != null ? r.ordem() : (items.indexOf(r) + 1));
            def.getAlcadas().add(a);
        }
    }
}
