package com.orionerp.modules.crm.service;

import com.orionerp.common.PageResponse;
import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.crm.domain.Lead;
import com.orionerp.modules.crm.dto.LeadRequest;
import com.orionerp.modules.crm.dto.LeadResponse;
import com.orionerp.modules.crm.repository.CrmSpecifications;
import com.orionerp.modules.crm.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeadService {

    private static final List<String> STATUS_FLOW = List.of(
            "NOVO", "CONTATADO", "QUALIFICADO");

    private final LeadRepository leadRepository;

    @Transactional(readOnly = true)
    public PageResponse<LeadResponse> list(Long empresaId, String status,
                                           Long responsavelId, String term, Pageable pageable) {
        var spec = CrmSpecifications.leadFilter(empresaId, status, responsavelId, term);
        var page = leadRepository.findAll(spec, pageable).map(LeadResponse::from);
        return PageResponse.from(page);
    }

    @Transactional(readOnly = true)
    public LeadResponse getById(Long id) {
        Lead lead = findActiveById(id);
        return LeadResponse.from(lead);
    }

    @Transactional
    public LeadResponse create(LeadRequest request) {
        Lead lead = new Lead();
        lead.setEmpresaId(request.empresaId());
        lead.setNome(request.nome());
        lead.setEmail(request.email());
        lead.setTelefone(request.telefone());
        lead.setEmpresaLead(request.empresaLead());
        lead.setCargo(request.cargo());
        lead.setOrigem(request.origem());
        lead.setResponsavelId(request.responsavelId());
        lead.setObservacao(request.observacao());
        lead.setStatus("NOVO");

        return LeadResponse.from(leadRepository.save(lead));
    }

    @Transactional
    public LeadResponse avancarStatus(Long id) {
        Lead lead = findActiveById(id);
        String statusAtual = lead.getStatus();

        int idx = STATUS_FLOW.indexOf(statusAtual);
        if (idx < 0 || idx >= STATUS_FLOW.size() - 1) {
            throw new BusinessException("Não é possível avançar o status do lead. Status atual: " + statusAtual);
        }

        lead.setStatus(STATUS_FLOW.get(idx + 1));
        return LeadResponse.from(leadRepository.save(lead));
    }

    @Transactional
    public LeadResponse converter(Long id, Long clienteId) {
        Lead lead = findActiveById(id);

        if (!"QUALIFICADO".equals(lead.getStatus())) {
            throw new BusinessException("Somente leads QUALIFICADOS podem ser convertidos. Status atual: "
                    + lead.getStatus());
        }

        lead.setStatus("CONVERTIDO");
        lead.setConvertidoClienteId(clienteId);
        return LeadResponse.from(leadRepository.save(lead));
    }

    @Transactional
    public LeadResponse perder(Long id, String motivo) {
        Lead lead = findActiveById(id);

        if ("CONVERTIDO".equals(lead.getStatus()) || "PERDIDO".equals(lead.getStatus())) {
            throw new BusinessException("Lead já está " + lead.getStatus());
        }

        lead.setStatus("PERDIDO");
        lead.setObservacao(motivo);
        return LeadResponse.from(leadRepository.save(lead));
    }

    private Lead findActiveById(Long id) {
        return leadRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead não encontrado: " + id));
    }
}
