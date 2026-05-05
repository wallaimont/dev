package com.orionerp.modules.crm.service;

import com.orionerp.common.PageResponse;
import com.orionerp.exception.BusinessException;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.crm.domain.Lead;
import com.orionerp.modules.crm.domain.Oportunidade;
import com.orionerp.modules.crm.dto.OportunidadeRequest;
import com.orionerp.modules.crm.dto.OportunidadeResponse;
import com.orionerp.modules.crm.repository.CrmSpecifications;
import com.orionerp.modules.crm.repository.LeadRepository;
import com.orionerp.modules.crm.repository.OportunidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OportunidadeService {

    private static final List<String> FUNIL_FLOW = List.of(
            "PROSPECCAO", "QUALIFICACAO", "PROPOSTA", "NEGOCIACAO", "FECHAMENTO");

    private static final Map<String, Integer> PROBABILIDADE_PADRAO = Map.of(
            "PROSPECCAO", 10, "QUALIFICACAO", 25, "PROPOSTA", 50,
            "NEGOCIACAO", 75, "FECHAMENTO", 90);

    private final OportunidadeRepository oportunidadeRepository;
    private final LeadRepository leadRepository;

    @Transactional(readOnly = true)
    public PageResponse<OportunidadeResponse> list(Long empresaId, String etapaFunil,
                                                    Long responsavelId, String term, Pageable pageable) {
        var spec = CrmSpecifications.oportunidadeFilter(empresaId, etapaFunil, responsavelId, term);
        var page = oportunidadeRepository.findAll(spec, pageable).map(OportunidadeResponse::from);
        return PageResponse.from(page);
    }

    @Transactional(readOnly = true)
    public OportunidadeResponse getById(Long id) {
        return OportunidadeResponse.from(findActiveById(id));
    }

    @Transactional
    public OportunidadeResponse create(OportunidadeRequest request) {
        Oportunidade op = new Oportunidade();
        op.setEmpresaId(request.empresaId());
        op.setTitulo(request.titulo());
        op.setClienteId(request.clienteId());
        op.setResponsavelId(request.responsavelId());
        op.setValorEstimado(request.valorEstimado());
        op.setProbabilidade(request.probabilidade() != null ? request.probabilidade() : 10);
        op.setDataPrevisaoFechamento(request.dataPrevisaoFechamento());
        op.setObservacao(request.observacao());
        op.setEtapaFunil("PROSPECCAO");

        if (request.leadId() != null) {
            Lead lead = leadRepository.findByIdAndDeletedFalse(request.leadId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lead não encontrado: " + request.leadId()));
            op.setLead(lead);
        }

        return OportunidadeResponse.from(oportunidadeRepository.save(op));
    }

    @Transactional
    public OportunidadeResponse avancarEtapa(Long id) {
        Oportunidade op = findActiveById(id);
        String etapaAtual = op.getEtapaFunil();

        int idx = FUNIL_FLOW.indexOf(etapaAtual);
        if (idx < 0 || idx >= FUNIL_FLOW.size() - 1) {
            throw new BusinessException("Não é possível avançar a etapa. Etapa atual: " + etapaAtual);
        }

        String novaEtapa = FUNIL_FLOW.get(idx + 1);
        op.setEtapaFunil(novaEtapa);
        op.setProbabilidade(PROBABILIDADE_PADRAO.getOrDefault(novaEtapa, op.getProbabilidade()));
        return OportunidadeResponse.from(oportunidadeRepository.save(op));
    }

    @Transactional
    public OportunidadeResponse ganhar(Long id) {
        Oportunidade op = findActiveById(id);

        if ("GANHO".equals(op.getEtapaFunil()) || "PERDIDO".equals(op.getEtapaFunil())) {
            throw new BusinessException("Oportunidade já finalizada: " + op.getEtapaFunil());
        }

        op.setEtapaFunil("GANHO");
        op.setProbabilidade(100);
        return OportunidadeResponse.from(oportunidadeRepository.save(op));
    }

    @Transactional
    public OportunidadeResponse perder(Long id, String motivoPerda) {
        Oportunidade op = findActiveById(id);

        if ("GANHO".equals(op.getEtapaFunil()) || "PERDIDO".equals(op.getEtapaFunil())) {
            throw new BusinessException("Oportunidade já finalizada: " + op.getEtapaFunil());
        }

        op.setEtapaFunil("PERDIDO");
        op.setProbabilidade(0);
        op.setMotivoPerda(motivoPerda);
        return OportunidadeResponse.from(oportunidadeRepository.save(op));
    }

    private Oportunidade findActiveById(Long id) {
        return oportunidadeRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Oportunidade não encontrada: " + id));
    }
}
