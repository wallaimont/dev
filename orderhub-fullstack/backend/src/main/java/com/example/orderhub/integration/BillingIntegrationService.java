package com.example.orderhub.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class BillingIntegrationService {

    private final RestTemplate restTemplate;

    @Value("${app.integration.billing-url}")
    private String billingUrl;

    public BillingIntegrationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String sendOrderToBilling(Long orderId, Double totalAmount) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("orderId", orderId);
        payload.put("totalAmount", totalAmount);
        payload.put("source", "orderhub");

        try {
            restTemplate.postForEntity(billingUrl, payload, String.class);
            return "Pedido enviado com sucesso para faturamento";
        } catch (Exception ex) {
            return "Falha ao enviar pedido: " + ex.getMessage();
        }
    }
}
