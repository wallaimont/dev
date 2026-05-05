package com.insuranceflow.common.exception;

import org.springframework.http.HttpStatus;

public class PlanLimitExceededException extends BusinessException {

    public PlanLimitExceededException(String resource) {
        super("Limite do plano atingido para: " + resource + ". Faça upgrade do seu plano.", HttpStatus.PAYMENT_REQUIRED);
    }
}
