package com.example.emailAttachments.infrastructure;

import com.example.emailAttachments.application.EmailRequest;
import com.example.emailAttachments.application.port.in.SendEmail;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@RequiredArgsConstructor
@Component
public class EmailScheduler {

    private final SendEmail emailService;
    Logger log = LoggerFactory.getLogger(EmailScheduler.class);
    @Value("${scheduled.email.recipient}")
    private String recipient;
    @Value("${scheduled.email.subject}")
    private String subject;
    @Value("${scheduled.email.body}")
    private String body;
    @Value("#{'${scheduled.email.attachments}'.split(',')}")
    private List<String> attachments;

    @Scheduled(cron = "${scheduled.email.cronExpression}")
    public void sendEmail() {
        emailService.send(new EmailRequest(recipient, subject, body, attachments));
        log.info("email sent on {}", new SimpleDateFormat("HH:mm:ss").format(new Date()));
    }
}
