package com.example.emailAttachments.domain.model;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EmailSizeConstraintValidator {

    private static final long MAX_ATTACHMENT_SIZE_KB = 10 * 1024;
    private static final long MAX_MESSAGE_SIZE_KB = 20 * 1024;
    private static final long MAX_BODY_SIZE_KB = 100;

    static Logger log = LoggerFactory.getLogger(EmailAttachmentUtils.class);

    public static void validateAttachmentSize(byte[] attachment) {
        log.info("attachment size: {}KB", attachment.length / 1024);
        if (attachment.length / 1024 > MAX_ATTACHMENT_SIZE_KB) {
            log.error("Max attachment size: {} --- current attachment size {}", MAX_ATTACHMENT_SIZE_KB, attachment.length / 1024);
            throw new IllegalArgumentException("Attachment size exceeds maximum allowed size");
        }
    }

    public static void validateMessageSize(MimeMessage message) throws MessagingException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            message.writeTo(os);
        } catch (IOException e) {
            log.error("Unable to write to ByteArrayOutputStream");
            throw new RuntimeException(e);
        }
        long messageSize = os.size();
        log.info("message size: {}KB", messageSize / 1024);
        if (messageSize/1024 > MAX_MESSAGE_SIZE_KB) {
            log.error("max message size: {} --- current message size {}", MAX_MESSAGE_SIZE_KB, messageSize/1024);
            throw new IllegalArgumentException("Message size exceeds maximum allowed size");
        }
    }

    public static void validateBodySize(byte[] body) {
        log.info("body size size: {}KB", body.length / 1024);
        if (body.length/1024 > MAX_BODY_SIZE_KB) {
            log.error("Max body size: {} --- current body size {}", MAX_BODY_SIZE_KB, body.length/1024);
            throw new IllegalArgumentException("Body size exceeds maximum allowed size");
        }
    }
}
