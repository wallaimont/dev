package com.nexus.modules.auth.service;

import com.nexus.modules.auth.domain.EmailVerification;
import com.nexus.modules.auth.domain.PasswordReset;
import com.nexus.modules.auth.repository.EmailVerificationRepository;
import com.nexus.modules.auth.repository.PasswordResetRepository;
import com.nexus.modules.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service @RequiredArgsConstructor @Slf4j
public class EmailVerificationService {

    private final EmailVerificationRepository verificationRepo;
    private final PasswordResetRepository     resetRepo;
    private final JavaMailSender              mailSender;

    private static final SecureRandom RANDOM = new SecureRandom();

    @Async
    public void sendVerification(User user) {
        String token = generateToken();

        EmailVerification ev = new EmailVerification();
        ev.setUserId(user.getId());
        ev.setToken(token);
        ev.setExpiresAt(Instant.now().plusSeconds(86400));
        verificationRepo.save(ev);

        sendEmail(user.getEmail(), "Verifique seu e-mail — Nexus",
            "<p>Olá!</p><p>Clique no link para verificar seu e-mail:</p>" +
            "<a href='${APP_URL}/verify-email?token=" + token + "'>Verificar e-mail</a>");
    }

    @Transactional
    public void verify(String token) {
        EmailVerification ev = verificationRepo.findByToken(token)
            .orElseThrow(() -> new AuthService.InvalidTokenException("Invalid token"));

        if (ev.getUsedAt() != null) throw new AuthService.InvalidTokenException("Token already used");
        if (ev.getExpiresAt().isBefore(Instant.now())) throw new AuthService.InvalidTokenException("Token expired");

        ev.setUsedAt(Instant.now());
        verificationRepo.save(ev);
    }

    @Async
    public void sendPasswordReset(User user) {
        String token = generateToken();

        PasswordReset pr = new PasswordReset();
        pr.setUserId(user.getId());
        pr.setTokenHash(hashToken(token));
        pr.setExpiresAt(Instant.now().plusSeconds(3600));
        resetRepo.save(pr);

        sendEmail(user.getEmail(), "Redefinir senha — Nexus",
            "<p>Clique para redefinir sua senha (válido por 1 hora):</p>" +
            "<a href='${APP_URL}/reset-password?token=" + token + "'>Redefinir senha</a>");
    }

    public PasswordReset validatePasswordResetToken(String token) {
        return resetRepo.findByTokenHash(hashToken(token))
            .orElseThrow(() -> new AuthService.InvalidTokenException("Invalid token"));
    }

    public void markResetUsed(PasswordReset reset) {
        reset.setUsedAt(Instant.now());
        resetRepo.save(reset);
    }

    @Async
    private void sendEmail(String to, String subject, String htmlBody) {
        try {
            var message = mailSender.createMimeMessage();
            var helper  = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("noreply@nexus.com.br", "Nexus Marketplace");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String token) {
        try {
            var digest = java.security.MessageDigest.getInstance("SHA-256");
            return Base64.getEncoder().encodeToString(
                digest.digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}
