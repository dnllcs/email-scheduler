package com.example.emailAttachments.domain.model;

import com.example.emailAttachments.infrastructure.EmailSizeConstraintValidator;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Attachment {
    private final String source;

    static Logger log = LoggerFactory.getLogger(Attachment.class);

    public Attachment(String attachmentAsString) {
        source = attachmentAsString;
    }

    public BodyPart attach() throws MessagingException {
        log.info("currently attaching: {}", source);
        if (source.startsWith("http")) {
            return attachPdf(source);
        } else if (Files.isRegularFile(Paths.get(source))) {
            return attachFile(source);
        } else if (Files.isDirectory(Paths.get(source))) {
            return attachFolderAsZip(source);
        } else {
            throw new IllegalArgumentException("Unsupported attachment: " + source);
        }
    }

    private static BodyPart attachFile(String pathString) {
        try {
            Path pp = Path.of(pathString);
            return toBodyPart(Files.readAllBytes(pp), pp.getFileName().toString(), "application/x-any");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static BodyPart attachFolderAsZip(String sourceDirPath) {
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
            zs.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return toBodyPart(baos.toByteArray(), pp.getFileName().toString() + ".zip", "application/zip");
    }

    private static BodyPart attachPdf(String url) {
        try {
            URL sourceUrl = new URL(url);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            sourceUrl.openStream().transferTo(baos);
            return toBodyPart(baos.toByteArray(), sourceUrl.getHost(), "application/pdf");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static BodyPart toBodyPart(byte[] array, String name, String type) {
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
