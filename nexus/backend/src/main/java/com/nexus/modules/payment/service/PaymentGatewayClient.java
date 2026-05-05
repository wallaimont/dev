package com.nexus.modules.payment.service;

import com.nexus.modules.payment.dto.GatewayPixRequest;
import com.nexus.modules.payment.dto.GatewayPixResponse;

public interface PaymentGatewayClient {
    GatewayPixResponse createPixCharge(GatewayPixRequest request);
    void validateSignature(String gateway, Object payload, String signature);
}
