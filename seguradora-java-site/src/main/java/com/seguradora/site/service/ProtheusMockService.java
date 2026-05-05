package com.seguradora.site.service;

import com.seguradora.site.api.ProtheusMockHealthResponse;
import com.seguradora.site.api.ProtheusMockLeadResponse;
import com.seguradora.site.model.LeadRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ProtheusMockService {

    private static final String SISTEMA = "PROTHEUS-MOCK";
    private static final String EMPRESA = "99";
    private static final String FILIAL = "01";

    private final AtomicLong sequence = new AtomicLong(1);
    private final CopyOnWriteArrayList<ProtheusMockLeadResponse> receivedLeads = new CopyOnWriteArrayList<>();

    public ProtheusMockLeadResponse receiveLead(LeadRequest leadRequest) {
        long id = sequence.getAndIncrement();
        ProtheusMockLeadResponse response = new ProtheusMockLeadResponse(
                id,
                buildProtocol(id),
                SISTEMA,
                EMPRESA,
                FILIAL,
                "RECEBIDO",
                leadRequest.getOrigem() != null ? leadRequest.getOrigem() : "manual",
                leadRequest.getNome(),
                leadRequest.getEmail(),
                leadRequest.getTelefone(),
                leadRequest.getTipoSeguro(),
                leadRequest.getMensagem(),
                LocalDateTime.now()
        );
        receivedLeads.add(0, response);
        return response;
    }

    public List<ProtheusMockLeadResponse> listReceivedLeads() {
        return List.copyOf(receivedLeads);
    }

    public ProtheusMockHealthResponse health() {
        return new ProtheusMockHealthResponse(
                SISTEMA,
                "UP",
                EMPRESA,
                FILIAL,
                receivedLeads.size(),
                LocalDateTime.now()
        );
    }

    public void clear() {
        receivedLeads.clear();
        sequence.set(1);
    }

    private String buildProtocol(long id) {
        return "PTM-" + String.format("%06d", id);
    }
}