package com.example.emailAttachments.domain.service;

import com.example.emailAttachments.domain.model.Email;
import com.example.emailAttachments.application.EmailRequest;
import com.example.emailAttachments.application.port.in.SendEmail;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class EmailService implements SendEmail {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void send(EmailRequest request) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            Email email = new Email(request);
            email.populateMessage(message);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
