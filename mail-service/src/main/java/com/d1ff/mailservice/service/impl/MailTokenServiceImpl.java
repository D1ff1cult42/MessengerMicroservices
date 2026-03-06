package com.d1ff.mailservice.service.impl;

import com.d1ff.mailservice.entity.MailToken;
import com.d1ff.mailservice.exceptions.EmailException;
import com.d1ff.mailservice.repository.MailTokenRepository;
import com.d1ff.mailservice.service.interfaces.MailTokenService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailTokenServiceImpl implements MailTokenService {

    private final JavaMailSender mailSender;
    private final MailTokenRepository mailTokenRepository;
    private final TemplateEngine templateEngine;

    @Value("${mail.token.deletionReminderTime}")
    private Duration deletionReminderTime;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${mail.confirm-url}")
    private String confirmUrl;

    @Value("${mail.token.expiration}")
    private Duration tokenExpiration;

    @Override
    public void sendConfirmationEmail(UUID userId, String email) {
        log.info("Sending message to confirm email: {}", email);
        MailToken mailToken = new MailToken();

        mailToken.setToken(UUID.randomUUID());
        mailToken.setUserId(userId);
        mailToken.setEmail(email);
        mailToken.setExpiresAt(LocalDateTime.now().plus(tokenExpiration));

        mailTokenRepository.save(mailToken);

        Context context = new Context();
        context.setVariable("confirmUrl",confirmUrl + "?token=" + mailToken.getToken());
        context.setVariable("expirationHours", tokenExpiration.toHours());

        String html = templateEngine.process("email-confirm", context);

        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("Подтверждение аккаунта - Messenger");
            helper.setText(html, true);
            mailSender.send(message);
            log.info("Confirmation message was sending to {}", email);
        }catch (MessagingException ex){
            log.error("Error sending message to {}", email);
            throw new EmailException(ex.getMessage());
        }
    }

    @Override
    public UUID confirmToken(UUID token) {
        log.info("Confirming token {}", token);
        MailToken mailToken = mailTokenRepository.findByToken(token)
                .orElseThrow(() -> new EmailException("Token not found"));

        if (mailToken.isUsed()) {
            log.warn("Token {} already used", token);
            throw new EmailException("Token already used");
        }

        if (mailToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("Token {} expired", token);
            throw new EmailException("Token expired");
        }

        mailToken.setUsed(true);
        mailToken.setConfirmedAt(LocalDateTime.now());
        mailTokenRepository.save(mailToken);

        log.info("Token {} confirm", token);
        return mailToken.getUserId();
    }

    @Scheduled(fixedDelayString = "${mail.token.scheduled-check-interval:3600000}")
    public void checkUnverifiedAccounts() {
        log.info("Scheduled to send unverified started");
        LocalDateTime warningThreshold = LocalDateTime.now().plus(deletionReminderTime);
        List<MailToken> toWarn = mailTokenRepository
                .findAllByUsedFalseAndWarningSentFalseAndCreatedAtBefore(
                        warningThreshold.minus(tokenExpiration));

        for (MailToken token : toWarn) {
            sendWarningEmail(token.getEmail(), token.getExpiresAt());
            token.setWarningSent(true);
            mailTokenRepository.save(token);
        }
    }

    @Scheduled(fixedDelayString = "${mail.token.cleanup-interval:86400000}")
    public void deleteUsedAndExpiredTokens() {
        log.info("Scheduled to delete expired and used tokens started");
        mailTokenRepository.deleteExpiredAndUsed(LocalDateTime.now());
    }

    private void sendWarningEmail(String email, LocalDateTime expiresAt) {
        Context context = new Context();
        context.setVariable("expiresAt", expiresAt);

        String html = templateEngine.process("email-warning", context);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("Ваш аккаунт будет удалён, если вы не подтвердите почту — Messenger");
            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException ex) {
            log.error("Failed to send warning message to {}", email);
            throw new EmailException(ex.getMessage());
        }
    }
}
