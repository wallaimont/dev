package com.orionerp.modules.crm.service;

import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.crm.domain.AtividadeCrm;
import com.orionerp.modules.crm.dto.AtividadeCrmRequest;
import com.orionerp.modules.crm.dto.AtividadeCrmResponse;
import com.orionerp.modules.crm.repository.AtividadeCrmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AtividadeCrmService {

    private final AtividadeCrmRepository atividadeCrmRepository;

    @Transactional(readOnly = true)
    public List<AtividadeCrmResponse> listarPorLead(Long empresaId, Long leadId) {
        return atividadeCrmRepository.findByEmpresaIdAndLeadIdOrderByDataHoraDesc(empresaId, leadId)
                .stream().map(AtividadeCrmResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<AtividadeCrmResponse> listarPorOportunidade(Long empresaId, Long oportunidadeId) {
        return atividadeCrmRepository.findByEmpresaIdAndOportunidadeIdOrderByDataHoraDesc(empresaId, oportunidadeId)
                .stream().map(AtividadeCrmResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<AtividadeCrmResponse> listarPorCliente(Long empresaId, Long clienteId) {
        return atividadeCrmRepository.findByEmpresaIdAndClienteIdOrderByDataHoraDesc(empresaId, clienteId)
                .stream().map(AtividadeCrmResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<AtividadeCrmResponse> listarPendentes(Long empresaId, Long responsavelId) {
        return atividadeCrmRepository.findByEmpresaIdAndResponsavelIdAndConcluidaFalseOrderByDataHoraAsc(empresaId, responsavelId)
                .stream().map(AtividadeCrmResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public AtividadeCrmResponse buscarPorId(Long id) {
        AtividadeCrm entity = atividadeCrmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Atividade CRM não encontrada: " + id));
        return AtividadeCrmResponse.from(entity);
    }

    @Transactional
    public AtividadeCrmResponse criar(AtividadeCrmRequest request) {
        AtividadeCrm entity = new AtividadeCrm();
        entity.setEmpresaId(request.empresaId());
        entity.setTipo(request.tipo());
        entity.setTitulo(request.titulo());
        entity.setDescricao(request.descricao());
        entity.setLeadId(request.leadId());
        entity.setOportunidadeId(request.oportunidadeId());
        entity.setClienteId(request.clienteId());
        entity.setResponsavelId(request.responsavelId());
        entity.setDataHora(request.dataHora());
        entity.setDuracaoMinutos(request.duracaoMinutos());
        entity.setConcluida(request.concluida() != null ? request.concluida() : false);

        return AtividadeCrmResponse.from(atividadeCrmRepository.save(entity));
    }

    @Transactional
    public AtividadeCrmResponse atualizar(Long id, AtividadeCrmRequest request) {
        AtividadeCrm entity = atividadeCrmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Atividade CRM não encontrada: " + id));

        entity.setTipo(request.tipo());
        entity.setTitulo(request.titulo());
        entity.setDescricao(request.descricao());
        entity.setLeadId(request.leadId());
        entity.setOportunidadeId(request.oportunidadeId());
        entity.setClienteId(request.clienteId());
        entity.setResponsavelId(request.responsavelId());
        entity.setDataHora(request.dataHora());
        entity.setDuracaoMinutos(request.duracaoMinutos());
        if (request.concluida() != null) entity.setConcluida(request.concluida());
        entity.setUpdatedAt(LocalDateTime.now());

        return AtividadeCrmResponse.from(atividadeCrmRepository.save(entity));
    }

    @Transactional
    public void concluir(Long id) {
        AtividadeCrm entity = atividadeCrmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Atividade CRM não encontrada: " + id));
        entity.setConcluida(true);
        entity.setUpdatedAt(LocalDateTime.now());
        atividadeCrmRepository.save(entity);
    }
}
