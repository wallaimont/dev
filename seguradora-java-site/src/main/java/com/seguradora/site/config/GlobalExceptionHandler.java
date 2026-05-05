package com.seguradora.site.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.ui.Model;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoResourceFoundException.class)
    public void handleNoResourceFound(
            NoResourceFoundException e,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        String uri = request.getRequestURI();

        if ("/favicon.ico".equals(uri)) {
            log.debug("Recurso opcional nao encontrado: {}", uri);
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return;
        }

        log.warn("Recurso nao encontrado: {}", uri);
        response.sendError(HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception e, Model model) {
        log.error("Erro inesperado: {}", e.getMessage(), e);
        model.addAttribute("errorMessage", "Ocorreu um erro inesperado. Por favor, tente novamente.");
        return "error";
    }
}
