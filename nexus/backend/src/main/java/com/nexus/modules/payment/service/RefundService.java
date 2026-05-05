package com.nexus.modules.payment.service;

import com.nexus.modules.payment.dto.RefundRequest;
import com.nexus.modules.payment.dto.RefundResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RefundService {

    public RefundResponse requestRefund(UUID paymentId, RefundRequest req, UUID userId) {
        // TODO: implement
        return null;
    }

    public void approve(UUID refundId, UUID approvedBy) {
        // TODO: implement
    }
}
