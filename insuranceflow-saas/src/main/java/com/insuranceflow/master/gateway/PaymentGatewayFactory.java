package com.insuranceflow.master.gateway;

import com.insuranceflow.master.model.GatewayPagamento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentGatewayFactory {

    private final StripeGateway stripeGateway;
    private final AsaasGateway asaasGateway;
    private final MercadoPagoGateway mercadoPagoGateway;
    private final EfiGateway efiGateway;

    public PaymentGateway getGateway(String gateway) {
        GatewayPagamento gp = GatewayPagamento.valueOf(gateway);
        return switch (gp) {
            case STRIPE -> stripeGateway;
            case ASAAS -> asaasGateway;
            case MERCADO_PAGO -> mercadoPagoGateway;
            case EFI -> efiGateway;
            case MANUAL -> throw new IllegalArgumentException("Gateway MANUAL não requer integração externa");
        };
    }
}
