package com.insuranceflow.common.exception;

import org.springframework.http.HttpStatus;

public class AccessDeniedException extends BusinessException {

    public AccessDeniedException() {
        super("Acesso negado", HttpStatus.FORBIDDEN);
    }

    public AccessDeniedException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
