package com.example.emailAttachments.domain.model;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.util.ByteArrayDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class EmailAttachmentUtils {

    static Logger log = LoggerFactory.getLogger(EmailAttachmentUtils.class);

    public static void addAttachments(Multipart multiPart, List<String> attachments) throws MessagingException {
        attachments.stream()
            .map(attachment -> {
                try {
                    return EmailAttachmentUtils.attach(attachment);
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            })
            .forEach(bodyPart -> {
                try {
                    multiPart.addBodyPart(bodyPart);
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    public static BodyPart attach(String stringPath) throws MessagingException {
        log.info("currently attaching: {}", stringPath);
        if (stringPath.startsWith("http")) {
            return EmailAttachmentUtils.attachPdf(stringPath);
        } else if (Files.isRegularFile(Paths.get(stringPath))) {
            return EmailAttachmentUtils.attachFile(stringPath);
        } else if (Files.isDirectory(Paths.get(stringPath))) {
            return EmailAttachmentUtils.attachFolderAsZip(stringPath);
        } else {
            throw new IllegalArgumentException("Unsupported attachment: " + stringPath);
        }
    }

    public static BodyPart attachFile(String pathString) throws MessagingException {
        try {
            Path pp = Path.of(pathString);
            return toBodyPart(Files.readAllBytes(pp), pp.getFileName().toString(), "application/x-any");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static BodyPart attachFolderAsZip(String sourceDirPath) throws MessagingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Path pp = Paths.get(sourceDirPath);
        ZipOutputStream zs = new ZipOutputStream(baos);
        try {
            Files.walk(pp)
                .filter(path -> !Files.isDirectory(path))
                .forEach(path -> {
                    ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
                    log.info("zipping file: {}", pp.relativize(path));
                    try {
                        zs.putNextEntry(zipEntry);
                        Files.copy(path, zs);
                        zs.closeEntry();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return toBodyPart(baos.toByteArray(), pp.getFileName().toString() + ".zip", "application/zip");
    }

    public static BodyPart attachPdf(String url) throws MessagingException {
        try {
            URL sourceUrl = new URL(url);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            sourceUrl.openStream().transferTo(baos);
            return toBodyPart(baos.toByteArray(), sourceUrl.getHost(), "application/pdf");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static BodyPart toBodyPart(byte[] array, String name, String type) throws MessagingException {
        EmailSizeConstraintValidator.validateAttachmentSize(array);
        BodyPart messageBodyPart = new MimeBodyPart();
        DataSource source = new ByteArrayDataSource(array, type);
        try {
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(name);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return messageBodyPart;
    }
}
