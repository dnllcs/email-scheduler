package com.example.emailAttachments.domain.service;

import com.example.emailAttachments.domain.model.EmailAttachmentUtils;
import com.example.emailAttachments.domain.model.EmailRequest;
import com.example.emailAttachments.domain.model.EmailSizeConstraintValidator;
import com.example.emailAttachments.port.in.SendEmailUseCase;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.mail.javamail.JavaMailSender;

public class EmailService implements SendEmailUseCase {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void send(EmailRequest request) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            message.setRecipients(Message.RecipientType.TO, request.to());
            message.setSubject(request.subject());
            message.setContent(formatMultipart(request));
            EmailSizeConstraintValidator.validateMessageSize(message);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public Multipart formatMultipart(EmailRequest request) throws MessagingException {
        Multipart multipart = new MimeMultipart();
        EmailAttachmentUtils.addAttachments(multipart, request.attachments());
        multipart.addBodyPart(setBodyText(request));
        return multipart;
    }

    public BodyPart setBodyText(EmailRequest request) throws MessagingException {
        EmailSizeConstraintValidator.validateBodySize(request.body().getBytes());
        BodyPart text = new MimeBodyPart();
        text.setText(request.body());
        return text;
    }
}

