package com.example.emailAttachments.domain.model;

import com.example.emailAttachments.application.EmailRequest;
import com.example.emailAttachments.infrastructure.EmailSizeConstraintValidator;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import java.util.List;
import java.util.stream.Collectors;

public class Email {
    private final String to;
    private final String subject;
    private final String body;
    private final List<Attachment> attachments;

    public Email(EmailRequest request) {
            to = request.to();
            subject = request.subject();
            body = request.body();
            attachments = request.attachments().stream()
                    .map(Attachment::new).collect(Collectors.toList());
    }

    public Message populateMessage(MimeMessage message) throws MessagingException {
        message.setRecipients(Message.RecipientType.TO, to);
        message.setSubject(subject);
        message.setContent(formatMultipart());
        EmailSizeConstraintValidator.validateMessageSize(message);
        return message;
    }
    private Multipart formatMultipart() throws MessagingException {
        Multipart multipart = new MimeMultipart();
        attachments.stream().map(e-> {
            try {
                return e.attach();
            } catch (MessagingException ex) {
                throw new RuntimeException(ex);
            }
        }).forEach(e-> {
            try {
                multipart.addBodyPart(e);
            } catch (MessagingException ex) {
                throw new RuntimeException(ex);
            }
        });
        multipart.addBodyPart(setBodyText());
        return multipart;
    }

    private BodyPart setBodyText() throws MessagingException {
        EmailSizeConstraintValidator.validateBodySize(body.getBytes());
        BodyPart text = new MimeBodyPart();
        text.setText(body);
        return text;
    }

}
