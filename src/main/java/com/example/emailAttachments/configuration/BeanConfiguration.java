package com.example.emailAttachments.configuration;

import com.example.emailAttachments.EmailAttachmentsApplication;
import com.example.emailAttachments.domain.service.EmailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
@ComponentScan(basePackageClasses = EmailAttachmentsApplication.class)
public class BeanConfiguration {

    @Bean
    EmailService emailService(JavaMailSender javaMailSender) {
        return new EmailService(javaMailSender);
    }
}
