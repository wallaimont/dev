package com.nexus.modules.payment.service;

import com.nexus.modules.payment.dto.GatewayPixRequest;
import com.nexus.modules.payment.dto.GatewayPixResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Profile({"dev", "test"})
public class MockGatewayPixClient implements GatewayPixClient {

    @Override
    public GatewayPixResponse createPixCharge(GatewayPixRequest req) {
        String txid = "MOCK" + System.currentTimeMillis();
        String pixCode = "00020126580014BR.GOV.BCB.PIX0136" + java.util.UUID.randomUUID()
            + "5204000053039865802BR5920NEXUS MARKETPLACE6009SAO PAULO62070503***6304ABCD";

        GatewayPixResponse response = new GatewayPixResponse();
        response.setGateway("MOCK");
        response.setTxId(txid);
        response.setQrCode(pixCode);
        response.setQrCodeImageUrl("https://api.qrserver.com/v1/create-qr-code/?data=" + pixCode);
        response.setPixCopyPaste(pixCode);
        response.setExpiresAt(Instant.now().plusSeconds(req.getExpiresInSeconds()));
        return response;
    }

    @Override
    public void validateSignature(String gateway, Object payload, String signature) {
        // No validation in dev/test
    }
}
