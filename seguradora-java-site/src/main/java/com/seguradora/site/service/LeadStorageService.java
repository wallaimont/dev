package com.seguradora.site.service;

import com.seguradora.site.entity.LeadRecord;
import com.seguradora.site.model.LeadRequest;
import com.seguradora.site.model.QuoteResult;
import com.seguradora.site.repository.LeadRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeadStorageService {

    private final LeadRecordRepository leadRecordRepository;

    public LeadStorageService(LeadRecordRepository leadRecordRepository) {
        this.leadRecordRepository = leadRecordRepository;
    }

    public LeadRecord save(LeadRequest leadRequest) {
        return save(leadRequest, null);
    }

    public List<LeadRequest> findAll() {
        return leadRecordRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toLeadRequest)
                .collect(Collectors.toList());
    }

    public int count() {
        return Math.toIntExact(leadRecordRepository.count());
    }

    public List<LeadRecord> findAllRecords() {
        return leadRecordRepository.findAllByOrderByCreatedAtDesc();
    }

    public LeadRecord save(LeadRequest leadRequest, QuoteResult quoteResult) {
        LeadRecord leadRecord = new LeadRecord();
        leadRecord.setNome(leadRequest.getNome());
        leadRecord.setEmail(leadRequest.getEmail());
        leadRecord.setTelefone(leadRequest.getTelefone());
        leadRecord.setTipoSeguro(leadRequest.getTipoSeguro());
        leadRecord.setCobertura(leadRequest.getCobertura());
        leadRecord.setValorBem(leadRequest.getValorBem());
        leadRecord.setMensagem(leadRequest.getMensagem());
        leadRecord.setOrigem(leadRequest.getOrigem());

        if (quoteResult != null) {
            leadRecord.setValorMensalEstimado(quoteResult.getValorMensal());
            leadRecord.setFranquiaEstimada(quoteResult.getFranquia());
            leadRecord.setPrazoRetorno(quoteResult.getPrazoRetorno());
        }

        return leadRecordRepository.save(leadRecord);
    }

    private LeadRequest toLeadRequest(LeadRecord leadRecord) {
        LeadRequest leadRequest = new LeadRequest();
        leadRequest.setNome(leadRecord.getNome());
        leadRequest.setEmail(leadRecord.getEmail());
        leadRequest.setTelefone(leadRecord.getTelefone());
        leadRequest.setTipoSeguro(leadRecord.getTipoSeguro());
        leadRequest.setCobertura(leadRecord.getCobertura());
        leadRequest.setValorBem(leadRecord.getValorBem());
        leadRequest.setMensagem(leadRecord.getMensagem());
        leadRequest.setOrigem(leadRecord.getOrigem());
        return leadRequest;
    }
}
