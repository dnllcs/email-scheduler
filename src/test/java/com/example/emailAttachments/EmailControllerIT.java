package com.example.emailAttachments;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmailControllerIT {

    @Autowired
    private TestRestTemplate testRestTemplate;
    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("springTest", "springTest"))
            .withPerMethodLifecycle(false);

    @Test
    void shouldSendEmail(@Value("${scheduled.email.recipient}") String recipient,
                         @Value("${scheduled.email.subject}") String subject,
                         @Value("${scheduled.email.body}") String body,
                         @Value("#{'${scheduled.email.attachments}'.split(',')}") List<String> attachments) {

        String attachmentsJson = attachments.stream()
                .map(attachment -> "\"" + attachment.replaceAll("\\\\", "\\\\\\\\") + "\"")
                .collect(Collectors.joining(",", "[", "]"));

        String payload = "{\"to\":\""+recipient+"\",\"subject\":\""+subject+"\",\"body\":\""+body+"\",\"attachments\":"+attachmentsJson+"}";
        System.out.println(payload);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(payload, headers);

        ResponseEntity<Void> response =  this.testRestTemplate.postForEntity("/v1/email/send", request, Void.class);

        assertEquals(200, response.getStatusCode().value());
    }
}
