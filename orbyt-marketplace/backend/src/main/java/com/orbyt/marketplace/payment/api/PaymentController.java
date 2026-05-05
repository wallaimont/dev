package com.orbyt.marketplace.payment.api;

import com.orbyt.marketplace.payment.api.dto.PixChargeRequest;
import com.orbyt.marketplace.payment.api.dto.PixChargeResponse;
import com.orbyt.marketplace.payment.application.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/pix/charges")
    public PixChargeResponse createPixCharge(@Valid @RequestBody PixChargeRequest request) {
        return paymentService.createPixCharge(request);
    }

    @PostMapping("/webhooks/{provider}")
    public String receiveWebhook(@PathVariable String provider, @RequestBody String payload) {
        return "Webhook accepted for provider " + provider + " with payload length " + payload.length();
    }
}
