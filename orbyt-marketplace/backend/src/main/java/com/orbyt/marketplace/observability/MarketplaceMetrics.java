package com.orbyt.marketplace.observability;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.math.BigDecimal;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MarketplaceMetrics {

    private final MeterRegistry meterRegistry;

    public void recordCheckoutOutcome(String status, String paymentMethod, BigDecimal amount) {
        meterRegistry.counter(
            "orbyt_checkout_requests",
                "status", safeTag(status),
                "payment_method", safeTag(paymentMethod)
        ).increment();

        meterRegistry.summary(
                "orbyt_checkout_amount",
                "payment_method", safeTag(paymentMethod)
        ).record(safeAmount(amount));
    }

    public void recordCheckoutDuration(String status, Duration duration) {
        Timer.builder("orbyt_checkout_duration")
                .tag("status", safeTag(status))
                .register(meterRegistry)
                .record(duration);
    }

    public void recordPaymentOutcome(String status, String method, BigDecimal amount) {
        meterRegistry.counter(
            "orbyt_payment_events",
                "status", safeTag(status),
                "method", safeTag(method)
        ).increment();

        meterRegistry.summary(
                "orbyt_payment_amount",
                "method", safeTag(method)
        ).record(safeAmount(amount));
    }

    public void recordPaymentWebhook(String status, String method) {
        meterRegistry.counter(
            "orbyt_payment_webhook_events",
                "status", safeTag(status),
                "method", safeTag(method)
        ).increment();
    }

    public void recordFraudAnalysis(String decision, BigDecimal score) {
        meterRegistry.counter(
            "orbyt_fraud_analyses",
                "decision", safeTag(decision)
        ).increment();

        meterRegistry.summary("orbyt_fraud_risk_score").record(safeAmount(score));
    }

    public void recordFraudBlacklistHit(String type) {
        meterRegistry.counter(
            "orbyt_fraud_blacklist_hits",
                "type", safeTag(type)
        ).increment();
    }

    private double safeAmount(BigDecimal value) {
        if (value == null) {
            return 0;
        }
        double parsed = value.doubleValue();
        if (Double.isNaN(parsed) || Double.isInfinite(parsed)) {
            return 0;
        }
        return Math.max(parsed, 0);
    }

    private String safeTag(String value) {
        if (value == null || value.isBlank()) {
            return "unknown";
        }
        return value;
    }
}
