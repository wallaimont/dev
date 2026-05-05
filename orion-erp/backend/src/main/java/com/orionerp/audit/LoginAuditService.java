package com.orionerp.audit;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginAuditService {

    private final LoginLogRepository loginLogRepository;

    public void success(Long userId, String email, HttpServletRequest request) {
        LoginLog log = new LoginLog();
        log.setUsuarioId(userId);
        log.setEmail(email);
        log.setSucesso(true);
        log.setIp(resolveIp(request));
        log.setUserAgent(request.getHeader("User-Agent"));
        loginLogRepository.save(log);
    }

    public void failed(String email, String reason, HttpServletRequest request) {
        LoginLog log = new LoginLog();
        log.setEmail(email);
        log.setSucesso(false);
        log.setMotivoFalha(reason);
        log.setIp(resolveIp(request));
        log.setUserAgent(request.getHeader("User-Agent"));
        loginLogRepository.save(log);
    }

    private String resolveIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
