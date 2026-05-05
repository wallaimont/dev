package com.supportdesk.exception;

import com.supportdesk.dto.response.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
    return build(
        HttpStatus.NOT_FOUND,
        "Not Found",
        "O recurso solicitado nao foi encontrado.",
        ex.getMessage(),
        req.getRequestURI(),
        null
    );
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusiness(BusinessException ex, HttpServletRequest req) {
    return build(
        HttpStatus.UNPROCESSABLE_ENTITY,
        ex.getCode(),
        "Nao foi possivel concluir a operacao.",
        ex.getMessage(),
        req.getRequestURI(),
        null
    );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<ApiError.FieldError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ApiError.FieldError(fe.getField(), fe.getDefaultMessage(), fe.getRejectedValue()))
                .toList();
    return build(
        HttpStatus.BAD_REQUEST,
        "Validation Failed",
        "Existem campos invalidos no formulario enviado.",
        "Request validation failed",
        req.getRequestURI(),
        errors
    );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccess(AccessDeniedException ex, HttpServletRequest req) {
    return build(
        HttpStatus.FORBIDDEN,
        "Forbidden",
        "Voce nao possui permissao para acessar este recurso.",
        ex.getMessage(),
        req.getRequestURI(),
        null
    );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuth(AuthenticationException ex, HttpServletRequest req) {
    return build(
        HttpStatus.UNAUTHORIZED,
        "Unauthorized",
        "Sua sessao nao e valida. Efetue login novamente.",
        ex.getMessage(),
        req.getRequestURI(),
        null
    );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req) {
    log.error("Unhandled exception path={} message={}", req.getRequestURI(), ex.getMessage(), ex);
    return build(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Internal Server Error",
        "Ocorreu um erro inesperado. Nossa equipe foi notificada.",
        ex.getClass().getSimpleName() + ": " + ex.getMessage(),
        req.getRequestURI(),
        null
    );
    }

    private ResponseEntity<ApiError> build(HttpStatus status,
                       String error,
                       String friendlyMessage,
                       String technicalMessage,
                       String path,
                       List<ApiError.FieldError> errors) {
    String correlationId = MDC.get("correlationId");
        ApiError body = ApiError.builder()
        .status(status.value())
        .error(error)
        .message(friendlyMessage)
        .friendlyMessage(friendlyMessage)
        .technicalMessage(technicalMessage)
        .correlationId(correlationId)
        .path(path)
        .errors(errors)
                .build();
        return ResponseEntity.status(status).body(body);
    }
}
