package com.nexus.modules.payment.service;

import com.nexus.modules.payment.dto.GatewayPixRequest;
import com.nexus.modules.payment.dto.GatewayPixResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Profile({"dev", "test", "default"})
public class MockPaymentGatewayClient implements PaymentGatewayClient {

    @Override
    public GatewayPixResponse createPixCharge(GatewayPixRequest req) {
        var resp = new GatewayPixResponse();
        resp.setGateway("MOCK");
        resp.setTxId("MOCK" + System.currentTimeMillis());
        resp.setQrCode("00020126580014BR.GOV.BCB.PIX0136" + java.util.UUID.randomUUID());
        resp.setPixCopyPaste(resp.getQrCode());
        resp.setExpiresAt(Instant.now().plusSeconds(req.getExpiresInSeconds()));
        return resp;
    }

    @Override
    public void validateSignature(String gateway, Object payload, String signature) {
        // No validation in dev/test
    }
}
